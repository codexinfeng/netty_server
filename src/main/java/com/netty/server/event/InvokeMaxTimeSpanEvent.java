package com.netty.server.event;

import java.util.concurrent.atomic.AtomicLong;

import javax.management.AttributeChangeNotification;
import javax.management.Notification;

public class InvokeMaxTimeSpanEvent extends AbstractInvokeEventBus {

	private AtomicLong sequenceInvokeSuccNumber = new AtomicLong(0L);

	public InvokeMaxTimeSpanEvent() {
		super();
	}

	public InvokeMaxTimeSpanEvent(String moduleName, String methodName) {
		super(moduleName, methodName);
	}

	@Override
	public Notification buildNotification(Object oldValue, Object newValue) {
		return new AttributeChangeNotification(this,
				sequenceInvokeSuccNumber.incrementAndGet(),
				System.currentTimeMillis(), super.moduleName, super.methodName,
				ModuleEvent.INVOKE_MAX_TIMESPAN_EVENT.toString(), oldValue,
				newValue);
	}

}
