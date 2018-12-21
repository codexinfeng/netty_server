package com.netty.server.policy;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RejectedPolicy implements RejectedExecutionHandler {
	private static final Logger LOG = LoggerFactory
			.getLogger(RejectedPolicy.class);
	private String threadName;

	public RejectedPolicy() {
		this(null);
	}

	public RejectedPolicy(String threadName) {
		this.threadName = threadName;
	}

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		if (threadName != null) {
			LOG.error("RPC Thread pool [{}] is exhausted, executor={}",
					threadName, executor.toString());
		}
		if (r instanceof RejectedRunnable) {
			((RejectedRunnable) r).rejected();
		} else {
			if (!executor.isShutdown()) {
				BlockingQueue<Runnable> queue = executor.getQueue();
				int discardSize = queue.size() >> 1;
				// 队列中任务去除一般
				for (int i = 0; i < discardSize; i++) {
					queue.poll();
				}
				try {
					queue.put(r);// put会阻塞
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}
