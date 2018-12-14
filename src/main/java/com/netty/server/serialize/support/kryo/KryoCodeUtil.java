package com.netty.server.serialize.support.kryo;

import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.esotericsoftware.kryo.pool.KryoPool;
import com.google.common.io.Closer;
import com.netty.server.serialize.support.MessageCodeUtil;

public class KryoCodeUtil implements MessageCodeUtil {

	private KryoPool pool;

	private static Closer closer = Closer.create();

	public KryoCodeUtil(KryoPool pool) {
		this.pool = pool;
	}

	@Override
	public void encode(ByteBuf out, Object message) throws IOException {
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			closer.register(byteArrayOutputStream);
			KryoSerialize kryoSerialize = new KryoSerialize(pool);
			kryoSerialize.serialize(byteArrayOutputStream, message);
			byte[] body = byteArrayOutputStream.toByteArray();
			int dataLength = body.length;
			out.writeInt(dataLength);
			out.writeBytes(body);
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
			KryoSerialize kryoSerialize = new KryoSerialize(pool);
			Object obj = kryoSerialize.deserialize(byteArrayInputStream);
			return obj;
		} finally {
			closer.close();
		}
	}

}
