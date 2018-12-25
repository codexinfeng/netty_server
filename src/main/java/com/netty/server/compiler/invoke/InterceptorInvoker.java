package com.netty.server.compiler.invoke;

import java.lang.reflect.Method;

import com.netty.server.compiler.interceptor.Interceptor;
import com.netty.server.compiler.interceptor.InvocationProvider;

public class InterceptorInvoker extends AbstractInvoker {

	private final Object target;
	private final Interceptor methodInterceptor;

	public InterceptorInvoker(Object target, Interceptor methodInterceptor) {
		this.target = target;
		this.methodInterceptor = methodInterceptor;
	}

	@Override
	public Object invokeImpl(Object proxy, Method method, Object[] args)
			throws Throwable {
		InvocationProvider invocation = new InvocationProvider(target, proxy,
				method, args);
		return methodInterceptor.interceptor(invocation);
	}

}
