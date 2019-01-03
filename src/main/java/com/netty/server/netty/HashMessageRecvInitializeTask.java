package com.netty.server.netty;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.management.JMException;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.iterators.FilterIterator;

import com.netty.server.core.ReflectionUtils;
import com.netty.server.filter.ServiceFilterBinder;
import com.netty.server.jmx.HashModuleMetricsVisitor;
import com.netty.server.jmx.ModuleMetricsVisitor;
import com.netty.server.model.MessageRequest;
import com.netty.server.model.MessageResponse;
import com.netty.server.parallel.HashCriticalSection;

public class HashMessageRecvInitializeTask extends
		AbstractMessageRecvInitializeTask {
	private int hashKey = 0;
	private static HashCriticalSection criticalSection = new HashCriticalSection();
	private AtomicReference<ModuleMetricsVisitor> visitor = new AtomicReference<>();

	public HashMessageRecvInitializeTask(MessageRequest request,
			MessageResponse response, Map<String, Object> handlerMap) {
		super(request, response, handlerMap);
		hashKey = HashCriticalSection.hash(request.getMessageId());
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
			String signatureMethod = utils.getProvider().toString().trim();
			int index = getHashVisitorListIndex(signatureMethod);
			List<ModuleMetricsVisitor> metricsVisitor = HashModuleMetricsVisitor
					.getInstance().getHashVisitorList().get(index);
			visitor.set(metricsVisitor.get(hashKey));
			incrementInvoke(visitor.get());
		} finally {
			utils.clearProvider();
		}
	}

	@Override
	protected void injectSuccInvoke(long invokeTimespan) {
		incrementInvokeSucc(visitor.get(), invokeTimespan);
	}

	@Override
	protected void injectFailInvoke(Throwable error) {
		incrementInvokFail(visitor.get(), error);
	}

	@Override
	protected void injectFilterInvoke() {
		incrementInvokFilter(visitor.get());
	}

	@Override
	protected void acquire() {
		criticalSection.enter(hashKey);
	}

	@Override
	protected void release() {
		criticalSection.exit(hashKey);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private int getHashVisitorListIndex(String signatureMethod) {
		int index = 0;
		int size = HashModuleMetricsVisitor.getInstance()
				.getHashModuleMetricsVisitorListSize();
		breakFor: for (index = 0; index < size; index++) {
			Iterator iterator = new FilterIterator(HashModuleMetricsVisitor
					.getInstance().getHashVisitorList().get(index).iterator(),
					new Predicate() {
						@Override
						public boolean evaluate(Object object) {
							String statModuleName = ((ModuleMetricsVisitor) object)
									.getModuleName();
							String statMethodName = ((ModuleMetricsVisitor) object)
									.getMethodName();
							return statModuleName.compareTo(request
									.getClassName()) == 0
									&& statMethodName
											.compareTo(signatureMethod) == 0;
						}
					});

			while (iterator.hasNext()) {
				break breakFor;
			}
		}
		return index;
	}

	private void incrementInvoke(ModuleMetricsVisitor visitor) {
		visitor.setHashKey(hashKey);
		visitor.incrementInvokeCount();
	}

	private void incrementInvokeSucc(ModuleMetricsVisitor visitor,
			long invokeTimespan) {
		visitor.incrementInvokeSuccCount();
		visitor.getHistogram().record(invokeTimespan);
		visitor.setInvokeTimespan(invokeTimespan);
		if (invokeTimespan < visitor.getInvokeMinTimespan()) {
			visitor.setInvokeMinTimespan(invokeTimespan);
		}
		if (invokeTimespan > visitor.getInvokeMaxTimespan()) {
			visitor.setInvokeMaxTimespan(invokeTimespan);
		}
	}

	private void incrementInvokFail(ModuleMetricsVisitor visitor, Throwable err) {
		visitor.incrementInvokeFailCount();
		visitor.setLastStackTrace((Exception) err);

		try {
			visitor.buildErrorCompositeData(err);
		} catch (JMException e) {
			e.printStackTrace();
		}
	}

	private void incrementInvokFilter(ModuleMetricsVisitor visitor) {
		visitor.incrementInvokeFilterCount();
	}

}
