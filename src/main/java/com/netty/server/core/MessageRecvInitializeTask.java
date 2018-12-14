package com.netty.server.core;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.lang3.reflect.MethodUtils;

import com.netty.server.model.MessageRequest;
import com.netty.server.model.MessageResponse;

public class MessageRecvInitializeTask implements Runnable {

	private MessageRequest request = null;

	private MessageResponse response = null;

	private Map<String, Object> handlerMap = null;

	private ChannelHandlerContext ctx = null;

	public MessageRecvInitializeTask(MessageRequest request,
			MessageResponse response, Map<String, Object> handlerMap,
			ChannelHandlerContext ctx) {
		this.request = request;
		this.response = response;
		this.handlerMap = handlerMap;
		this.ctx = ctx;
	}

	@Override
	public void run() {
		response.setMessageId(request.getMessageId());
		try {
			Object result = reflect(request);
			response.setResultDesc(result);
		} catch (Exception e) {
			response.setError(e.getMessage());
			e.printStackTrace();
			System.err.println("RPC Server invoke error!\n");
		}

		ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				System.out.println("RPC Server Send message-id response:"
						+ response.getMessageId() + " return:"
						+ response.getResultDesc().toString());
			}
		});

	}

	private Object reflect(MessageRequest request)
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		String className = request.getClassName();
		Object serviceBean = handlerMap.get(className);
		String methodName = request.getMethodName();
		Object[] params = request.getParameterVal();
		return MethodUtils.invokeMethod(serviceBean, methodName, params);
	}

	public MessageRequest getRequest() {
		return request;
	}

	public void setRequest(MessageRequest request) {
		this.request = request;
	}

	public MessageResponse getResponse() {
		return response;
	}

}
