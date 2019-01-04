package com.netty.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.netty.server.compiler.AccessAdaptiveProvider;
import com.netty.server.core.RpcSystemConfig;
import com.netty.server.enums.RpcSerializerProtocol;
import com.netty.server.jmx.ModuleMetricsHandler;
import com.netty.server.model.MessageKeyVal;
import com.netty.server.model.MessageRequest;
import com.netty.server.model.MessageResponse;
import com.netty.server.parallel.NameThreadFactory;
import com.netty.server.parallel.RpcThreadPool;
import com.netty.server.resolver.ApiEchoResolver;
import com.netty.server.view.AbilityDetailProvider;

public class MessageRecvExecutor implements ApplicationContextAware {

	private String serverAddress;
	private int echoApiPort;
	private RpcSerializerProtocol serializerProtocol = RpcSerializerProtocol.JDK_SERIALLZE;
	private static final String DELIMITER = ":";
	private static final int PARALLEL = RpcSystemConfig.PARALLEL * 2;
	private static int threadNums = RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_THREAD_NUMS;
	private static int queueNums = RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_QUEUE_NAME;
	private static volatile ListeningExecutorService threadPoolExecutor;
	private Map<String, Object> handlerMap = new ConcurrentHashMap<>();
	private int numberOfEchoThreadsPool = 1;
	ThreadFactory threadFactory = new NameThreadFactory(
			"NettyRpc ThreadFactory");
	EventLoopGroup boss = new NioEventLoopGroup();
	EventLoopGroup worker = new NioEventLoopGroup(PARALLEL, threadFactory,
			SelectorProvider.provider());

	private MessageRecvExecutor() {
		handlerMap.clear();
		register();
	}

	private static class MessageRecvExecutorHolder {
		static final MessageRecvExecutor INSTANCE = new MessageRecvExecutor();
	}

	public static MessageRecvExecutor getInstance() {
		return MessageRecvExecutorHolder.INSTANCE;
	}

	private void register() {
		handlerMap.put(RpcSystemConfig.RPC_COMPILER_SPI_ATTR,
				new AccessAdaptiveProvider());
		handlerMap.put(RpcSystemConfig.RPC_ABILITY_DETAIL_SPI_STTR,
				new AbilityDetailProvider());
	}

	public MessageRecvExecutor(String serverAddress, String protocol) {
		this.serverAddress = serverAddress;
		this.serializerProtocol = Enum.valueOf(RpcSerializerProtocol.class,
				protocol);

	}

	public static void submit(Callable<Boolean> task,
			ChannelHandlerContext ctx, MessageRequest request,
			MessageResponse response) {
		if (threadPoolExecutor == null) {
			synchronized (MessageRecvExecutor.class) {
				if (threadPoolExecutor == null) {
					threadPoolExecutor = MoreExecutors
							.listeningDecorator((ThreadPoolExecutor) (RpcSystemConfig
									.isMonitorServerSupport() ? RpcThreadPool
									.getExecutorWithJmx(threadNums, queueNums)
									: RpcThreadPool.getExecutor(threadNums,
											queueNums)));
				}
			}
		}
		ListenableFuture<Boolean> listenableFuture = threadPoolExecutor
				.submit(task);
		Futures.addCallback(listenableFuture, new FutureCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				ctx.writeAndFlush(response).addListener(
						new ChannelFutureListener() {
							@Override
							public void operationComplete(ChannelFuture future)
									throws Exception {
								System.out
										.println("RPC Server Send message-id respone:"
												+ request.getMessageId());
							}
						});
			}

			@Override
			public void onFailure(Throwable t) {
				t.printStackTrace();
			}
		}, threadPoolExecutor);
	}

	public Map<String, Object> getHandlerMap() {
		return handlerMap;
	}

	public void setHandlerMap(Map<String, Object> handlerMap) {
		this.handlerMap = handlerMap;
	}

	public void start() {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap
				.group(boss, worker)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true)
				.childHandler(
						new MessageRecvChannelInitializer(handlerMap)
								.buildRpcSerializeProtocol(serializerProtocol));
		String[] ipAddr = serverAddress.split(MessageRecvExecutor.DELIMITER);
		if (ipAddr.length == RpcSystemConfig.IPADDR_OPRT_ARRAY_LENGTH) {
			final String host = ipAddr[0];
			final int port = Integer.parseInt(ipAddr[1]);
			ChannelFuture future = null;
			try {
				// 绑定主机和端口号
				future = bootstrap.bind(host, port).sync();
				future.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture channelFuture)
							throws Exception {
						//成功后开启监控接口
						if (channelFuture.isSuccess()) {
							final ExecutorService executor = Executors
									.newFixedThreadPool(numberOfEchoThreadsPool);
							ExecutorCompletionService<Boolean> completionService = new ExecutorCompletionService<>(
									executor);
							completionService.submit(new ApiEchoResolver(host,
									echoApiPort));
							System.out
									.printf("[author tangjie] Netty RPC Server start success!\nip:%s\nport:%d\nprotocol:%s\nstart-time:%s\njmx-invoke-metrics:%s\n\n",
											host,
											port,
											serializerProtocol,
											ModuleMetricsHandler.getStartTime(),
											(RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_SUPPORT ? "open"
													: "close"));
							channelFuture.channel().closeFuture().sync()
									.addListener(new ChannelFutureListener() {
										@Override
										public void operationComplete(
												ChannelFuture future)
												throws Exception {
											executor.shutdown();
										}
									});

						}
					}
				});

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.err.println("Netty Server failed");
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {

		try {
			// Class<?> messageClazz = Class
			// .forName("com.netty.server.model.MessageKeyVal");
			MessageKeyVal keyVal = (MessageKeyVal) ctx.getBean("rpcbean");
			Map<String, Object> rpcServiceObject = keyVal.getMessageKeyVal();
			Set<Map.Entry<String, Object>> s = rpcServiceObject.entrySet();
			Iterator<Map.Entry<String, Object>> it = s.iterator();
			Map.Entry<String, Object> entry;
			while (it.hasNext()) {
				entry = it.next();
				handlerMap.put(entry.getKey(), entry.getValue());
			}
		} catch (Exception e) {
			java.util.logging.Logger.getLogger(
					MessageRecvExecutor.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}

	public void stop() {
		worker.shutdownGracefully();
		boss.shutdownGracefully();
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public int getEchoApiPort() {
		return echoApiPort;
	}

	public void setEchoApiPort(int echoApiPort) {
		this.echoApiPort = echoApiPort;
	}

	public RpcSerializerProtocol getSerializerProtocol() {
		return serializerProtocol;
	}

	public void setSerializerProtocol(RpcSerializerProtocol serializerProtocol) {
		this.serializerProtocol = serializerProtocol;
	}

}
