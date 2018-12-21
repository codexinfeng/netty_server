package com.netty.server.jmx;

import javax.management.MalformedObjectNameException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.jmx.support.MBeanServerFactoryBean;
import org.springframework.remoting.rmi.RmiRegistryFactoryBean;

import com.netty.server.core.MessageRecvExecutor;

@Configuration
@EnableMBeanExport
@ComponentScan("com.netty.server.jmx")
public class ThreadPoolMonitorProvider {

	public final static String DELIMITER = ":";
	public static String url;
	public static String jmxPoolSizeMethod = "setPoolSize";
	public static String jmxActiveCountMethod = "setActiveCount";
	public static String jmxCorePoolSizeMethod = "setCorePoolSize";
	public static String jmxMaximumPoolSizeMethod = "setMaximumPoolSize";
	public static String jmxLargestPoolSizeMethod = "setLargestPoolSize";
	public static String jmxTaskCountMethod = "setTaskCount";
	public static String jmxCompletedTaskCountMethod = "setCompletedTaskCount";

	@Bean
	public ThreadPoolStatus threadPoolStatus() {
		return new ThreadPoolStatus();
	}

	@Bean
	public MBeanServerFactoryBean mbeanServer() {
		return new MBeanServerFactoryBean();
	}

	@Bean
	public RmiRegistryFactoryBean registry() {
		return new RmiRegistryFactoryBean();
	}

//	public ConnectorServerFactoryBean connectorServer()
//			throws MalformedObjectNameException {
////		MessageRecvExecutor ref = MessageRecvExecutor.g
//
//	}

}
