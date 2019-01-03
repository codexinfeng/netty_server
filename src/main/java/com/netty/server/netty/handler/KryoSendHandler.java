package com.netty.server.netty.handler;

import io.netty.channel.ChannelPipeline;

import com.netty.server.netty.MessageSendHandler;
import com.netty.server.serialize.support.kryo.KryoCodeUtil;
import com.netty.server.serialize.support.kryo.KryoDecoder;
import com.netty.server.serialize.support.kryo.KryoEncoder;
import com.netty.server.serialize.support.kryo.KryoPoolFactory;

public class KryoSendHandler implements NettyRpcSendHandler {

	@Override
	public void handle(ChannelPipeline pipeline) {
		KryoCodeUtil util = new KryoCodeUtil(
				KryoPoolFactory.getKryoPoolInstance());
		pipeline.addLast(new KryoEncoder(util));
		pipeline.addLast(new KryoDecoder(util));
		pipeline.addLast(new MessageSendHandler());
	}

}
