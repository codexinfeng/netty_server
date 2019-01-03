package com.netty.server.filter.support;

import com.netty.server.core.ModuleInvoker;
import com.netty.server.filter.ChainFilter;
import com.netty.server.model.MessageRequest;

public class ClassLoaderChainFilter implements ChainFilter {

	@Override
	public Object invoke(ModuleInvoker<?> invoker, MessageRequest request)
			throws Throwable {
		ClassLoader ocl = Thread.currentThread().getContextClassLoader();
		//先使用invoker的classloader 最后再设置回来
		Thread.currentThread().setContextClassLoader(
				invoker.getInterface().getClassLoader());
		try {
			Object result = null;
			result = invoker.invoke(request);
			return result;
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			throw throwable;
		} finally {
			Thread.currentThread().setContextClassLoader(ocl);
		}
	}

}
