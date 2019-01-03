package com.netty.server.listener.support;

import org.apache.commons.lang3.StringUtils;

import com.netty.server.core.ModuleProvider;
import com.netty.server.listener.ModuleListener;
import com.netty.server.model.MessageRequest;

public class ModuleListenerAdapter implements ModuleListener {

	@Override
	public void exported(ModuleProvider<?> provider, MessageRequest request) {
		System.out.println(StringUtils.center(
				"[ModuleListenerAdapter][exported]", 48, "*"));
	}

	@Override
	public void unExported(ModuleProvider<?> provider, MessageRequest request) {
		System.out.println(StringUtils.center(
				"[ModuleListenerAdapter][unExported]", 48, "*"));

	}

}
