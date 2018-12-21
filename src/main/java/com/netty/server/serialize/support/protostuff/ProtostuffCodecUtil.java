package com.netty.server.serialize.support.protostuff;

import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.google.common.io.Closer;
import com.netty.server.serialize.support.MessageCodeUtil;

public class ProtostuffCodecUtil implements MessageCodeUtil {

	private static Closer closer = Closer.create();
	private ProtostuffSerializePool pool = ProtostuffSerializePool
			.getProtostuffPoolFactory();
	private boolean rpcDirect = false;

	// �ӿͻ����õ��ľ���ByteBuf����
	@Override
	public void encode(final ByteBuf out, final Object message)
			throws IOException {
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			closer.register(byteArrayOutputStream);
			// ProtostuffSerialize ��ֻ�Ǹ����кŻ�����,�������������кŹ���������л�
			ProtostuffSerialize protostuffSerialize = pool.borrow();
			protostuffSerialize.serialize(byteArrayOutputStream, message);
			byte[] body = byteArrayOutputStream.toByteArray();
			int dataLength = body.length;
			out.writeInt(dataLength);
			out.writeBytes(body);
			pool.restore(protostuffSerialize);
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
			ProtostuffSerialize protostuffSerialize = pool.borrow();
			protostuffSerialize.setRpcDirect(rpcDirect);
			Object obj = protostuffSerialize.deserialize(byteArrayInputStream);
			pool.restore(protostuffSerialize);
			return obj;
		} finally {
			closer.close();
		}
	}

	public boolean isRpcDirect() {
		return rpcDirect;
	}

	public void setRpcDirect(boolean rpcDirect) {
		this.rpcDirect = rpcDirect;
	}

}
