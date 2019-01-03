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
	
	public HashCriticalSection(){
		boolean fair = RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_LOCK_FAIR == 1;
		
	}
}
