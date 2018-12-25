package com.netty.server.compiler.invoke;

import java.lang.reflect.Method;

import com.netty.server.core.ReflectionUtils;

public abstract class AbstractInvoker implements ObjectInvoker {
	@Override
	public Object invoke(Object proxy, Method method, Object... args)
			throws Throwable {
		if (ReflectionUtils.isHashCodeMethod(method)) {
			return Integer.valueOf(System.identityHashCode(proxy));
		}

		if (ReflectionUtils.isEqualsMethod(method)) {
			return Boolean.valueOf(proxy == args[0]);
		}

		return invokeImpl(proxy, method, args);
	}

	public abstract Object invokeImpl(Object proxy, Method method, Object[] args)
			throws Throwable;
}
