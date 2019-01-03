package com.netty.server.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

import com.netty.server.enums.RpcSerializerProtocol;

public class MessageSendInitializeTask implements Runnable {

	private EventLoopGroup eventLoopGroup = null;
	private InetSocketAddress serverAddress = null;
	@SuppressWarnings("unused")
	private RpcSerializerProtocol protocol;

	MessageSendInitializeTask(EventLoopGroup eventLoopGroup,
			InetSocketAddress serverAddress, RpcSerializerProtocol protocol) {
		this.eventLoopGroup = eventLoopGroup;
		this.serverAddress = serverAddress;
		this.protocol = protocol;
	}

	@Override
	public void run() {
		Bootstrap b = new Bootstrap();
		b.group(eventLoopGroup).channel(NioSocketChannel.class)
				.option(ChannelOption.SO_KEEPALIVE, true);
		b.handler(new MessageSendChannelInitializer());

		ChannelFuture channelFuture = b.connect(serverAddress);
		channelFuture.addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				if (future.isSuccess()) {
					MessageSendHandler handler = channelFuture.channel()
							.pipeline().get(MessageSendHandler.class);
					RpcServerLoader.getInstance()
							.setMessageSendHandler(handler);
				}
			}
		});

	}
}
