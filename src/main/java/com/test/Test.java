package com.test;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.locks.LockSupport;

public class Test {

	public static void main(String[] args) {
//		SynchronousQueue<Integer> squeue = new SynchronousQueue<Integer>();
//		squeue.offer(1);
//		squeue.offer(2);
//		squeue.offer(3);
//		System.out.println(squeue.size());
//		try {
//			System.out.println(squeue.take());
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		};
		LockSupport.park(Test.class);

	}

}
