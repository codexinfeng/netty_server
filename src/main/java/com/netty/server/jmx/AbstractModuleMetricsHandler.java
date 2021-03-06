package com.netty.server.jmx;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.NotificationBroadcasterSupport;

import com.netty.server.core.RpcSystemConfig;

public abstract class AbstractModuleMetricsHandler extends
		NotificationBroadcasterSupport implements ModuleMetricsVistorMXBean {

	protected List<ModuleMetricsVisitor> visitorList = new CopyOnWriteArrayList<>();
	protected static String startTime;
	private final AtomicBoolean locked = new AtomicBoolean(false);
	private final Queue<Thread> waiters = new ConcurrentLinkedQueue<>();
	private static final int METRICS_VISITOR_LIST_SIZE = HashModuleMetricsVisitor
			.getInstance().getHashModuleMetricsVisitorListSize();
	private MetricsTask[] tasks = new MetricsTask[METRICS_VISITOR_LIST_SIZE];
	private boolean aggregationTaskFlag = false;
	private ExecutorService executor = Executors
			.newFixedThreadPool(METRICS_VISITOR_LIST_SIZE);

	public AbstractModuleMetricsHandler() {
	}

	public ModuleMetricsVisitor visit(String moduleName, String methodName) {
		try {
			enter();
			return visitCriticalSection(moduleName, methodName);
		} finally {
			exit();
		}
	}

	@Override
	public List<ModuleMetricsVisitor> getModuleMetricsVisitor() {
		if (RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_HASH_SUPPORT) {
			CountDownLatch latch = new CountDownLatch(1);
			MetricsAggregationTask aggregationTask = new MetricsAggregationTask(
					aggregationTaskFlag, tasks, visitorList, latch);
			CyclicBarrier barrier = new CyclicBarrier(
					METRICS_VISITOR_LIST_SIZE, aggregationTask);
			for (int i = 0; i < METRICS_VISITOR_LIST_SIZE; i++) {
				tasks[i] = new MetricsTask(barrier, HashModuleMetricsVisitor
						.getInstance().getHashVisitorList().get(i));
				executor.execute(tasks[i]);
			}
			visitorList.clear();
			try {
				latch.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return visitorList;
	}

	@Override
	public void addModuleMetricsVisitor(ModuleMetricsVisitor visitor) {
		visitorList.add(visitor);
	}

	@Override
	public MBeanNotificationInfo[] getNotificationInfo() {
		String[] types = new String[] { AttributeChangeNotification.ATTRIBUTE_CHANGE };
		String name = AttributeChangeNotification.class.getName();
		String description = "the event send from NettyRPC server!";
		MBeanNotificationInfo info = new MBeanNotificationInfo(types, name,
				description);
		return new MBeanNotificationInfo[] { info };
	}

	public final static String getStartTime() {
		if (startTime == null) {
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			startTime = format.format(new Date(ManagementFactory
					.getRuntimeMXBean().getStartTime()));
		}
		return startTime;
	}

	protected void enter() {
		Thread current = Thread.currentThread();
		waiters.add(current);
		while (waiters.peek() != current || locked.compareAndSet(false, true)) {
			LockSupport.park(ModuleMetricsVisitor.class);
		}
		waiters.remove();
	}

	protected void exit() {
		locked.set(false);
		LockSupport.unpark(waiters.peek());
	}

	protected abstract ModuleMetricsVisitor visitCriticalSection(
			String moduleName, String methodName);

	public ExecutorService getExecutor() {
		return executor;
	}

	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}

}
