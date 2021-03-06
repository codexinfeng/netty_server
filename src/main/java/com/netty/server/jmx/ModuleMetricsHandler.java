package com.netty.server.jmx;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.iterators.FilterIterator;
import org.apache.commons.lang3.StringUtils;

import com.netty.server.core.RpcSystemConfig;
import com.netty.server.netty.MessageRecvExecutor;
import com.netty.server.parallel.AbstractDaemonThread;
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
	public List<ModuleMetricsVisitor> getModuleMetricsVisitor() {
		return super.getModuleMetricsVisitor();
	}

	@Override
	protected ModuleMetricsVisitor visitCriticalSection(String moduleName,
			String methodName) {
		final String method = methodName.trim();
		final String module = moduleName.trim();
		// JMX度量临界区注意线程间的并发竞争,否则会统计数据失真
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Iterator iterator = new FilterIterator<>(visitorList.iterator(),
				new Predicate() {
					@Override
					public boolean evaluate(Object object) {
						String statModuleName = ((ModuleMetricsVisitor) object)
								.getModuleName();
						String statMethodName = ((ModuleMetricsVisitor) object)
								.getMethodName();
						return statModuleName.compareTo(module) == 0
								&& statMethodName.compareTo(method) == 0;
					}
				});

		ModuleMetricsVisitor visitor = null;
		while (iterator.hasNext()) {
			visitor = (ModuleMetricsVisitor) iterator.next();
			break;
		}
		if (visitor != null) {
			return visitor;
		} else {
			visitor = new ModuleMetricsVisitor(moduleName, methodName);
			addModuleMetricsVisitor(visitor);
			return visitor;
		}

	}

	public void start() {
		new AbstractDaemonThread() {
			@Override
			public String getDeamonThreadName() {
				return ModuleMetricsHandler.class.getSimpleName();
			}

			@Override
			public void run() {
				MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
				try {
					latch.await();
					LocateRegistry.createRegistry(MODULE_METRICS_JMX_PORT);
					MessageRecvExecutor ref = MessageRecvExecutor.getInstance();
					String ipAddr = StringUtils.isNotEmpty(ref
							.getServerAddress()) ? StringUtils
							.substringBeforeLast(ref.getServerAddress(),
									RpcSystemConfig.DELIMITER) : "localhost";
					moduleMetricsJmxUrl = "service:jmx:rmi:///jndi/rmi://"
							+ ipAddr + ":" + MODULE_METRICS_JMX_PORT
							+ "/NettyRPCServer";
					JMXServiceURL url = new JMXServiceURL(moduleMetricsJmxUrl);
					JMXConnectorServer cs = JMXConnectorServerFactory
							.newJMXConnectorServer(url, null, mbs);

					ObjectName name = new ObjectName(MBEAN_NAME);

					mbs.registerMBean(ModuleMetricsHandler.this, name);
					mbs.addNotificationListener(name, listener, null, null);
					cs.start();

					semaphoreWrapper.release();

					System.out
							.printf("NettyRPC JMX server is start success!\njmx-url:[ %s ]\n\n",
									moduleMetricsJmxUrl);
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (MBeanRegistrationException e) {
					e.printStackTrace();
				} catch (InstanceAlreadyExistsException e) {
					e.printStackTrace();
				} catch (NotCompliantMBeanException e) {
					e.printStackTrace();
				} catch (MalformedObjectNameException e) {
					e.printStackTrace();
				} catch (InstanceNotFoundException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
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
