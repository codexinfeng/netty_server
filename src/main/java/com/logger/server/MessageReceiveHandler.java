package com.logger.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MessageReceiveHandler extends SimpleChannelInboundHandler<Object> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object obj)
			throws Exception {
		String message = obj.toString();
		System.out
				.println(ctx.channel().remoteAddress() + " 传递过来的消息: " + message);
		// 回传消息
		ctx.writeAndFlush("Receive your msg");

	}

	/**
	 * 启动时触发
	 */

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.print("RemoteAddress :" + ctx.channel().remoteAddress()
				+ "active");
		super.channelActive(ctx);
	}

}
