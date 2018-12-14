package com.netty.server.core;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * �̳߳ط�װ
 * 
 * @author JZG
 *
 */
public class RpcThreadPool {

	public static Executor getExecutor(int threads, int queues) {
		String name = "RpcThreadPool";
		return new ThreadPoolExecutor(threads, threads, 0,
				TimeUnit.MICROSECONDS, queues == 0 ? new SynchronousQueue<>()
						: (queues < 0 ? new LinkedBlockingQueue<Runnable>()
								: new LinkedBlockingQueue<>(queues)),
				new NameThreadFactory(name, true), new AbortPolicyWithReport(
						name));
	}
}
