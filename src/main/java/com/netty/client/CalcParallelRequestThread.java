package com.netty.client;

import java.util.concurrent.CountDownLatch;

import com.netty.server.core.MessageSendExecutor;
import com.netty.server.servicebean.Caculate;

public class CalcParallelRequestThread implements Runnable {

	private CountDownLatch signal;

	private CountDownLatch finish;

	private int taskNumber = 0;

	public CalcParallelRequestThread(CountDownLatch signal,
			CountDownLatch finish, int taskNumber) {
		this.signal = signal;
		this.finish = finish;
		this.taskNumber = taskNumber;
	}

	@Override
	public void run() {
		try {
			signal.await();
			Caculate calc = MessageSendExecutor.execute(Caculate.class);
			int add = calc.add(taskNumber, taskNumber);
			System.out.println("calc add result£º[" + add + "]");
			finish.countDown();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
