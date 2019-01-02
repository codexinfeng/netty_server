package com.netty.server.core;

import com.netty.server.model.MessageRequest;

public interface Modular {

	<T> ModuleProvider<T> invoke(ModuleInvoker<T> invoker,
			MessageRequest request);
}
