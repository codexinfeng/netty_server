package com.netty.server.compiler.interceptor;

import java.lang.reflect.Method;

public interface Invocation {
	/**
	 * ����
	 * 
	 * @return
	 */
	Object[] getArguments();

	/**
	 * ����
	 * 
	 * @return
	 */
	Method getMehtod();

	/**
	 * ����
	 * 
	 * @return
	 */
	Object getProxy();

	/**
	 * ִ��
	 * 
	 * @return
	 * @throws Throwable
	 */
	Object proceed() throws Throwable;
}
