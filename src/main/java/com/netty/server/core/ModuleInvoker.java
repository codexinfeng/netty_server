package com.netty.server.core;

import com.netty.server.model.MessageRequest;

public interface ModuleInvoker<T> {

	Class<T> getInterface();

	Object invoke(MessageRequest request) throws Throwable;

	void destroy();
}
