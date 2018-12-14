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
/**
 * V1�汾 // ObjectDecoder �ײ�Ĭ�ϼ̳а��������LengthFieldBasedFrameDecoder����ճ�������ʱ�� //
 * ��Ϣͷ��ʼ��Ϊ�����ֶΣ�ռ��4���ֽڡ�������ڱ��ּ��ݵĿ��� final public static int MESSAGE_LENGTH = 4;
 * private Map<String, Object> handlerMap = null;
 * 
 * MessageRecvChannelInitializer(Map<String, Object> handlerMap) {
 * this.handlerMap = handlerMap; }
 * 
 * protected void initChannel(SocketChannel socketChannel) throws Exception {
 * ChannelPipeline pipeline = socketChannel.pipeline(); //
 * ObjectDecoder�Ļ�����������LengthFieldBasedFrameDecoder�ı��ĸ�ʽ���ּ���
 * ����Ϊ�ײ�ĸ���LengthFieldBasedFrameDecoder // �ĳ�ʼ��������Ϊsuper(maxObjectSize, 0, 4, 0,
 * 4); pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0,
 * MessageRecvChannelInitializer.MESSAGE_LENGTH, 0,
 * MessageRecvChannelInitializer.MESSAGE_LENGTH)); //
 * ����LengthFieldPrepender�����ObjectDecoder��Ϣ����ͷ pipeline.addLast(new
 * LengthFieldPrepender( MessageRecvChannelInitializer.MESSAGE_LENGTH));
 * pipeline.addLast(new ObjectEncoder()); //
 * ���ǵ��������ܣ�����weakCachingConcurrentResolver������ԡ�һ�����ʹ��:cacheDisabled����
 * pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE,
 * ClassResolvers.weakCachingConcurrentResolver(this.getClass()
 * .getClassLoader()))); pipeline.addLast(new MessageRecvHandler(handlerMap));
 * }**
 *
 */

