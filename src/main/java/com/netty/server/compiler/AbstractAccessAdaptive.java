package com.netty.server.compiler;

import java.util.regex.Pattern;

public class AbstractAccessAdaptive implements Compiler {
	// 自己手动写编译器
	private static final Pattern PACKAGE_PATTERN = Pattern
			.compile("package\\s+([$_a-zA-Z][$_a-zA-Z0-9\\.]*);)");
	private static final Pattern CLASS_PATTERN = Pattern
			.compile("class\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s+");
	private static final String CLASS_END_FLAG = "}";

	// protected ClassProxy factory = new

	@Override
	public Class<?> compile(String code, ClassLoader classLoader) {
		// TODO Auto-generated method stub
		return null;
	}

}
