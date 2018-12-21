package com.netty.server.policy;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscardedPolicy implements RejectedExecutionHandler {
	private static final Logger LOG = LoggerFactory
			.getLogger(DiscardedPolicy.class);
	private String threadName;

	public DiscardedPolicy() {
		this(null);
	}

	public DiscardedPolicy(String threadName) {
		this.threadName = threadName;
	}

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		if (threadName != null) {
			LOG.error("RPC Thread pool [{}] is exhausted, executor={}",
					threadName, executor.toString());
		}
		if (!executor.isShutdown()) {
			BlockingQueue<Runnable> queue = executor.getQueue();
			int discardSize = queue.size() >> 1;
			for (int i = 0; i < discardSize; i++) {
				// 队列里面拿到对头,并删除,不阻塞
				queue.poll();
			}
			queue.offer(r);
		}
	}

}
