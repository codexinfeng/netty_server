package com.netty.server.core;

public interface ModuleProvider<T> {

	ModuleInvoker<T> getInvoker();

	void destoryInvoker();
}
