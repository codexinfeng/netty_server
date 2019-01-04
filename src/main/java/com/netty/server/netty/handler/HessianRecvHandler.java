package com.netty.server.netty.handler;

import io.netty.channel.ChannelPipeline;

import java.util.Map;

import com.netty.server.netty.MessageRecvHandler;
import com.netty.server.serialize.support.hessian.HessianCodeUtil;
import com.netty.server.serialize.support.hessian.HessianDecoder;
import com.netty.server.serialize.support.hessian.HessianEncoder;

public class HessianRecvHandler implements NettyRpcRecvHandler {

	@Override
	public void handle(Map<String, Object> handlerMap, ChannelPipeline pipeline) {
		HessianCodeUtil util = new HessianCodeUtil();
		pipeline.addLast(new HessianEncoder(util));
		pipeline.addLast(new HessianDecoder(util));
		pipeline.addLast(new MessageRecvHandler(handlerMap));
	}

}
