package com.netty.server.jmx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.netty.server.core.ReflectionUtils;
import com.netty.server.core.RpcSystemConfig;
import com.netty.server.netty.MessageRecvExecutor;

public class HashModuleMetricsVisitor {
	private List<List<ModuleMetricsVisitor>> hashVisitorList = new ArrayList<>();
	private static final HashModuleMetricsVisitor INSTANCE = new HashModuleMetricsVisitor();

	private HashModuleMetricsVisitor() {
		init();
	}

	public static HashModuleMetricsVisitor getInstance() {
		return INSTANCE;
	}

	public int getHashModuleMetricsVisitorListSize() {
		return hashVisitorList.size();
	}

	private void init() {
		Map<String, Object> map = MessageRecvExecutor.getInstance()
				.getHandlerMap();
		ReflectionUtils utils = new ReflectionUtils();
		Set<String> s = map.keySet();
		Iterator<String> iter = s.iterator();
		String key;
		while (iter.hasNext()) {
			key = iter.next();
			try {
				List<String> list = utils.getClassMethodSignature(Class
						.forName(key));
				for (String signature : list) {
					List<ModuleMetricsVisitor> visitorList = new ArrayList<>();
					for (int i = 0; i < RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_HASH_NUMS; i++) {
						ModuleMetricsVisitor visitor = new ModuleMetricsVisitor(
								key, signature);
						visitor.setHashKey(i);
						visitorList.add(visitor);
					}
					hashVisitorList.add(visitorList);
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void signal() {
		// ModuleMetricsHandler
	}

	public List<List<ModuleMetricsVisitor>> getHashVisitorList() {
		return hashVisitorList;
	}

	public void setHashVisitorList(
			List<List<ModuleMetricsVisitor>> hashVisitorList) {
		this.hashVisitorList = hashVisitorList;
	}

}
