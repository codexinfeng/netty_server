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

	// 从客户端拿到的就是ByteBuf对象
	@Override
	public void encode(final ByteBuf out, final Object message)
			throws IOException {
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			closer.register(byteArrayOutputStream);
			// ProtostuffSerialize 就只是个序列号化工具,编解码就是用序列号工具完成序列化
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
