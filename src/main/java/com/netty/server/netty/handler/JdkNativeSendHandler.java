package com.netty.server.netty.handler;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import com.netty.server.netty.MessageSendHandler;
import com.netty.server.serialize.support.MessageCodeUtil;

public class JdkNativeSendHandler implements NettyRpcSendHandler {

	@Override
	public void handle(ChannelPipeline pipeline) {
		pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0,
				MessageCodeUtil.MESSAGE_LENGTH, 0,
				MessageCodeUtil.MESSAGE_LENGTH));
		pipeline.addLast(new LengthFieldPrepender(
				MessageCodeUtil.MESSAGE_LENGTH));
		pipeline.addLast(new ObjectEncoder());
		pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE,
				ClassResolvers.weakCachingConcurrentResolver(this.getClass()
						.getClassLoader())));
		pipeline.addLast(new MessageSendHandler());

	}

}
