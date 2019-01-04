package com.netty.server.netty;

import io.netty.channel.ChannelPipeline;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import com.netty.server.enums.RpcSerializerProtocol;
import com.netty.server.netty.handler.HessianSendHandler;
import com.netty.server.netty.handler.JdkNativeSendHandler;
import com.netty.server.netty.handler.KryoSendHandler;
import com.netty.server.netty.handler.NettyRpcSendHandler;
import com.netty.server.netty.handler.ProtostuffSendHandler;
import com.netty.server.serialize.support.RpcSerializeFrame;

public class RpcSendSerializeFrame implements RpcSerializeFrame {

	private static ClassToInstanceMap<NettyRpcSendHandler> handler = MutableClassToInstanceMap
			.create();
	static {
		handler.putInstance(JdkNativeSendHandler.class,
				new JdkNativeSendHandler());
		handler.putInstance(KryoSendHandler.class, new KryoSendHandler());
		handler.putInstance(HessianSendHandler.class, new HessianSendHandler());
		handler.putInstance(ProtostuffSendHandler.class,
				new ProtostuffSendHandler());
	}

	@Override
	public void select(RpcSerializerProtocol protocol, ChannelPipeline pipeline) {
		switch (protocol) {
		case JDK_SERIALLZE: {
			handler.getInstance(JdkNativeSendHandler.class).handle(pipeline);
			break;
		}
		case KRYO_SERIALIZE: {
			handler.getInstance(KryoSendHandler.class).handle(pipeline);
			break;
		}
		case HESSIAN_SERIALIZE: {
			handler.getInstance(HessianSendHandler.class).handle(pipeline);
			break;
		}
		case PROTOSTUFFSERIALIZE: {
			handler.getInstance(ProtostuffSendHandler.class).handle(pipeline);
			break;
		}
		default: {
			break;
		}
		}

	}

	// @Override
	// public void select(RpcSerializerProtocol protocol, ChannelPipeline
	// pipeline) {
	// switch (protocol) {
	// case JDK_SERIALLZE: {
	// pipeline.addLast(new LengthFieldBasedFrameDecoder(
	// Integer.MAX_VALUE, 0, MessageCodeUtil.MESSAGE_LENGTH, 0,
	// MessageCodeUtil.MESSAGE_LENGTH));
	// pipeline.addLast(new LengthFieldPrepender(
	// MessageCodeUtil.MESSAGE_LENGTH));
	// pipeline.addLast(new ObjectEncoder());
	// pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE,
	// ClassResolvers.weakCachingConcurrentResolver(this
	// .getClass().getClassLoader())));
	// pipeline.addLast(new MessageSendHandler());
	// break;
	// }
	// case KRYO_SERIALIZE: {
	// KryoCodeUtil util = new KryoCodeUtil(
	// KryoPoolFactory.getKryoPoolInstance());
	// pipeline.addLast(new KryoEncoder(util));
	// pipeline.addLast(new KryoDecoder(util));
	// pipeline.addLast(new MessageSendHandler());
	// break;
	// }
	// case HESSIAN_SERIALIZE: {
	// HessianCodeUtil util = new HessianCodeUtil();
	// pipeline.addLast(new HessianEncoder(util));
	// pipeline.addLast(new HessianDecoder(util));
	// pipeline.addLast(new MessageSendHandler());
	// break;
	// }
	// }
	// }
}
