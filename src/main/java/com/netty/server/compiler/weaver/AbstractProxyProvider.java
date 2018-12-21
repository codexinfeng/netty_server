package com.netty.server.compiler.weaver;

import com.netty.server.compiler.interceptor.Interceptor;

public abstract class AbstractProxyProvider implements ClassProxy {

	@Override
	public <T> T createProxy(Object target, Interceptor interceptor,
			Class<?>... proxyClasses) {
		return createProxy(Thread.currentThread().getContextClassLoader(),
				target, interceptor, proxyClasses);
	}

}
