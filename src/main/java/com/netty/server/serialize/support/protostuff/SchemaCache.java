package com.netty.server.serialize.support.protostuff;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class SchemaCache {

	private static class SchemaSingleton {
		private static SchemaCache schema = new SchemaCache();
	}

	public static SchemaCache getInstance() {
		return SchemaSingleton.schema;
	}

	// 谷歌的缓存框架,怎样确保都放入的是同一个cache,SchemaCache单例
	private Cache<Class<?>, Schema<?>> cache = CacheBuilder.newBuilder()
			.maximumSize(1024).expireAfterWrite(1, TimeUnit.HOURS).build();

	private Schema<?> get(final Class<?> cls, Cache<Class<?>, Schema<?>> cache) {
		try {
			return cache.get(cls, new Callable<RuntimeSchema<?>>() {
				@Override
				public RuntimeSchema<?> call() throws Exception {
					return RuntimeSchema.createFrom(cls);
				}
			});
		} catch (ExecutionException e) {
			return null;
		}
	}

	public Schema<?> get(final Class<?> cls) {
		return get(cls, cache);
	}
}
