package com.netty.server.serialize.support.kryo;

import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.netty.server.model.MessageRequest;
import com.netty.server.model.MessageResponse;

public class KryoPoolFactory {

	private static KryoPoolFactory poolFactory = null;

	private KryoFactory factory = new KryoFactory() {

		@Override
		public Kryo create() {
			Kryo kryo = new Kryo();
			kryo.setReferences(false);
			// ����֪�Ļ���ע�ᵽkryoע��������,������к�/�����л�Ч��
			kryo.register(MessageRequest.class);
			kryo.register(MessageResponse.class);
			kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
			return kryo;
		}

	};

	private KryoPoolFactory() {
	}

	private KryoPool pool = new KryoPool.Builder(factory).build();

	public static KryoPool getKryoPoolInstance() {
		if (poolFactory == null) {
			synchronized (KryoPoolFactory.class) {
				if (poolFactory == null) {
					poolFactory = new KryoPoolFactory();
				}
			}
		}
		return poolFactory.getPool();

	}

	public KryoPool getPool() {
		return pool;
	}

}