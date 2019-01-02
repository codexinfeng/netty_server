package com.netty.server.event;

import java.util.Observable;

import com.netty.server.jmx.ModuleMetricsVisitor;

public class InvokeObserver extends AbstractInvokeObserve {

	public InvokeObserver(InvokeEventBusFacade facade,
			ModuleMetricsVisitor visitor) {
		super(facade, visitor);
	}

	@Override
	public void update(Observable o, Object arg) {
		if ((AbstractInvokeEventBus.ModuleEvent) arg == AbstractInvokeEventBus.ModuleEvent.INVOKE_EVENT) {
			super.getFacade()
					.fetchEvent(AbstractInvokeEventBus.ModuleEvent.INVOKE_EVENT)
					.notify(super.getVisitor().getInvokeCount(),
							super.getVisitor().incrementInvokeCount());
		}
	}

}
