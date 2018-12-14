package com.netty.server.core;

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
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.netty.server.model.MessageKeyVal;
import com.netty.server.model.MessageRequest;
import com.netty.server.model.MessageResponse;

public class MessageRecvExecutor implements ApplicationContextAware,
		InitializingBean {

	// V1 版本

	//

	//
	// private static ThreadPoolExecutor threadPoolExecutor;
	//
	// public MessageRecvExecutor(String serverAddress) {
	// this.serverAddress = serverAddress;
	// }
	//
	// public static void submit(Runnable task) {
	// if (threadPoolExecutor == null) {
	// synchronized (MessageRecvExecutor.class) {
	// if (threadPoolExecutor == null) {
	// threadPoolExecutor = (ThreadPoolExecutor) RpcThreadPool
	// .getExecutor(16, -1);
	// }
	// }
	// }
	// threadPoolExecutor.submit(task);
	// }
	private String serverAddress;

	private final static String DELIMITER = ":";
	private Map<String, Object> handlerMap = new ConcurrentHashMap<>();
	private static ListeningExecutorService threadPoolExecutor;
	// 默认JDK本地序列号协议
	private RpcSerializerProtocol serializerProtocol = RpcSerializerProtocol.JDK_SERIALLZE;

	public MessageRecvExecutor(String serverAddress, String serializeProtocol) {
		this.serverAddress = serverAddress;
		this.serializerProtocol = Enum.valueOf(RpcSerializerProtocol.class,
				serializeProtocol);
	}

	public static void submit(Callable<Boolean> task,
			ChannelHandlerContext ctx, MessageRequest request,
			MessageResponse response) {
		if (threadPoolExecutor == null) {
			synchronized (MessageRecvExecutor.class) {
				if (threadPoolExecutor == null) {
					threadPoolExecutor = MoreExecutors
							.listeningDecorator((ThreadPoolExecutor) RpcThreadPool
									.getExecutor(16, -1));
				}
			}
		}
		ListenableFuture<Boolean> listenableFuture = threadPoolExecutor
				.submit(task);
		// Netty服务端把计算结果异步返回
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

	@Override
	public void afterPropertiesSet() throws Exception {
		// netty的线程池模型设置为主从线程池模型,应对高并发
		// netty还支持单线程,多网络IO
		ThreadFactory threadFactory = new NameThreadFactory(
				"NettyRPC ThreadFactory");
		// java 虚拟机可用的处理器
		int parallel = Runtime.getRuntime().availableProcessors() * 2;

		EventLoopGroup boss = new NioEventLoopGroup();
		EventLoopGroup worker = new NioEventLoopGroup(parallel, threadFactory,
				SelectorProvider.provider());
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap
					.group(boss, worker)
					.channel(NioServerSocketChannel.class)
					.childHandler(
							new MessageRecvChannelInitializer(handlerMap)
									.buildRpcSerializeProtocol(serializerProtocol))
					.option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true);
			String[] ipAddr = serverAddress
					.split(MessageRecvExecutor.DELIMITER);
			if (ipAddr.length == 2) {
				String host = ipAddr[0];
				int port = Integer.valueOf(ipAddr[1]);
				ChannelFuture future = bootstrap.bind(host, port).sync();
				System.out.printf(
						"Netty RPC Server start success ip:%s port:%d\n", host,
						port);
				// 表示异步等待
				future.channel().closeFuture().sync();
			} else {
				System.out.println("Netty RPC server start fail");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			worker.shutdownGracefully();
			boss.shutdownGracefully();
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

}
