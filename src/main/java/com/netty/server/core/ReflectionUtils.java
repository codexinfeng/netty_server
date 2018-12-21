package com.netty.server.core;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.netty.server.exception.CreateProxyException;

public class ReflectionUtils {

	private static ImmutableMap.Builder<Class<?>, Object> builder = ImmutableMap
			.builder();
	private StringBuilder provider = new StringBuilder();

	public StringBuilder getProvider() {
		return provider;
	}

	public void clearProvider() {
		provider.delete(0, provider.length());
	}

	static {
		builder.put(Boolean.class, Boolean.FALSE);
		builder.put(Byte.class, Byte.valueOf((byte) 0));
		builder.put(Character.class, Character.valueOf((char) 0));
		builder.put(Short.class, Short.valueOf((short) 0));
		builder.put(Double.class, Double.valueOf(0));
		builder.put(Float.class, Float.valueOf(0));
		builder.put(Integer.class, Integer.valueOf(0));
		builder.put(Long.class, Long.valueOf(0));
		builder.put(boolean.class, Boolean.FALSE);
		builder.put(byte.class, Byte.valueOf((byte) 0));
		builder.put(char.class, Character.valueOf((char) 0));
		builder.put(short.class, Short.valueOf((short) 0));
		builder.put(double.class, Double.valueOf(0));
		builder.put(float.class, Float.valueOf(0));
		builder.put(int.class, Integer.valueOf(0));
		builder.put(long.class, Long.valueOf(0));
	}

	// 找到所有接口的类
	public static Class<?>[] filterInterfaces(Class<?>[] proxyClasses) {
		Set<Class<?>> interfaces = new HashSet<>();
		for (Class<?> clazz : proxyClasses) {
			if (clazz.isInterface()) {
				interfaces.add(clazz);
			}
		}
		interfaces.add(Serializable.class);
		return interfaces.toArray(new Class[interfaces.size()]);
	}

	public static Class<?>[] filterNonInterfaces(Class<?>[] proxyClasses) {
		Set<Class<?>> nonInterfaceSet = new HashSet<>();
		for (Class<?> clazz : proxyClasses) {
			if (!clazz.isInterface()) {
				nonInterfaceSet.add(clazz);
			}
		}
		return nonInterfaceSet.toArray(new Class[nonInterfaceSet.size()]);
	}

	public static boolean existDefaultConstructor(Class<?> superClass) {
		final Constructor<?>[] declaredConstructors = superClass
				.getConstructors();
		for (int i = 0; i < declaredConstructors.length; i++) {
			Constructor<?> constructor = declaredConstructors[i];
			// 构造函数的参数为0,public或者protected开头
			boolean exist = (constructor.getParameterTypes().length == 0 && (Modifier
					.isPublic(constructor.getModifiers()) || Modifier
					.isProtected(constructor.getModifiers())));
			if (exist) {
				return true;
			}
		}
		return false;
	}

	public static Class<?> getParentClass(Class<?>[] proxyClasses) {
		final Class<?>[] parent = filterNonInterfaces(proxyClasses);
		switch (parent.length) {
		case 0:
			return Object.class;
		case 1:
			Class<?> superclass = parent[0];
			if (Modifier.isFinal(superclass.getModifiers())) {
				throw new CreateProxyException("proxy can't build"
						+ superclass.getName() + "because it is final");
			}
			if (!existDefaultConstructor(superclass)) {
				throw new CreateProxyException("proxy can't build"
						+ superclass.getName()
						+ ",because it has no default constructor");
			}
			return superclass;
		default:
			StringBuilder errorMessage = new StringBuilder(
					"proxy class can't buid");
			for (int i = 0; i < parent.length; i++) {
				Class<?> c = parent[i];
				errorMessage.append(c.getName());
				if (i != parent.length - 1) {
					errorMessage.append(", ");
				}
			}
			errorMessage.append("; multiple implement not allowed");
			throw new CreateProxyException(errorMessage.toString());
		}
	}

	public static boolean isHashCodeMethod(Method method) {
		// hashCode,返回类型是hashCode,没有入参
		return "hashCode".equals(method.getName())
				&& Integer.TYPE.equals(method.getReturnType())
				&& method.getParameterTypes().length == 0;
	}

	public static boolean isEqualsMethod(Method method) {
		// 方法名称是equals,返回参数是boolean,只有一个入参,且入参类型是Object
		return "equals".equals(method.getName())
				&& Boolean.TYPE.equals(method.getReturnType())
				&& method.getParameterTypes().length == 1
				&& Object.class.equals(method.getParameterTypes()[0]);
	}

	public static Object newInstance(Class<?> type) {
		Constructor<?> constructor = null;
		Object[] args = new Object[0];
		try {
			// 默认无参构造方法
			constructor = type.getConstructor(new Class[] {});
		} catch (NoSuchMethodException | SecurityException e) {
		}
		if (constructor == null) {
			Constructor<?>[] constructors = type.getConstructors();
			if (constructors.length == 0) {
				return null;
			}
			constructor = constructors[0];
			Class<?>[] params = constructor.getParameterTypes();
			args = new Object[params.length];
			for (int i = 0; i < params.length; i++) {
				args[i] = getDefaultVal(params[i]);
			}
		}

		try {
			return constructor.newInstance(args);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public static Object getDefaultVal(Class<?> cl) {
		if (cl.isArray()) {
			return Array.newInstance(cl.getComponentType(), 0);
		} else if (cl.isPrimitive() || builder.build().containsKey(cl)) {
			return builder.build().get(cl);
		} else {
			return newInstance(cl);
		}
	}

	public static Class<?> getGenericClass(ParameterizedType parameterizedType,
			int i) {
		Object genericClass = parameterizedType.getActualTypeArguments()[i];
		if (genericClass instanceof GenericArrayType) {
			return (Class<?>) ((GenericArrayType) genericClass)
					.getGenericComponentType();
		} else if (genericClass instanceof ParameterizedType) {
			return (Class<?>) ((ParameterizedType) genericClass).getRawType();
		} else {
			return (Class<?>) genericClass;
		}
	}

	private String modifiers(int m) {
		return m != 0 ? Modifier.toString(m) + " " : "";
	}

	private String getType(Class<?> t) {
		String brackets = "";
		while (t.isArray()) {
			brackets += "[]";
			t = t.getComponentType();
		}
		return t.getName() + brackets;
	}

	private void listTypes(Class<?>[] types) {
		for (int i = 0; i < types.length; i++) {
			if (i > 0) {
				provider.append(", ");
			}
			provider.append(getType(types[i]));
		}
	}

	private void listField(Field f, boolean html) {

	}
}
