package com.netty.server.netty;

import io.netty.channel.ChannelPipeline;

import java.util.Map;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import com.netty.server.enums.RpcSerializerProtocol;
import com.netty.server.netty.handler.HessianRecvHandler;
import com.netty.server.netty.handler.JdkNativeRecvHandler;
import com.netty.server.netty.handler.KryoRecvHandler;
import com.netty.server.netty.handler.NettyRpcRecvHandler;
import com.netty.server.netty.handler.ProtostuffRecvHandler;
import com.netty.server.serialize.support.RpcSerializeFrame;

public class RpcRecvSerializeFrame implements RpcSerializeFrame {

	private Map<String, Object> handlerMap = null;

	public RpcRecvSerializeFrame(Map<String, Object> handlerMap) {
		this.handlerMap = handlerMap;
	}

	private static ClassToInstanceMap<NettyRpcRecvHandler> handler = MutableClassToInstanceMap
			.create();
	static {
		handler.putInstance(JdkNativeRecvHandler.class,
				new JdkNativeRecvHandler());
		handler.putInstance(KryoRecvHandler.class, new KryoRecvHandler());
		handler.putInstance(HessianRecvHandler.class, new HessianRecvHandler());
		handler.putInstance(ProtostuffRecvHandler.class,
				new ProtostuffRecvHandler());
	}

	// private static Map<Class<?>, NettyRpcRecvHandler> clazzMap = new
	// HashMap<>();
	// static {
	// clazzMap.put(JdkNativeRecvHandler.class, new JdkNativeRecvHandler());
	// }
	@Override
	public void select(RpcSerializerProtocol protocol, ChannelPipeline pipeline) {
		switch (protocol) {
		case JDK_SERIALLZE: {
			handler.getInstance(JdkNativeRecvHandler.class).handle(handlerMap,
					pipeline);
			break;
		}
		case KRYO_SERIALIZE: {
			handler.getInstance(KryoRecvHandler.class).handle(handlerMap,
					pipeline);
			break;
		}
		case HESSIAN_SERIALIZE: {
			handler.getInstance(HessianRecvHandler.class).handle(handlerMap,
					pipeline);
			break;
		}
		case PROTOSTUFFSERIALIZE: {
			handler.getInstance(ProtostuffRecvHandler.class).handle(handlerMap,
					pipeline);
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
	// pipeline.addLast(new MessageRecvHandler(handlerMap));
	// break;
	// }
	// case KRYO_SERIALIZE: {
	// KryoCodeUtil util = new KryoCodeUtil(
	// KryoPoolFactory.getKryoPoolInstance());
	// pipeline.addLast(new KryoEncoder(util));
	// pipeline.addLast(new KryoDecoder(util));
	// pipeline.addLast(new MessageRecvHandler(handlerMap));
	// break;
	// }
	// case HESSIAN_SERIALIZE: {
	// HessianCodeUtil util = new HessianCodeUtil();
	// pipeline.addLast(new HessianEncoder(util));
	// pipeline.addLast(new HessianDecoder(util));
	// pipeline.addLast(new MessageRecvHandler(handlerMap));
	// break;
	// }
	// }
	//
	// }

}
