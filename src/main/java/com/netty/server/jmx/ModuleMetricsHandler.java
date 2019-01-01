package com.netty.server.jmx;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

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
	private ModuleMetricsListener listener = new ModuleMetricsListener();

	public static ModuleMetricsHandler getInstance() {
		return INSTANCE;
	}

	private ModuleMetricsHandler() {
		super();
	}

	@Override
	protected ModuleMetricsVisitor visitCriticalSection(String moduleName,
			String methodName) {
		// TODO Auto-generated method stub
		return null;
	}

	public void stop() {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		try {
			ObjectName name = new ObjectName(MBEAN_NAME);
			mbs.unregisterMBean(name);
			ExecutorService executor = getExecutor();
			executor.shutdown();
			while (!executor.isTerminated()) {

			}

		} catch (MalformedObjectNameException | MBeanRegistrationException
				| InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public MBeanServerConnection connect() {
		if (!semaphoreWrapper.isRelease()) {
			semaphoreWrapper.acquire();
		}
		try {
			JMXServiceURL url = new JMXServiceURL(moduleMetricsJmxUrl);
			JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
			connection = jmxc.getMBeanServerConnection();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connection;

	}

	public MBeanServerConnection getConnection() {
		return connection;
	}

	public CountDownLatch getLatch() {
		return latch;
	}

	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}

}
