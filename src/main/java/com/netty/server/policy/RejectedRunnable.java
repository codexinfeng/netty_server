package com.netty.server.policy;

public interface RejectedRunnable extends Runnable {

	void rejected();
}
