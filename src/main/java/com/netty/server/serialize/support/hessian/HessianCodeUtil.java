package com.netty.server.serialize.support.hessian;

import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.google.common.io.Closer;
import com.netty.server.serialize.support.MessageCodeUtil;

public class HessianCodeUtil implements MessageCodeUtil {

	HessianSerializePool pool = HessianSerializePool.getHessianPoolInstance();
	private static Closer closer = Closer.create();

	public HessianCodeUtil() {

	}

	@Override
	public void encode(ByteBuf out, Object message) throws IOException {
		try {
			ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
			closer.register(byteOutputStream);
			HessianSerialize hessianSerialize = pool.borrow();
			hessianSerialize.serialize(byteOutputStream, message);
			byte[] body = byteOutputStream.toByteArray();
			int dataLength = body.length;
			out.writeInt(dataLength);
			out.writeBytes(body);
			pool.restore(hessianSerialize);
		} finally {
			closer.close();
		}
	}

	@Override
	public Object decode(byte[] body) throws IOException {
		try {
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
					body);
			closer.register(byteArrayInputStream);
			HessianSerialize hessianSerialize = pool.borrow();
			Object object = hessianSerialize.deserialize(byteArrayInputStream);
			pool.restore(hessianSerialize);
			return object;
		} finally {
			closer.close();
		}
	}

}
