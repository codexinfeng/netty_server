package com.netty.server.policy;

import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbortPolicy extends ThreadPoolExecutor.AbortPolicy {
	private static final Logger LOG = LoggerFactory
			.getLogger(AbortPolicy.class);

	private String threadName;

	public AbortPolicy() {
		this(null);
	}

	public AbortPolicy(String threadName) {
		this.threadName = threadName;
	}

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
		if (threadName != null) {
			LOG.error("RPC Thread pool [{}] is exhausted,executor={}",
					threadName, e.toString());
		}
		String msg = String
				.format("RpcServer["
						+ " ThreadName: %s, Pool size: %d (active:%d,core:%d,"
						+ "max:%d,largest:%d),Task:%d(completed:%d),"
						+ "Executor status:(isShutdown:%s,isTerminated:%s,isTerminating:%s)",
						threadName, e.getPoolSize(), e.getActiveCount(),
						e.getCorePoolSize(), e.getMaximumPoolSize(),
						e.getLargestPoolSize(), e.getTaskCount(),
						e.getCompletedTaskCount(), e.isShutdown(),
						e.isTerminated(), e.isTerminating());
		System.out.println(msg);
		super.rejectedExecution(r, e);
	}
}
