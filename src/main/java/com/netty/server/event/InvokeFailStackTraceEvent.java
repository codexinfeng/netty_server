package com.netty.server.event;

import java.util.concurrent.atomic.AtomicLong;

import javax.management.AttributeChangeNotification;
import javax.management.Notification;

public class InvokeFailStackTraceEvent extends AbstractInvokeEventBus {

	private AtomicLong sequenceInvokeSuccNumber = new AtomicLong(0L);

	public InvokeFailStackTraceEvent() {
		super();
	}

	public InvokeFailStackTraceEvent(String moduleName, String methodName) {
		super(moduleName, methodName);
	}

	@Override
	public Notification buildNotification(Object oldValue, Object newValue) {
		return new AttributeChangeNotification(this,
				sequenceInvokeSuccNumber.incrementAndGet(),
				System.currentTimeMillis(), super.moduleName, super.methodName,
				ModuleEvent.INVOKE_FAIL_STACKTRACE_EVENT.toString(), oldValue,
				newValue);
	}

}
