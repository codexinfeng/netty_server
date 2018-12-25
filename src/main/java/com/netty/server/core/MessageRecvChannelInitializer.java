package com.netty.server.core;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.util.Map;

public class MessageRecvChannelInitializer extends
		ChannelInitializer<SocketChannel> {
	private RpcSerializerProtocol protocol;

	private RpcRecvSerializeFrame frame = null;

	MessageRecvChannelInitializer buildRpcSerializeProtocol(
			RpcSerializerProtocol protocol) {
		this.protocol = protocol;
		return this;
	}

	MessageRecvChannelInitializer(Map<String, Object> handlerMap) {
		frame = new RpcRecvSerializeFrame(handlerMap);
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		frame.select(protocol, pipeline);
	}
}
