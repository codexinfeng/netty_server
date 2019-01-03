package com.netty.server.netty;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.Callable;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.NameMatchMethodPointcutAdvisor;

import com.netty.server.core.Modular;
import com.netty.server.core.ModuleInvoker;
import com.netty.server.core.ModuleProvider;
import com.netty.server.core.RpcSystemConfig;
import com.netty.server.model.MessageRequest;
import com.netty.server.model.MessageResponse;
import com.netty.server.spring.BeanFactoryUtils;

public abstract class AbstractMessageRecvInitializeTask implements
		Callable<Boolean> {

	private MessageRequest request = null;
	private MessageResponse response = null;
	private Map<String, Object> handlerMap = null;
	protected static final String METHOD_MAPPED_NAME = "invoke";
	protected boolean returnNotNull = true;
	protected long invokeTimespan;
	protected Modular modular = BeanFactoryUtils.getBean("modular");

	public AbstractMessageRecvInitializeTask(MessageRequest request,
			MessageResponse response, Map<String, Object> handlerMap) {
		this.request = request;
		this.response = response;
		this.handlerMap = handlerMap;
	}

	@Override
	public Boolean call() throws Exception {
		try {
			acquire();
			response.setMessageId(request.getMessageId());
			injectInvoke();
			Object result = reflect(request);
			boolean isInvokeSucc = ((returnNotNull && result != null) || !returnNotNull);
			if (isInvokeSucc) {
				response.setResultDesc(result);
				response.setError("");
				response.setReturnNotNull(returnNotNull);
				injectSuccInvoke(invokeTimespan);
			} else {
				System.err.println(RpcSystemConfig.FILTER_RESPONSE_MSG);
				response.setResultDesc(null);
				response.setError(RpcSystemConfig.FILTER_RESPONSE_MSG);
				injectFilterInvoke();
			}
			return Boolean.TRUE;
		} catch (Throwable t) {
			response.setError(getStackTrace(t));
			t.printStackTrace();
			System.err.println("RPC Server invoke error!\n");
			injectFailInvoke(t);
			return Boolean.FALSE;
		} finally {
			release();
		}

	}

	@SuppressWarnings("unchecked")
	private Object invoke(MethodInvoker mi, MessageRequest request)
			throws Throwable {
		if (modular != null) {
			@SuppressWarnings("rawtypes")
			ModuleProvider provider = modular.invoke(new ModuleInvoker() {
				@Override
				public Class<?> getInterface() {
					return mi.getClass().getInterfaces()[0];
				}

				@Override
				public Object invoke(MessageRequest request) throws Throwable {
					return mi.invoke(request);
				}

				@Override
				public void destroy() {

				}
			}, request);
			return provider.getInvoker().invoke(request);
		} else {
			return mi.invoke(request);
		}
	}

	private Object reflect(MessageRequest request) throws Throwable {
		ProxyFactory weaver = new ProxyFactory(new MethodInvoker());
		NameMatchMethodPointcutAdvisor advisor = new NameMatchMethodPointcutAdvisor();
		advisor.setMappedName(METHOD_MAPPED_NAME);
		advisor.setAdvice(new MethodProxyAdvisor(handlerMap));
		weaver.addAdvisor(advisor);
		MethodInvoker mi = (MethodInvoker) weaver.getProxy();
		Object obj = invoke(mi, request);
		invokeTimespan = mi.getInvokeTimespan();
		setReturnNotNull(((MethodProxyAdvisor) advisor.getAdvice())
				.isReturnNotNull());
		return obj;

	}

	/**
	 * Thowable/Exception 转成String字符串
	 * 
	 * @param ex
	 * @return
	 */
	public String getStackTrace(Throwable ex) {
		StringWriter buf = new StringWriter();
		ex.printStackTrace(new PrintWriter(buf));
		return buf.toString();
	}

	protected abstract void injectInvoke();

	protected abstract void injectSuccInvoke(long invokeTimespan);

	protected abstract void injectFailInvoke(Throwable error);

	protected abstract void injectFilterInvoke();

	protected abstract void acquire();

	protected abstract void release();

	public MessageRequest getRequest() {
		return request;
	}

	public void setRequest(MessageRequest request) {
		this.request = request;
	}

	public MessageResponse getResponse() {
		return response;
	}

	public void setResponse(MessageResponse response) {
		this.response = response;
	}

	public boolean isReturnNotNull() {
		return returnNotNull;
	}

	public void setReturnNotNull(boolean returnNotNull) {
		this.returnNotNull = returnNotNull;
	}

}
