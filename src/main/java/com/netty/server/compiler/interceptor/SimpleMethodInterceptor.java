package com.netty.server.compiler.interceptor;

import org.apache.commons.lang3.StringUtils;

public class SimpleMethodInterceptor implements Interceptor {
	@Override
	public Object interceptor(Invocation invocation) throws Throwable {
		System.out.println(StringUtils.center(
				"[SimpleMethodInterceptor##intercept]", 48, "*"));
		/**
		 * 反射过来的请求做拦截处理
		 */
		return invocation.proceed();
	}

}
