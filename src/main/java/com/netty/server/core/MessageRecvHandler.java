package com.netty.server.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;

import com.netty.server.model.MessageRequest;
import com.netty.server.model.MessageResponse;

public class MessageRecvHandler extends ChannelInboundHandlerAdapter {

	private final Map<String, Object> handlerMap;

	public MessageRecvHandler(Map<String, Object> handlerMap) {
		this.handlerMap = handlerMap;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		MessageRequest request = (MessageRequest) msg;
		MessageResponse response = new MessageResponse();
		// MessageRecvInitializeTask recvTask = new MessageRecvInitializeTask(
		// request, response, handlerMap, ctx);
		// MessageRecvExecutor.submit(recvTask);
		// MessageRecvExecutor.submit(task, ctx, request, response);

		MessageRecvInitializeCallTask recvTask = new MessageRecvInitializeCallTask(
				request, response, handlerMap);
		MessageRecvExecutor.submit(recvTask, ctx, request, response);
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		ctx.close();
	}
}
