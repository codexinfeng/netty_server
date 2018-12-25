package com.netty.server.core;

public class RpcSystemConfig {

	public static final String SYSTEM_PROPERTY_THREADPOOL_REJECTED_POLICY_ATTR = "nettyrpc.parallel.rejected.policy";

	public static final String SYSTEM_PROPERTY_THREADPOOL_QUEUE_NAME_ATTR = "nettyrpc.parallel.queue";

	public static final int PARALLEL = Math.max(2, Runtime.getRuntime()
			.availableProcessors());

	public static final int SYSTEM_PROPERTY_THREADPOOL_THREAD_NUMS = Integer
			.getInteger("nettyrpc.default.thread.nums", 16);

	public static final int SYSTEM_PROPERTY_THREADPOOL_QUEUE_NAME = Integer
			.getInteger("nettyrpc.default.queue.nums", -1);

	public static final String RPC_COMPILER_SPI_ATTR = "AccessAdaptive";
	public static final String RPC_ABILITY_DETAIL_SPI_STTR = "AbilityDetail";

	public static final int IPADDR_OPRT_ARRAY_LENGTH = 2;
	private static boolean monitorServerSupport = false;

	public static boolean isMonitorServerSupport() {
		return monitorServerSupport;
	}

	public static void setMonitorServerSupport(boolean monitorServerSupport) {
		RpcSystemConfig.monitorServerSupport = monitorServerSupport;
	}

}
