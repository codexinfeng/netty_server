package com.netty.server.async;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;

import com.netty.server.core.ReflectionUtils;
import com.netty.server.core.RpcSystemConfig;
import com.netty.server.exception.AsyncCallException;
import com.netty.server.exception.InvokeTimeoutException;

public class AsyncCallResult {
	private Class<?> returnClass;
	private Future<?> future;
	private long timeout;

	public AsyncCallResult(Class<?> returnClass, Future<?> future, long timeout) {
		this.returnClass = returnClass;
		this.future = future;
		this.timeout = timeout;
	}

	public Object loadFuture() throws AsyncCallException {
		try {
			if (timeout <= 0L) {
				return future.get();
			} else {
				return future.get(timeout, TimeUnit.MILLISECONDS);
			}
		} catch (TimeoutException e) {
			future.cancel(true);
			throw new AsyncCallException(e);
		} catch (InterruptedException e) {
			throw new AsyncCallException(e);
		} catch (Exception e) {
			translateTimeoutException(e);
			throw new AsyncCallException(e);
		}
	}

	private void translateTimeoutException(Throwable t) {
		int index = t.getMessage()
				.indexOf(RpcSystemConfig.TIMEOUT_RESPONSE_MSG);
		if (index != -1) {
			throw new InvokeTimeoutException(t);
		}
	}

	public Object getResult() {
		Class<?> proxyClass = AsyncProxyCache.get(returnClass.getName());
		// enhancer是通过继承类来实现的
		if (proxyClass == null) {
			Enhancer enhancer = new Enhancer();
			if (returnClass.isInterface()) {
				enhancer.setInterfaces(new Class[] { AsyncCallObject.class,
						returnClass });
			} else {
				enhancer.setInterfaces(new Class[] { AsyncCallObject.class });
				enhancer.setSuperclass(returnClass);
			}
			enhancer.setCallbackFilter(new AsyncCallFilter());
			enhancer.setCallbackTypes(new Class[] {
					AsyncCallResultInterceptor.class,
					AsyncCallObjectInterceptor.class });
			proxyClass = enhancer.createClass();
			AsyncProxyCache.save(returnClass.getName(), proxyClass);
		}
		Enhancer.registerCallbacks(proxyClass, new Callback[] {
				new AsyncCallResultInterceptor(this),
				new AsyncCallObjectInterceptor(future) });

		try {
			return ReflectionUtils.newInstance(proxyClass);
		} finally {
			Enhancer.registerStaticCallbacks(proxyClass, null);
		}

	}
}
