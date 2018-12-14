package com.netty.server.serialize.support.hessian;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class HessianSerializePool {

	// Ϊ�˱����ظ���������,���JVM�ڴ�������,�������ؼ���,�߲������л�/�����кų���,Ч������
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
		// ���ض�������
		config.setMaxTotal(maxTotal);
		// ��С������
		config.setMinIdle(minIdle);
		// ���ȴ�ʱ��,Ĭ��-1 ��ʾ���ߵȴ�
		config.setMaxWaitMillis(maxWaitMillis);
		// �˳����ӵ���С����ʱ��
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
