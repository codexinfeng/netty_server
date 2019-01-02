package com.netty.server.event;

import java.util.Observable;

import com.netty.server.jmx.ModuleMetricsVisitor;

public class InvokeSuccObserver extends AbstractInvokeObserve {
	private long invokeTimespan;

	public InvokeSuccObserver(InvokeEventBusFacade facade,
			ModuleMetricsVisitor visitor, long invokeTimespan) {
		super(facade, visitor);
		this.invokeTimespan = invokeTimespan;

	}

	public long getInvokeTimespan() {
		return invokeTimespan;
	}

	public void setInvokeTimespan(long invokeTimespan) {
		this.invokeTimespan = invokeTimespan;
	}

	@Override
	public void update(Observable o, Object arg) {
		if ((AbstractInvokeEventBus.ModuleEvent) arg == AbstractInvokeEventBus.ModuleEvent.INVOKE_SUCC_EVENT) {
			super.getFacade()
					.fetchEvent(
							AbstractInvokeEventBus.ModuleEvent.INVOKE_SUCC_EVENT)
					.notify(super.getVisitor().getInvokeSuccCount(),
							super.getVisitor().incrementInvokeSuccCount());
			super.getFacade()
					.fetchEvent(
							AbstractInvokeEventBus.ModuleEvent.INVOKE_TIMESPAN_EVENT)
					.notify(super.getVisitor().getInvokeTimespan(),
							invokeTimespan);
			super.getFacade()
					.fetchEvent(
							AbstractInvokeEventBus.ModuleEvent.INVOKE_MAX_TIMESPAN_EVENT)
					.notify(super.getVisitor().getInvokeMaxTimespan(),
							invokeTimespan);
			super.getFacade()
					.fetchEvent(
							AbstractInvokeEventBus.ModuleEvent.INVOKE_MIN_TIMESPAN_EVENT)
					.notify(super.getVisitor().getInvokeMinTimespan(),
							invokeTimespan);
		}

	}

}
