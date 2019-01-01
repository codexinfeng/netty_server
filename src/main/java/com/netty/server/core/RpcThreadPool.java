package com.netty.server.core;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;

import com.netty.server.enums.BlockingQueueType;
import com.netty.server.enums.RejectedPolicyType;
import com.netty.server.jmx.ThreadPoolMonitorProvider;
import com.netty.server.jmx.ThreadPoolStatus;
import com.netty.server.policy.AbortPolicy;
import com.netty.server.policy.BlockingPolicy;
import com.netty.server.policy.CallerRunspolicy;
import com.netty.server.policy.DiscardedPolicy;
import com.netty.server.policy.RejectedPolicy;

/**
 * �̳߳ط�װ
 * 
 * @author JZG
 *
 */
public class RpcThreadPool {

	private static final Timer TIMER = new Timer("ThreadPoolMonitor", true);

	private static long monitorDelay = 100L;

	private static long monitorPeriod = 300L;

	private static RejectedExecutionHandler createPolicy() {

		RejectedPolicyType rejectedPolicyType = RejectedPolicyType
				.fromString(System
						.getProperty(
								RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_REJECTED_POLICY_ATTR,
								"AbortPolicy"));
		switch (rejectedPolicyType) {
		case BLOCKING_POLICY:
			return new BlockingPolicy();
		case CALLER_RUNS_POLICY:
			return new CallerRunspolicy();
		case ABORT_POLICY:
			return new AbortPolicy();
		case REJECTED_POLICY:
			return new RejectedPolicy();
		case DISCARDED_POLICY:
			return new DiscardedPolicy();
		}
		return null;
	}

	private static BlockingQueue<Runnable> createBlockingQueue(int queues) {
		BlockingQueueType queueType = BlockingQueueType
				.fromString(System
						.getProperty(
								RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_QUEUE_NAME_ATTR,
								"LinkedBlockingQueue"));

		switch (queueType) {
		case LINKED_BLOCKING_QUEUE:
			return new LinkedBlockingQueue<Runnable>();
		case ARRAY_BLOCKING_QUEUE:
			return new ArrayBlockingQueue<Runnable>(RpcSystemConfig.PARALLEL
					* queues);
		case SYNCHRONOUS_QUEUE:
			return new SynchronousQueue<Runnable>();
		}
		return null;
	}

	public static Executor getExecutor(int threads, int queues) {
		String name = "RpcThreadPool";
		return new ThreadPoolExecutor(threads, threads, 0,
				TimeUnit.MICROSECONDS, createBlockingQueue(queues),
				new NameThreadFactory(name, true), createPolicy());
	}

	public static Executor getExecutorWithJmx(int threads, int queues) {
		final ThreadPoolExecutor executor = (ThreadPoolExecutor) getExecutor(
				threads, queues);
		TIMER.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				ThreadPoolStatus status = new ThreadPoolStatus();
				status.setPoolSize(executor.getPoolSize());
				status.setActiveCount(executor.getActiveCount());
				status.setCorePoolSize(executor.getCorePoolSize());
				status.setMaximumPoolSize(executor.getMaximumPoolSize());
				status.setLargestPoolSize(executor.getLargestPoolSize());
				status.setTaskCount(executor.getTaskCount());
				status.setCompletedTaskCount(executor.getCompletedTaskCount());

				try {
					ThreadPoolMonitorProvider.monitor(status);
				} catch (MalformedObjectNameException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstanceNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MBeanException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ReflectionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, monitorDelay, monitorPeriod);
		return executor;
	}
}
