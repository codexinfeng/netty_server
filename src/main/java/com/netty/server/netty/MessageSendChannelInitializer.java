package com.netty.server.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import com.netty.server.enums.RpcSerializerProtocol;

public class MessageSendChannelInitializer extends
		ChannelInitializer<SocketChannel> {

	// V1�汾
	// ObjectDecoder �ײ�Ĭ�ϼ̳а�������� LengthFieldBaseFrameDecoder����ճ������
	// ��Ϣͷռ4���ֽ�
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
	// //
	// ���ǵ���������,����weakCachingConcurrentResolver�������,һ�����ʹ��cacheDisabled
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
