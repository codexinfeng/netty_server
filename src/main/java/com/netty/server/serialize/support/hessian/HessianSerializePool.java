package com.netty.server.serialize.support.hessian;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class HessianSerializePool {

	// 为了避免重复产生对象,提高JVM内存利用率,引入对象池技术,高并发序列化/反序列号场景,效率提升
	private GenericObjectPool<HessianSerialize> hessianPool;
	private static HessianSerializePool poolFactory = null;

	private HessianSerializePool() {
		hessianPool = new GenericObjectPool<HessianSerialize>(
				new HessianSerializeFactory());
	}

	public static HessianSerializePool getHessianPoolInstance() {
		if (poolFactory == null) {
			synchronized (HessianSerializePool.class) {
				poolFactory = new HessianSerializePool();
			}
		}
		return poolFactory;
	}

	public HessianSerializePool(final int maxTotal, final int minIdle,
			final long maxWaitMillis, final long minEvictableIdleTimeMillis) {
		hessianPool = new GenericObjectPool<HessianSerialize>(
				new HessianSerializeFactory());
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		// 最大池对象总数
		config.setMaxTotal(maxTotal);
		// 最小空闲数
		config.setMinIdle(minIdle);
		// 最大等待时间,默认-1 表示无线等待
		config.setMaxWaitMillis(maxWaitMillis);
		// 退出连接的最小空闲时间
		config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		hessianPool.setConfig(config);

	}

	public HessianSerialize borrow() {
		try {
			return getHessianPool().borrowObject();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public void restore(HessianSerialize object) {
		getHessianPool().returnObject(object);
	}

	public GenericObjectPool<HessianSerialize> getHessianPool() {
		return hessianPool;
	}

}
