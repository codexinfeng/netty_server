package com.netty.server.listener;

import com.netty.server.core.ModuleProvider;
import com.netty.server.model.MessageRequest;

public interface ModuleListener {

	void exported(ModuleProvider<?> provider, MessageRequest request);

	void unExported(ModuleProvider<?> provider, MessageRequest request);
}
