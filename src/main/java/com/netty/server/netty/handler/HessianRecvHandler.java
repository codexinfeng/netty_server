package com.netty.server.netty.handler;

import io.netty.channel.ChannelPipeline;

import com.netty.server.netty.MessageSendHandler;
import com.netty.server.serialize.support.hessian.HessianCodeUtil;
import com.netty.server.serialize.support.hessian.HessianDecoder;
import com.netty.server.serialize.support.hessian.HessianEncoder;

public class HessianRecvHandler implements NettyRpcSendHandler {

	@Override
	public void handle(ChannelPipeline pipeline) {
		HessianCodeUtil util = new HessianCodeUtil();
		pipeline.addLast(new HessianEncoder(util));
		pipeline.addLast(new HessianDecoder(util));
		pipeline.addLast(new MessageSendHandler());
	}

}
