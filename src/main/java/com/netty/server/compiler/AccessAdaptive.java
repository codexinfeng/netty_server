package com.netty.server.compiler;

public interface AccessAdaptive {

	Object invoke(String code, String method, Object[] args);
}
