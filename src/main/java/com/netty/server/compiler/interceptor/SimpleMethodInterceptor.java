package com.netty.server.compiler.interceptor;

import org.apache.commons.lang3.StringUtils;

public class SimpleMethodInterceptor implements Interceptor {
	//具体方法拦截器要实现
	@Override
	public Object interceptor(Invocation invocation) throws Throwable {
		System.out.println(StringUtils.center(
				"[SimpleMethodInterceptor##intercept]", 48, "*"));
		/**
		 */
		return invocation.proceed();
	}

}
