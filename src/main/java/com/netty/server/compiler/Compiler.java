package com.netty.server.compiler;
/**
 * ±à¼­×Ö½ÚÂë
 * @author JZG
 *
 */
public interface Compiler {

	Class<?> compile(String code, ClassLoader classLoader);
}
