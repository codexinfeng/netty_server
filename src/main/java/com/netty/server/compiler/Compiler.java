package com.netty.server.compiler;
/**
 * �༭�ֽ���
 * @author JZG
 *
 */
public interface Compiler {

	Class<?> compile(String code, ClassLoader classLoader);
}
