package com.netty.server.event;

import java.util.Observer;

import com.netty.server.jmx.ModuleMetricsVisitor;

public abstract class AbstractInvokeObserve implements Observer {

	private InvokeEventBusFacade facade;

	private ModuleMetricsVisitor visitor;

	public AbstractInvokeObserve(InvokeEventBusFacade facade,
			ModuleMetricsVisitor visitor) {
		this.facade = facade;
		this.visitor = visitor;
	}

	public InvokeEventBusFacade getFacade() {
		return facade;
	}

	public void setFacade(InvokeEventBusFacade facade) {
		this.facade = facade;
	}

	public ModuleMetricsVisitor getVisitor() {
		return visitor;
	}

	public void setVisitor(ModuleMetricsVisitor visitor) {
		this.visitor = visitor;
	}

}
