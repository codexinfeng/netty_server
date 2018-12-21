package com.netty.server.serialize.support.protostuff;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.netty.server.model.MessageRequest;
import com.netty.server.model.MessageResponse;
import com.netty.server.serialize.support.RpcSerialize;

public class ProtostuffSerialize implements RpcSerialize {

	private static SchemaCache cachedSchema = SchemaCache.getInstance();

	// 运用不同的策略生成类的实例
	private static Objenesis objenesis = new ObjenesisStd(true);

	private boolean rpcDirect = false;

	@SuppressWarnings("unchecked")
	private static <T> Schema<T> getSchema(Class<T> cls) {
		return (Schema<T>) cachedSchema.get(cls);
	}

	@Override
	public void serialize(OutputStream output, Object object) {
		Class<?> cls = object.getClass();
		LinkedBuffer buffer = LinkedBuffer
				.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
		try {
			@SuppressWarnings("unchecked")
			Schema<Object> schema = (Schema<Object>) getSchema(cls);
			ProtostuffIOUtil.writeTo(output, object, schema, buffer);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		} finally {
			buffer.clear();
		}
	}

	@Override
	public Object deserialize(InputStream input) throws IOException {
		Class<?> cls = isRpcDirect() ? MessageRequest.class
				: MessageResponse.class;
		Object message = (Object) objenesis.newInstance(cls);
		@SuppressWarnings("unchecked")
		Schema<Object> schema = (Schema<Object>) getSchema(cls);
		ProtostuffIOUtil.mergeFrom(input, message, schema);
		return message;
	}

	public boolean isRpcDirect() {
		return rpcDirect;
	}

	public void setRpcDirect(boolean rpcDirect) {
		this.rpcDirect = rpcDirect;
	}

}
