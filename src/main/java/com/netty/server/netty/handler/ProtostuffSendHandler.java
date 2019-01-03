package com.netty.server.netty.handler;

import io.netty.channel.ChannelPipeline;

import com.netty.server.netty.MessageSendHandler;
import com.netty.server.serialize.support.protostuff.ProtostuffCodecUtil;
import com.netty.server.serialize.support.protostuff.ProtostuffDecoder;
import com.netty.server.serialize.support.protostuff.ProtostuffEncoder;

public class ProtostuffSendHandler implements NettyRpcSendHandler {

	@Override
	public void handle(ChannelPipeline pipeline) {
		ProtostuffCodecUtil util = new ProtostuffCodecUtil();
		util.setRpcDirect(true);
		pipeline.addLast(new ProtostuffEncoder(util));
		pipeline.addLast(new ProtostuffDecoder(util));
		pipeline.addLast(new MessageSendHandler());
	}

}
