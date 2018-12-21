package com.netty.server.compiler.weaver;

public interface Transformer {

	Class<?> transform(ClassLoader classLoader, Class<?>... proxyClasses);
}
