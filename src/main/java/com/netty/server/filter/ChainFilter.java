package com.netty.server.filter;

import com.netty.server.core.ModuleInvoker;
import com.netty.server.model.MessageRequest;

public interface ChainFilter {

	Object invoke(ModuleInvoker<?> invoker, MessageRequest request)
			throws Throwable;
}
