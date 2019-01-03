package com.netty.server.netty.handler;

import io.netty.channel.ChannelPipeline;

import java.util.Map;

import com.netty.server.netty.MessageRecvHandler;
import com.netty.server.serialize.support.protostuff.ProtostuffCodecUtil;
import com.netty.server.serialize.support.protostuff.ProtostuffDecoder;
import com.netty.server.serialize.support.protostuff.ProtostuffEncoder;

public class ProtostuffRecvHandler implements NettyRpcRecvHandler {

	@Override
	public void handle(Map<String, Object> handlerMap, ChannelPipeline pipeline) {
		ProtostuffCodecUtil util = new ProtostuffCodecUtil();
		util.setRpcDirect(true);
		pipeline.addLast(new ProtostuffEncoder(util));
		pipeline.addLast(new ProtostuffDecoder(util));
		pipeline.addLast(new MessageRecvHandler(handlerMap));
	}

}
