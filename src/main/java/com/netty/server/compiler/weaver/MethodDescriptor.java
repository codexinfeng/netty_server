package com.netty.server.compiler.weaver;

import java.lang.reflect.Method;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class MethodDescriptor {

	private static final Map<Class<?>, Character> BUILDER = new ImmutableMap.Builder<Class<?>, Character>()
			.put(Boolean.TYPE, Character.valueOf('Z'))
			.put(Byte.TYPE, Character.valueOf('B'))
			.put(Short.TYPE, Character.valueOf('S'))
			.put(Integer.TYPE, Character.valueOf('I'))
			.put(Character.TYPE, Character.valueOf('C'))
			.put(Long.TYPE, Character.valueOf('J'))
			.put(Float.TYPE, Character.valueOf('F'))
			.put(Double.TYPE, Character.valueOf('D'))
			.put(Void.TYPE, Character.valueOf('V')).build();

	private final String internal;

	public MethodDescriptor(Method method) {
		final StringBuilder buf = new StringBuilder(method.getName())
				.append('(');
		for (Class<?> p : method.getParameterTypes()) {
			appendTo(buf, p);
		}
		buf.append(')');
		this.internal = buf.toString();
	}

	private static void appendTo(StringBuilder buf, Class<?> type) {
		if (type.isPrimitive()) {
			buf.append(BUILDER.get(type));
		} else if (type.isArray()) {
			buf.append('[');
			appendTo(buf, type.getComponentType());
		} else {
			buf.append('L').append(type.getName().replace('.', '/'))
					.append(";");
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}

		MethodDescriptor descriptor = (MethodDescriptor) obj;
		return descriptor.internal.equals(internal);
	}

	@Override
	public int hashCode() {
		return internal.hashCode();
	}

	@Override
	public String toString() {
		return internal;
	}

}
