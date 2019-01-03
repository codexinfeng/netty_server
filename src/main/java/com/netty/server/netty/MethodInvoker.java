package com.netty.server.netty;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.time.StopWatch;

import com.netty.server.model.MessageRequest;

public class MethodInvoker {

	private Object serviceBean;
	private StopWatch sw = new StopWatch();

	public Object getServiceBean() {
		return serviceBean;
	}

	public void setServiceBean(Object serviceBean) {
		this.serviceBean = serviceBean;
	}

	public long getInvokeTimespan() {
		return sw.getTime();
	}

	public Object invoke(MessageRequest request) throws Throwable {
		String methodName = request.getMethodName();
		Object[] parameters = request.getParameterVal();
		sw.reset();
		sw.start();
		Object result = MethodUtils.invokeMethod(serviceBean, methodName,
				parameters);
		sw.stop();
		return result;
	}

}
