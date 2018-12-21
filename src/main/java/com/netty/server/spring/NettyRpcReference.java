package com.netty.server.spring;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.google.common.eventbus.EventBus;
import com.netty.server.core.MessageSendExecutor;
import com.netty.server.core.RpcSerializerProtocol;
import com.netty.server.event.ClientStopEvent;
import com.netty.server.event.ClientStopEventListener;

public class NettyRpcReference implements FactoryBean<Object>,
		InitializingBean, DisposableBean {
	private String interfaceName;

	private String ipAddr;

	private String protocol;

	private EventBus eventBus = new EventBus();

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	@Override
	public Object getObject() throws Exception {
		return MessageSendExecutor.getInstance().execute(getObjectType());
	}

	@Override
	public Class<?> getObjectType() {
		try {
			return this.getClass().getClassLoader().loadClass(interfaceName);
		} catch (ClassNotFoundException e) {
			System.err.println("spring analize fail");
		}
		return null;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void destroy() throws Exception {
		eventBus.post(new ClientStopEvent(0));
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		MessageSendExecutor.getInstance().setRpcServerLoader(ipAddr,
				RpcSerializerProtocol.valueOf(protocol));
		ClientStopEventListener listener = new ClientStopEventListener();
		eventBus.register(listener);
	}

}
