package com.netty.server.async;

import java.util.Map;

import com.google.common.collect.Maps;

public class AsyncProxyCache {

	private static Map<String, Class<?>> cache = Maps.newConcurrentMap();

	public static Class<?> get(String key) {
		return cache.get(key);
	}

	// 装入的是代理类
	public static void save(String key, Class<?> clazz) {
		// 不包含放入map,包含直接过
		if (!cache.containsKey(key)) {
			synchronized (cache) {
				if (!cache.containsKey(key)) {
					cache.put(key, clazz);
				}
			}
		}
	}

}
