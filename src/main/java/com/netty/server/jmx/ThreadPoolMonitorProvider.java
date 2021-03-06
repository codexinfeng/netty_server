package com.netty.server.jmx;

import java.io.IOException;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.jmx.support.MBeanServerConnectionFactoryBean;
import org.springframework.jmx.support.MBeanServerFactoryBean;
import org.springframework.remoting.rmi.RmiRegistryFactoryBean;

import com.netty.server.netty.MessageRecvExecutor;

@Configuration
@EnableMBeanExport
@ComponentScan("com.netty.server.jmx")
public class ThreadPoolMonitorProvider {

	public final static String DELIMITER = ":";
	public static String url;
	// 常量的定义用大写
	public static final String JMX_POOL_SIZE_METHOD = "setPoolSize";
	public static final String JMX_ACTIVE_COUNT_METHOD = "setActiveCount";
	public static final String JMX_CORE_POOL_SIZE_METHOD = "setCorePoolSize";
	public static final String JMX_MAXIMUM_POOL_SIZE_METHOD = "setMaximumPoolSize";
	public static final String JMX_LARGEST_POOL_SIZE_METHOD = "setLargestPoolSize";
	public static final String JMX_TASK_COUNT_METHOD = "setTaskCount";
	public static final String JMX_COMPLETED_TASK_COUNT_METHOD = "setCompletedTaskCount";

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

	@Bean
	@DependsOn("registry")
	public ConnectorServerFactoryBean connectorServer()
			throws MalformedObjectNameException {
		MessageRecvExecutor ref = MessageRecvExecutor.getInstance();
		String ipAddr = StringUtils.isNotEmpty(ref.getServerAddress()) ? StringUtils
				.substringBeforeLast(ref.getServerAddress(), DELIMITER)
				: "localhost";
		url = "service:jmx:rmi://" + ipAddr + "/jndi/rmi://" + ipAddr
				+ ":1099/nettyrpcstatus";
		System.out.println("NettyRPC JMX monitorURL :[" + url + "]");
		ConnectorServerFactoryBean connectorFactoryBean = new ConnectorServerFactoryBean();
		connectorFactoryBean.setObjectName("connector:name=rmi");
		connectorFactoryBean.setServiceUrl(url);
		return connectorFactoryBean;

	}

	public static void monitor(ThreadPoolStatus status) throws IOException,
			MalformedObjectNameException, InstanceNotFoundException,
			MBeanException, ReflectionException {
		MBeanServerConnectionFactoryBean mFactoryBean = new MBeanServerConnectionFactoryBean();
		mFactoryBean.setServiceUrl(url);
		mFactoryBean.afterPropertiesSet();
		MBeanServerConnection connection = mFactoryBean.getObject();
		ObjectName objectName = new ObjectName(
				"com.netty.server.jmx:name=threadPoolStatus,type=ThreadPoolStatus");
		connection.invoke(objectName, JMX_POOL_SIZE_METHOD,
				new Object[] { status.getPoolSize() },
				new String[] { int.class.getName() });
		connection.invoke(objectName, JMX_ACTIVE_COUNT_METHOD,
				new Object[] { status.getActiveCount() },
				new String[] { int.class.getName() });
		connection.invoke(objectName, JMX_CORE_POOL_SIZE_METHOD,
				new Object[] { status.getCorePoolSize() },
				new String[] { int.class.getName() });
		connection.invoke(objectName, JMX_MAXIMUM_POOL_SIZE_METHOD,
				new Object[] { status.getMaximumPoolSize() },
				new String[] { int.class.getName() });
		connection.invoke(objectName, JMX_LARGEST_POOL_SIZE_METHOD,
				new Object[] { status.getLargestPoolSize() },
				new String[] { int.class.getName() });
		connection.invoke(objectName, JMX_TASK_COUNT_METHOD,
				new Object[] { status.getTaskCount() },
				new String[] { int.class.getName() });
		connection.invoke(objectName, JMX_COMPLETED_TASK_COUNT_METHOD,
				new Object[] { status.getCompletedTaskCount() },
				new String[] { long.class.getName() });

	}
}
