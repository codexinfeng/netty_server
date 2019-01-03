package com.netty.server.filter;

import java.lang.reflect.Method;

public interface Filter {
	// 过滤器和拦截器有什么区别,过滤返回false,后面还要执行吗
	boolean before(Method method, Object processor, Object[] requestObjects);

	void after(Method method, Object processor, Object[] requestObjects);
}
