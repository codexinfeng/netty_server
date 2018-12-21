package com.netty.server.compiler.interceptor;

public interface Interceptor {

	Object interceptor(Invocation invocation) throws Throwable;
}
