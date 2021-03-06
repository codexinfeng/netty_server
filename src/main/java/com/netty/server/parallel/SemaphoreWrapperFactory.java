package com.netty.server.parallel;

public class SemaphoreWrapperFactory extends SemaphoreWrapper {
	private static SemaphoreWrapperFactory INSTANCE = new SemaphoreWrapperFactory();

	public static SemaphoreWrapperFactory getInstance() {
		return INSTANCE;
	}

	private SemaphoreWrapperFactory() {
		super();
	}

	@Override
	public void acquire() {
		if (semaphore != null) {
			try {
				while (true) {
					boolean result = released.get();
					// 期望的值，更新的值
					if (released.compareAndSet(result, true)) {
						semaphore.acquire();
						break;
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void release() {
		if (semaphore != null) {
			while (true) {
				boolean result = released.get();
				if (released.compareAndSet(result, false)) {
					semaphore.release();
					break;
				}
			}
		}
	}
}
