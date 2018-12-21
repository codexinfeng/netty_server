package com.netty.server.serialize.support.protostuff;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class ProtostuffSerializeFactory extends
		BasePooledObjectFactory<ProtostuffSerialize> {

	@Override
	public ProtostuffSerialize create() throws Exception {
		return createProtostuffSerialize();
	}

	@Override
	public PooledObject<ProtostuffSerialize> wrap(ProtostuffSerialize obj) {
		return new DefaultPooledObject<ProtostuffSerialize>(obj);
	}

	private ProtostuffSerialize createProtostuffSerialize() {
		return new ProtostuffSerialize();
	}

}
