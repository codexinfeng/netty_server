package com.netty.server.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;

public class MessageSendInitializeCallTask implements Callable<Boolean> {

	private EventLoopGroup eventLoopGroup = null;

	private InetSocketAddress serverAddress = null;

	private RpcSerializerProtocol protocol;

	MessageSendInitializeCallTask(EventLoopGroup eventLoopGroup,
			InetSocketAddress serverAddress, RpcSerializerProtocol protocol) {
		this.eventLoopGroup = eventLoopGroup;
		this.serverAddress = serverAddress;
		this.protocol = protocol;
	}

	@Override
	public Boolean call() throws Exception {
		Bootstrap b = new Bootstrap();
		b.group(eventLoopGroup).channel(NioSctpChannel.class)
				.option(ChannelOption.SO_KEEPALIVE, true);
		b.handler(new MessageSendChannelInitializer()
				.buildRpcSerializeProtocol(protocol));
		ChannelFuture future = b.connect(serverAddress);
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				if (future.isSuccess()) {
					MessageSendHandler handler = future.channel().pipeline()
							.get(MessageSendHandler.class);
					RpcServerLoader.getInstance()
							.setMessageSendHandler(handler);
				}

			}
		});
		return Boolean.TRUE;
	}
}
