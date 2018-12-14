package com.netty.server.core;

import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.reflect.MethodUtils;

import com.netty.server.model.MessageRequest;
import com.netty.server.model.MessageResponse;

public class MessageRecvInitializeCallTask implements Callable<Boolean> {

	private MessageRequest request = null;
	private MessageResponse response = null;
	private Map<String, Object> handlerMap = null;

	public MessageResponse getResponse() {
		return response;
	}

	public MessageRequest getRequest() {
		return request;
	}

	public void setRequest(MessageRequest request) {
		this.request = request;
	}

	MessageRecvInitializeCallTask(MessageRequest request,
			MessageResponse response, Map<String, Object> handlerMap) {
		this.request = request;
		this.response = response;
		this.handlerMap = handlerMap;
	}

	@Override
	public Boolean call() throws Exception {
		response.setMessageId(request.getMessageId());
		try {
			Object result = reflect(request);
			response.setResultDesc(result);
			return Boolean.TRUE;
		} catch (Throwable e) {
			response.setError(e.toString());
			e.printStackTrace();
			System.err.printf("RPC Server invoke error!\n");
			return Boolean.FALSE;
		}
	}

	private Object reflect(MessageRequest request) throws Throwable {
		String className = request.getClassName();
		Object serviceBean = handlerMap.get(className);
		String methodName = request.getMethodName();
		Object[] parameters = request.getParameterVal();
		return MethodUtils.invokeMethod(serviceBean, methodName, parameters);
	}

}
