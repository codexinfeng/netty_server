package com.netty.server.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.netty.server.core.RpcSystemConfig;
import com.netty.server.enums.RpcSerializerProtocol;

public class MessageSendInitializeTask implements Callable<Boolean> {

	private EventLoopGroup eventLoopGroup = null;
	private InetSocketAddress serverAddress = null;
	private RpcSerializerProtocol protocol;

	MessageSendInitializeTask(EventLoopGroup eventLoopGroup,
			InetSocketAddress serverAddress, RpcSerializerProtocol protocol) {
		this.eventLoopGroup = eventLoopGroup;
		this.serverAddress = serverAddress;
		this.protocol = protocol;
	}

	// @Override
	// public void run() {
	// Bootstrap b = new Bootstrap();
	// b.group(eventLoopGroup).channel(NioSocketChannel.class)
	// .option(ChannelOption.SO_KEEPALIVE, true);
	// b.handler(new MessageSendChannelInitializer());
	//
	// ChannelFuture channelFuture = b.connect(serverAddress);
	// channelFuture.addListener(new ChannelFutureListener() {
	//
	// @Override
	// public void operationComplete(ChannelFuture future)
	// throws Exception {
	// if (future.isSuccess()) {
	// MessageSendHandler handler = channelFuture.channel()
	// .pipeline().get(MessageSendHandler.class);
	// RpcServerLoader.getInstance()
	// .setMessageSendHandler(handler);
	// }
	// }
	// });
	//
	// }

	@Override
	public Boolean call() throws Exception {
		Bootstrap b = new Bootstrap();
		b.group(eventLoopGroup)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.remoteAddress(serverAddress)
				.handler(
						new MessageSendChannelInitializer()
								.buildRpcSerializeProtocol(protocol));
		ChannelFuture future = b.connect();
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				if (future.isSuccess()) {
					MessageSendHandler handler = future.channel().pipeline()
							.get(MessageSendHandler.class);
					RpcServerLoader.getInstance()
							.setMessageSendHandler(handler);
				} else {
					eventLoopGroup.schedule(
							new Runnable() {
								@Override
								public void run() {
									System.out
											.println("NettyRPC server is down,start to reconnecting to: "
													+ serverAddress
															.getAddress()
															.getHostAddress()
													+ ':'
													+ serverAddress.getPort());
									try {
										call();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}

							},
							RpcSystemConfig.SYSTEM_PROPERTY_CLIENT_RECONNECT_DELAY,
							TimeUnit.SECONDS);
				}
			}
		});
		return Boolean.TRUE;
	}
}
