package com.netty.server.jmx;

import java.util.List;

public interface ModuleMetricsVistorMXBean {

	List<ModuleMetricsVisitor> getModuleMetricsVisitor();

	void addModuleMetricsVisitor(ModuleMetricsVisitor visitor);
}
