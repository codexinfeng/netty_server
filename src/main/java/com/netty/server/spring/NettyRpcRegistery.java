package com.netty.server.spring;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.netty.server.core.MessageRecvExecutor;
import com.netty.server.core.RpcSerializerProtocol;
import com.netty.server.core.RpcSystemConfig;
import com.netty.server.jmx.HashModuleMetricsVisitor;
import com.netty.server.jmx.ModuleMetricsHandler;
import com.netty.server.jmx.ThreadPoolMonitorProvider;

public class NettyRpcRegistery implements InitializingBean, DisposableBean {

	private String ipAddr;
	private String protocol;
	private String echoApiPort;
	private AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

	@Override
	public void destroy() throws Exception {
		MessageRecvExecutor.getInstance().stop();
		if (RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_SUPPORT) {
			ModuleMetricsHandler handler = ModuleMetricsHandler.getInstance();
			handler.stop();
		}

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		MessageRecvExecutor ref = MessageRecvExecutor.getInstance();
		ref.setServerAddress(ipAddr);
		ref.setEchoApiPort(Integer.parseInt(echoApiPort));
		ref.setSerializerProtocol(Enum.valueOf(RpcSerializerProtocol.class,
				protocol));
		if (RpcSystemConfig.isMonitorServerSupport()) {
			context.register(ThreadPoolMonitorProvider.class);
			context.refresh();
		}
		ref.start();
		if (RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_SUPPORT) {
			HashModuleMetricsVisitor visitor = HashModuleMetricsVisitor
					.getInstance();
			visitor.signal();
			ModuleMetricsHandler handler = ModuleMetricsHandler.getInstance();
			handler.start();
		}
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

	public String getEchoApiPort() {
		return echoApiPort;
	}

	public void setEchoApiPort(String echoApiPort) {
		this.echoApiPort = echoApiPort;
	}

}
