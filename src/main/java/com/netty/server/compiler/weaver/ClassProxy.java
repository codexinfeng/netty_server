package com.netty.server.compiler.weaver;

import com.netty.server.compiler.interceptor.Interceptor;

public interface ClassProxy {

	<T> T createProxy(Object target, Interceptor interceptor,
			Class<?>... proxyClasses);

	<T> T createProxy(ClassLoader classLoader, Object target,
			Interceptor interceptor, Class<?>... proxyClasses);
}
