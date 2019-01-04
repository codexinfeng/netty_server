package com.netty.server.netty;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.netty.server.core.ReflectionUtils;
import com.netty.server.event.InvokeEventBusFacade;
import com.netty.server.event.InvokeEventWatcher;
import com.netty.server.event.InvokeFailObserver;
import com.netty.server.event.InvokeFilterObserver;
import com.netty.server.event.InvokeObserver;
import com.netty.server.event.AbstractInvokeEventBus.ModuleEvent;
import com.netty.server.event.InvokeSuccObserver;
import com.netty.server.filter.ServiceFilterBinder;
import com.netty.server.jmx.ModuleMetricsHandler;
import com.netty.server.jmx.ModuleMetricsVisitor;
import com.netty.server.model.MessageRequest;
import com.netty.server.model.MessageResponse;
import com.netty.server.parallel.SemaphoreWrapperFactory;

public class MessageRecvInitializeTask extends
		AbstractMessageRecvInitializeTask {
	private AtomicReference<ModuleMetricsVisitor> visitor = new AtomicReference<>();
	private AtomicReference<InvokeEventBusFacade> facade = new AtomicReference<>();
	private AtomicReference<InvokeEventWatcher> watcher = new AtomicReference<>(
			new InvokeEventWatcher());
	private SemaphoreWrapperFactory factory = SemaphoreWrapperFactory
			.getInstance();

	public MessageRecvInitializeTask(MessageRequest request,
			MessageResponse response, Map<String, Object> handlerMap) {
		super(request, response, handlerMap);
	}

	@Override
	protected void injectInvoke() {
		Class<?> cls = handlerMap.get(request.getClassName()).getClass();
		boolean binder = ServiceFilterBinder.class.isAssignableFrom(cls);
		if (binder) {
			cls = ((ServiceFilterBinder) handlerMap.get(request.getClassName()))
					.getObject().getClass();
		}
		ReflectionUtils utils = new ReflectionUtils();
		try {
			Method method = ReflectionUtils.getDeclaredMethod(cls,
					request.getMethodName(), request.getTypeParameter());
			utils.listMethod(method, false);
			String signatureMethod = utils.getProvider().toString();
			visitor.set(ModuleMetricsHandler.getInstance().visit(
					request.getClassName(), signatureMethod));
			facade.set(new InvokeEventBusFacade(ModuleMetricsHandler
					.getInstance(), visitor.get().getModuleName(), visitor
					.get().getMethodName()));
			watcher.get().addObserver(
					new InvokeObserver(facade.get(), visitor.get()));
			watcher.get().watch(ModuleEvent.INVOKE_EVENT);
		} finally {
			utils.clearProvider();
		}
	}

	@Override
	protected void injectSuccInvoke(long invokeTimespan) {
		watcher.get().addObserver(
				new InvokeSuccObserver(facade.get(), visitor.get(),
						invokeTimespan));
		watcher.get().watch(ModuleEvent.INVOKE_SUCC_EVENT);

	}

	@Override
	protected void injectFailInvoke(Throwable error) {
		watcher.get().addObserver(
				new InvokeFailObserver(facade.get(), visitor.get(), error));
		watcher.get().watch(ModuleEvent.INVOKE_FAIL_EVENT);
	}

	@Override
	protected void injectFilterInvoke() {
		watcher.get().addObserver(
				new InvokeFilterObserver(facade.get(), visitor.get()));
		watcher.get().watch(ModuleEvent.INVOKE_FILTER_EVENT);
	}

	@Override
	protected void acquire() {
		factory.acquire();
	}

	@Override
	protected void release() {
		factory.release();
	}

}
