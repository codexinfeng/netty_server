package com.netty.server.netty;

import java.util.Map;
import java.util.concurrent.Callable;

import com.netty.server.core.RpcSystemConfig;
import com.netty.server.model.MessageRequest;
import com.netty.server.model.MessageResponse;

public class RecvInitializeTaskFacade {

	private MessageRequest request;
	private MessageResponse response;
	private Map<String, Object> handlerMap;
	private boolean isMetrics = RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_SUPPORT;
	private boolean jmxMetricsHash = RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_HASH_SUPPORT;

	public RecvInitializeTaskFacade(MessageRequest request,
			MessageResponse response, Map<String, Object> handlerMap) {
		this.request = request;
		this.response = response;
		this.handlerMap = handlerMap;
	}

	public Callable<Boolean> getTask() {
		return isMetrics ? getMetricsTask()
				: new MessageRecvInitializeTaskAdapter(request, response,
						handlerMap);
	}

	public Callable<Boolean> getMetricsTask() {
		return jmxMetricsHash ? new HashMessageRecvInitializeTask(request,
				response, handlerMap) : new MessageRecvInitializeTask(request,
				response, handlerMap);
	}
}
