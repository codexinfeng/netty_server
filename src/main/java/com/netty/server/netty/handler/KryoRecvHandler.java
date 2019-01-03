package com.netty.server.netty.handler;

import io.netty.channel.ChannelPipeline;

import java.util.Map;

import com.netty.server.netty.MessageRecvHandler;
import com.netty.server.serialize.support.kryo.KryoCodeUtil;
import com.netty.server.serialize.support.kryo.KryoDecoder;
import com.netty.server.serialize.support.kryo.KryoEncoder;
import com.netty.server.serialize.support.kryo.KryoPoolFactory;

public class KryoRecvHandler implements NettyRpcRecvHandler {

	@Override
	public void handle(Map<String, Object> handlerMap, ChannelPipeline pipeline) {
		KryoCodeUtil util = new KryoCodeUtil(
				KryoPoolFactory.getKryoPoolInstance());
		pipeline.addLast(new KryoEncoder(util));
		pipeline.addLast(new KryoDecoder(util));
		pipeline.addLast(new MessageRecvHandler(handlerMap));
	}
}
