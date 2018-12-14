package com.netty.server.core;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class MessageSendChannelInitializer extends
		ChannelInitializer<SocketChannel> {

	// V1版本
	// ObjectDecoder 底层默认继承半包解码器 LengthFieldBaseFrameDecoder处理粘包问题
	// 消息头占4个字节
	// public final static int MESSAGE_LENGTH = 4;
	//
	// @Override
	// protected void initChannel(SocketChannel ch) throws Exception {
	// ChannelPipeline pipeline = ch.pipeline();
	// pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0,
	// MessageSendChannelInitializer.MESSAGE_LENGTH, 0,
	// MessageSendChannelInitializer.MESSAGE_LENGTH));
	// pipeline.addLast(new LengthFieldPrepender(
	// MessageSendChannelInitializer.MESSAGE_LENGTH));
	// pipeline.addLast(new ObjectEncoder());
	// // 考虑到并发性能,采用weakCachingConcurrentResolver缓存策略,一般情况使用cacheDisabled
	// pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE,
	// ClassResolvers.weakCachingConcurrentResolver(this.getClass()
	// .getClassLoader())));
	// pipeline.addLast(new MessageSendHandler());
	//
	// }

	private RpcSerializerProtocol protocol;

	private RpcSendSerializeFrame frame = new RpcSendSerializeFrame();

	MessageSendChannelInitializer buildRpcSerializeProtocol(
			RpcSerializerProtocol protocol) {
		this.protocol = protocol;
		return this;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		frame.select(protocol, pipeline);
	}
}
