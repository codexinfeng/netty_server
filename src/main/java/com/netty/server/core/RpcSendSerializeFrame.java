package com.netty.server.core;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import com.netty.server.serialize.support.MessageCodeUtil;
import com.netty.server.serialize.support.RpcSerializeFrame;
import com.netty.server.serialize.support.hessian.HessianCodeUtil;
import com.netty.server.serialize.support.hessian.HessianDecoder;
import com.netty.server.serialize.support.hessian.HessianEncoder;
import com.netty.server.serialize.support.kryo.KryoCodeUtil;
import com.netty.server.serialize.support.kryo.KryoDecoder;
import com.netty.server.serialize.support.kryo.KryoEncoder;
import com.netty.server.serialize.support.kryo.KryoPoolFactory;

public class RpcSendSerializeFrame implements RpcSerializeFrame {

	@Override
	public void select(RpcSerializerProtocol protocol, ChannelPipeline pipeline) {
		switch (protocol) {
		case JDK_SERIALLZE: {
			pipeline.addLast(new LengthFieldBasedFrameDecoder(
					Integer.MAX_VALUE, 0, MessageCodeUtil.MESSAGE_LENGTH, 0,
					MessageCodeUtil.MESSAGE_LENGTH));
			pipeline.addLast(new LengthFieldPrepender(
					MessageCodeUtil.MESSAGE_LENGTH));
			pipeline.addLast(new ObjectEncoder());
			pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE,
					ClassResolvers.weakCachingConcurrentResolver(this
							.getClass().getClassLoader())));
			pipeline.addLast(new MessageSendHandler());
			break;
		}
		case KRYO_SERIALIZE: {
			KryoCodeUtil util = new KryoCodeUtil(
					KryoPoolFactory.getKryoPoolInstance());
			pipeline.addLast(new KryoEncoder(util));
			pipeline.addLast(new KryoDecoder(util));
			pipeline.addLast(new MessageSendHandler());
			break;
		}
		case HESSIAN_SERIALIZE: {
			HessianCodeUtil util = new HessianCodeUtil();
			pipeline.addLast(new HessianEncoder(util));
			pipeline.addLast(new HessianDecoder(util));
			pipeline.addLast(new MessageSendHandler());
			break;
		}
		}
	}
}
