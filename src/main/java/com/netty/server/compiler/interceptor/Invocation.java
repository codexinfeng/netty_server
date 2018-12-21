package com.netty.server.compiler.interceptor;

import java.lang.reflect.Method;

public interface Invocation {
	/**
	 * 参数
	 * 
	 * @return
	 */
	Object[] getArguments();

	/**
	 * 方法
	 * 
	 * @return
	 */
	Method getMehtod();

	/**
	 * 代理
	 * 
	 * @return
	 */
	Object getProxy();

	/**
	 * 执行
	 * 
	 * @return
	 * @throws Throwable
	 */
	Object proceed() throws Throwable;
}
