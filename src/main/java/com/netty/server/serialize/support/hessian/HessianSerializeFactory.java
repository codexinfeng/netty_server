package com.netty.server.serialize.support.hessian;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * 对Hessian序列号/反序列化类进行池化处理
 * 
 * @author JZG
 *
 */
public class HessianSerializeFactory extends
		BasePooledObjectFactory<HessianSerialize> {

	@Override
	public HessianSerialize create() throws Exception {
		return createHessian();
	}

	@Override
	public PooledObject<HessianSerialize> wrap(HessianSerialize hessian) {
		return new DefaultPooledObject<HessianSerialize>(hessian);
	}

	private HessianSerialize createHessian() {
		return new HessianSerialize();
	}

}
