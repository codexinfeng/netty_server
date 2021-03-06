package com.netty.server.compiler;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.google.common.io.Files;
import com.netty.server.compiler.interceptor.SimpleMethodInterceptor;
import com.netty.server.core.ReflectionUtils;

public class AccessAdaptiveProvider extends AbstractAccessAdaptive implements
		AccessAdaptive {

	@Override
	protected Class<?> doCompile(String clsName, String javaSource)
			throws Throwable {
		File tempFileLocation = Files.createTempDir();
		compiler = new NativeCompiler(tempFileLocation);
		Class<?> type = compiler.compile(clsName, javaSource);
		tempFileLocation.deleteOnExit();
		return type;
	}

	@Override
	public Object invoke(String javaSource, String method, Object[] args) {
		if (StringUtils.isEmpty(javaSource) || StringUtils.isEmpty(method)) {
			return null;
		} else {
			try {
				Class<?> type = compile(javaSource, Thread.currentThread()
						.getContextClassLoader());
				Object object = ReflectionUtils.newInstance(type);
				Thread.currentThread().getContextClassLoader()
						.loadClass(type.getName());
				Object proxy = getFactory().createProxy(object,
						new SimpleMethodInterceptor(), new Class[] { type });
				return MethodUtils.invokeMethod(proxy, method, args);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
