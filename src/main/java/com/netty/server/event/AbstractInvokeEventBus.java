package com.netty.server.event;

import javax.management.Notification;

import com.netty.server.jmx.ModuleMetricsHandler;

public abstract class AbstractInvokeEventBus {

	public enum ModuleEvent {
		INVOKE_EVENT, INVOKE_SUCC_EVENT, INVOKE_TIMESPAN_EVENT, INVOKE_MAX_TIMESPAN_EVENT, INVOKE_MIN_TIMESPAN_EVENT, INVOKE_FILTER_EVENT, INVOKE_FAIL_EVENT, INVOKE_FAIL_STACKTRACE_EVENT
	}

	protected String moduleName;
	protected String methodName;
	protected ModuleMetricsHandler handler;

	public AbstractInvokeEventBus() {
	}

	public AbstractInvokeEventBus(String moduleName, String methodName) {
		this.moduleName = moduleName;
		this.methodName = methodName;
	}

	public abstract Notification buildNotification(Object oldValue,
			Object newValue);

	public void notify(Object oldValue, Object newValue) {
		Notification notification = buildNotification(oldValue, newValue);
		handler.sendNotification(notification);
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public ModuleMetricsHandler getHandler() {
		return handler;
	}

	public void setHandler(ModuleMetricsHandler handler) {
		this.handler = handler;
	}

}
