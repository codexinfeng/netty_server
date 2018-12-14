package com.netty.client;

import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.time.StopWatch;

import com.netty.server.core.MessageSendExecutor;

public class RpcRaralleTest {

	public static void main(String[] args) throws InterruptedException {

		final MessageSendExecutor executor = new MessageSendExecutor(
				"127.0.0.1:18888");

		int parallel = 2;

		StopWatch sw = new StopWatch();
		sw.start();

		CountDownLatch signal = new CountDownLatch(1);
		CountDownLatch finish = new CountDownLatch(parallel);
		for (int index = 1; index < parallel; index++) {
			CalcParallelRequestThread client = new CalcParallelRequestThread(
					signal, finish, index);
			new Thread(client).start();
		}

		signal.countDown();
		finish.await();
		sw.stop();
		String tip = String.format("RPC�����ܹ���ʱ: [%s]����", sw.getTime());
		System.out.println(tip);
		executor.stop();

	}

}
