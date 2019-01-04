package com.netty.server.core;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.netty.server.model.MessageRequest;
import com.netty.server.model.MessageResponse;

public class MessageCallBack {

	private MessageRequest request;

	private MessageResponse response;

	private Lock lock = new ReentrantLock();

	private Condition finish = lock.newCondition();

	public MessageCallBack(MessageRequest request) {
		this.request = request;
	}

	public Object start() throws InterruptedException {

		try {
			lock.lock();
			finish.await(10 * 1000, TimeUnit.MICROSECONDS);
			if (this.response != null) {
				return this.response.getResultDesc();
			} else {
				return null;
			}
		} finally {
			lock.unlock();
		}

	}

	public void over(MessageResponse response) {

		try {
			lock.lock();
			finish.signal();
			this.response = response;
		} finally {
			lock.unlock();
		}

	}

}
