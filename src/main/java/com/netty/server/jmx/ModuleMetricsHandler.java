package com.netty.server.jmx;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import javax.management.MBeanServerConnection;

import com.netty.server.parallel.SemaphoreWrapper;

public class ModuleMetricsHandler extends AbstractModuleMetricsHandler {

	public final static String MBEAN_NAME = "com.netty.server.jmx:type=ModuleMetricsHandler";
	public final static int MODULE_METRICS_JMX_PORT = 1098;
	private String moduleMetricsJmxUrl = "";
	private Semaphore semaphore = new Semaphore(0);
	private SemaphoreWrapper semaphoreWrapper = new SemaphoreWrapper(semaphore);
	private static final ModuleMetricsHandler INSTANCE = new ModuleMetricsHandler();
	private MBeanServerConnection connection;
	private CountDownLatch latch = new CountDownLatch(1);
//	private ModuleMetric
	
	public static ModuleMetricsHandler getInstance(){
		return INSTANCE;
	}
	

	@Override
	protected ModuleMetricsVisitor visitCriticalSection(String moduleName,
			String methodName) {
		// TODO Auto-generated method stub
		return null;
	}

}
