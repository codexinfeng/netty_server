package com.netty.server.parallel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import com.netty.server.core.RpcSystemConfig;

public class HashCriticalSection {

	private static Integer partition = RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_HASH_NUMS;
	private final Map<Integer, Semaphore> criticalSectionMap = new ConcurrentHashMap<>();
	public final static long BASIC = 0xcbf29ce484222325L;
	public final static long PRIME = 0x100000001b3L;

	public HashCriticalSection() {
		boolean fair = RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_LOCK_FAIR == 1;
		init(null, fair);
	}

	public HashCriticalSection(Integer counts, boolean fair) {
		init(counts, fair);
	}

	private void init(Integer counts, boolean fair) {
		if (counts != null) {
			partition = counts;
		}
		for (int i = 0; i < partition; i++) {
			criticalSectionMap.put(i, new Semaphore(1, fair));
		}
	}

	public void exit(String key) {
		int hashKey = hash(key);
		Semaphore smaphore = criticalSectionMap.get(hashKey);
		smaphore.release();
	}

	public void enter(String key) {
		int hashKey = hash(key);
		Semaphore semaphore = criticalSectionMap.get(hashKey);
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void enter(int hashKey){
		Semaphore semaphore = criticalSectionMap.get(hashKey);
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int hash(String key) {
		return Math.abs((int) fnv1a64(key) % partition);
	}

	public static long fnv1a64(String key) {
		long hashCode = BASIC;
		for (int i = 0; i < key.length(); ++i) {
			char ch = key.charAt(i);
			if (ch >= 'A' && ch <= 'Z') {
				ch = (char) (ch + 32);
			}
			hashCode ^= ch;
			hashCode *= PRIME;
		}
		return hashCode;
	}

	public void exit(int hashKey) {
		Semaphore semaphore = criticalSectionMap.get(hashKey);
		semaphore.release();
	}
}
