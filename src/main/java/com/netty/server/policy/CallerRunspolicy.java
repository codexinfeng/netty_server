package com.netty.server.policy;

import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallerRunspolicy extends ThreadPoolExecutor.CallerRunsPolicy {

	private static final Logger LOG = LoggerFactory
			.getLogger(CallerRunspolicy.class);
	private String threadName;

	public CallerRunspolicy() {
		this(null);
	}

	public CallerRunspolicy(String threadName) {
		this.threadName = threadName;
	}

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
		if (threadName != null) {
			LOG.error("RPC Thread Pool [{}] is exhausted,executor={}",
					threadName, e.toString());
		}
		super.rejectedExecution(r, e);
	}
}
