package com.netty.server.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.netty.server.enums.RpcSerializerProtocol;
import com.netty.server.parallel.RpcThreadPool;

public class RpcServerLoader {

	private volatile static RpcServerLoader rpcServerLoader;

	private final static String DELIMITER = ":";

	@SuppressWarnings("unused")
	private RpcSerializerProtocol serializeProtocol = RpcSerializerProtocol.JDK_SERIALLZE;

	private final static int parallel = Runtime.getRuntime()
			.availableProcessors();
	private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(parallel);
	private static ListeningExecutorService threadPoolExecutor = MoreExecutors
			.listeningDecorator((ThreadPoolExecutor) (RpcThreadPool
					.getExecutor(16, -1)));
	private Lock lock = new ReentrantLock();
	private Condition connectStatus = lock.newCondition();
	private Condition handlerStatus = lock.newCondition();
	// V1�汾
	// private static ThreadPoolExecutor threadPoolExecutor =
	// (ThreadPoolExecutor) RpcThreadPool
	// .getExecutor(16, -1);
	// private Condition signal = lock.newCondition();
	private MessageSendHandler messageSendHandler = null;

	private RpcServerLoader() {
	}

	public static RpcServerLoader getInstance() {
		if (rpcServerLoader == null) {
			synchronized (RpcServerLoader.class) {
				if (rpcServerLoader == null) {
					rpcServerLoader = new RpcServerLoader();
				}
			}
		}
		return rpcServerLoader;
	}

	public void load(String serverAddress,
			RpcSerializerProtocol serializeProtocol) {
		String[] ipAddr = serverAddress.split(RpcServerLoader.DELIMITER);
		if (ipAddr.length == 2) {
			String host = ipAddr[0];
			int port = Integer.valueOf(ipAddr[1]);
			final InetSocketAddress remoteAddr = new InetSocketAddress(host,
					port);
			// threadPoolExecutor.submit(new MessageSendInitializeTask(
			// eventLoopGroup, remoteAddr, serializeProtocol));
			ListenableFuture<Boolean> listenableFuture = threadPoolExecutor
					.submit(new MessageSendInitializeTask(eventLoopGroup,
							remoteAddr, serializeProtocol));
			Futures.addCallback(listenableFuture,
					new FutureCallback<Boolean>() {

						@Override
						public void onSuccess(Boolean result) {
							try {
								lock.lock();
								if (messageSendHandler == null) {
									handlerStatus.await();
								}
								if (result == Boolean.TRUE
										&& messageSendHandler != null) {
									connectStatus.signalAll();
								}
							} catch (InterruptedException e) {
								Logger.getLogger(
										RpcServerLoader.class.getName()).log(
										Level.SEVERE, null, e);
							} finally {
								lock.unlock();
							}

						}

						@Override
						public void onFailure(Throwable t) {
							t.printStackTrace();
						}
					}, threadPoolExecutor);
		}
	}

	public void setMessageSendHandler(MessageSendHandler messageSendHandler) {
		try {
			lock.lock();
			this.messageSendHandler = messageSendHandler;
			handlerStatus.signal();
		} finally {
			lock.unlock();
		}
	}

	public MessageSendHandler getMessageSendHandler()
			throws InterruptedException {
		try {
			lock.lock();
			if (messageSendHandler == null) {
				connectStatus.await();
			}
			return messageSendHandler;
		} finally {
			lock.unlock();
		}
	}

	public void unLoad() {
		messageSendHandler.close();
		threadPoolExecutor.shutdown();
		eventLoopGroup.shutdownGracefully();
	}

	public void setSerializeProtocol(RpcSerializerProtocol serializeProtocol) {
		this.serializeProtocol = serializeProtocol;
	}
}
