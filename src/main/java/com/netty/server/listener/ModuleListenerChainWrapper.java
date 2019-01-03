package com.netty.server.listener;

import java.util.Collections;
import java.util.List;

import com.netty.server.core.Modular;
import com.netty.server.core.ModuleInvoker;
import com.netty.server.core.ModuleProvider;
import com.netty.server.model.MessageRequest;

public class ModuleListenerChainWrapper implements Modular {

	private Modular modular;
	private List<ModuleListener> listeners;

	public ModuleListenerChainWrapper(Modular modular) {
		if (modular == null) {
			throw new IllegalArgumentException("module is null");
		}
		this.modular = modular;
	}

	@Override
	public <T> ModuleProvider<T> invoke(ModuleInvoker<T> invoker,
			MessageRequest request) {
		return new ModuleProviderWrapper<>(modular.invoke(invoker, request),
				Collections.unmodifiableList(listeners), request);
	}

	public List<ModuleListener> getListeners() {
		return listeners;
	}

	public void setListeners(List<ModuleListener> listeners) {
		this.listeners = listeners;
	}

}
