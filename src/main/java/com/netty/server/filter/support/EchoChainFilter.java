package com.netty.server.filter.support;

import com.netty.server.core.ModuleInvoker;
import com.netty.server.filter.ChainFilter;
import com.netty.server.model.MessageRequest;

public class EchoChainFilter implements ChainFilter {

	@Override
	public Object invoke(ModuleInvoker<?> invoker, MessageRequest request)
			throws Throwable {
		Object o = null;
		try {
			System.out.println("EchoChainFilter##TRACE MESSAGE-ID:"
					+ request.getMessageId());
			o = invoker.invoke(request);
			return o;
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			throw throwable;
		}

	}

}
