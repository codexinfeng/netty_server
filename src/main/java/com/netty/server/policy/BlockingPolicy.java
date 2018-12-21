package com.netty.server.policy;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockingPolicy implements RejectedExecutionHandler {

	private static final Logger LOG = LoggerFactory
			.getLogger(BlockingPolicy.class);

	private String threadName;

	public BlockingPolicy() {
		this(null);
	}

	public BlockingPolicy(String threadName) {
		this.threadName = threadName;
	}

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		if (threadName != null) {
			LOG.error("RPC Thread pool[{}] is exhausted,executor={}",
					threadName, executor.toString());
			if (!executor.isShutdown()) {
				try {
					executor.getQueue().put(r);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
