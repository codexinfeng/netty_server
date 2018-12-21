package com.netty.server.serialize.support.protostuff;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class ProtostuffSerializePool {
	private GenericObjectPool<ProtostuffSerialize> pool;
	// 当前自己定义的pool对象,只是对三方的GenericObjectPool做封装,确保只有一个实例
	private volatile static ProtostuffSerializePool poolFactory;

	private ProtostuffSerializePool() {
		pool = new GenericObjectPool<ProtostuffSerialize>(
				new ProtostuffSerializeFactory());
	}

	public static ProtostuffSerializePool getProtostuffPoolFactory() {
		if (poolFactory == null) {
			synchronized (ProtostuffSerializePool.class) {
				if (poolFactory == null) {
					poolFactory = new ProtostuffSerializePool();
				}
			}
		}
		return poolFactory;
	}

	// 设置三方pool的属性,两个实例化方法,有什么用
	public ProtostuffSerializePool(final int maxTotal, final int minIdle,
			final long maxWaitMillis, final long minEvictableIdleTimeMillis) {
		pool = new GenericObjectPool<ProtostuffSerialize>(
				new ProtostuffSerializeFactory());
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxTotal(maxTotal);
		config.setMinIdle(minIdle);
		config.setMaxWaitMillis(maxWaitMillis);
		config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		pool.setConfig(config);
	}

	public ProtostuffSerialize borrow() {
		try {
			return getPool().borrowObject();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public void restore(final ProtostuffSerialize object) {
		getPool().returnObject(object);
	}

	public GenericObjectPool<ProtostuffSerialize> getPool() {
		return pool;
	}

}
