package com.netty.server.jmx;

import java.beans.ConstructorProperties;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

import javax.management.JMException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import com.alibaba.druid.util.Histogram;

public class ModuleMetricsVisitor {

	public static final long DEFAULT_INVOKE_MIN_TIMESPAN = 3600 * 1000L;
	private static final String[] THROWABLE_NAMES = { "message", "class",
			"stackTrace" };
	private static final String[] THROWABLE_DESCRIPTIONS = { "message",
			"class", "stackTrace" };
	private static final OpenType<?>[] THROWABLE_TYPES = new OpenType<?>[] {
			SimpleType.STRING, SimpleType.STRING, SimpleType.STRING };
	private static CompositeType THROWABLE_COMPOSITE_TYPE = null;

	private String moduleName;
	private String methodName;
	private volatile long invokeCount = 0L;
	private volatile long invokeSuccCount = 0L;
	private volatile long invokeFailCount = 0L;
	private volatile long invokeFilterCount = 0L;
	private long invokeTimespan = 0L;
	private long invokeMinTimespan = DEFAULT_INVOKE_MIN_TIMESPAN;
	private long invokeMaxTimespan = 0L;
	private long[] invokeHistogram;
	private Exception lastStackTrace;
	private String lastStackTraceDetail;
	private long lastErrorTime;
	private int hashKey = 0;
	private Histogram histogram = new Histogram(TimeUnit.MILLISECONDS,
			new long[] { 1, 10, 100, 1000, 10 * 100, 100 * 100, 1000 * 100 });
	// 统计某个类的具体属性
	private final AtomicLongFieldUpdater<ModuleMetricsVisitor> invokeCountUpdater = AtomicLongFieldUpdater
			.newUpdater(ModuleMetricsVisitor.class, "invokeCount");
	private final AtomicLongFieldUpdater<ModuleMetricsVisitor> invokeSuccCountUpdater = AtomicLongFieldUpdater
			.newUpdater(ModuleMetricsVisitor.class, "invokeSuccCount");
	private final AtomicLongFieldUpdater<ModuleMetricsVisitor> invokeFailCountUpdater = AtomicLongFieldUpdater
			.newUpdater(ModuleMetricsVisitor.class, "invokeFailCount");
	private final AtomicLongFieldUpdater<ModuleMetricsVisitor> invokeFilterCountUpdater = AtomicLongFieldUpdater
			.newUpdater(ModuleMetricsVisitor.class, "invokeFilterCount");

	@ConstructorProperties({ "moduleName", "methodName" })
	public ModuleMetricsVisitor(String moduleName, String methodName) {
		this.moduleName = moduleName;
		this.methodName = methodName;
		clear();
	}

	public void clear() {
		lastStackTraceDetail = "";
		invokeTimespan = 0L;
		invokeMinTimespan = DEFAULT_INVOKE_MIN_TIMESPAN;
		invokeMaxTimespan = 0L;
		lastErrorTime = 0L;
		lastStackTrace = null;
		invokeCountUpdater.set(this, 0);
		invokeSuccCountUpdater.set(this, 0);
		invokeFailCountUpdater.set(this, 0);
		invokeFilterCountUpdater.set(this, 0);
		histogram.reset();
	}

	public void reset() {
		moduleName = "";
		methodName = "";
		clear();
	}

	public void setErrorLastTimeLongVal(long lastErrorTime) {
		this.lastErrorTime = lastErrorTime;
	}

	public long getErrorLastTimeLongVal() {
		return lastErrorTime;
	}

	public String getLastErrorTime() {
		if (lastErrorTime <= 0) {
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date(lastErrorTime));
	}

	public String getLastStackTrace() {
		if (lastStackTrace == null) {
			return null;
		}
		StringWriter buf = new StringWriter();
		lastStackTrace.printStackTrace(new PrintWriter(buf));
		return buf.toString();
	}

	public String getStackTrace(Throwable ex) {
		StringWriter buf = new StringWriter();
		ex.printStackTrace(new PrintWriter(buf));
		return buf.toString();
	}

	public void setLastStackTrace(Exception lastStackTrace) {
		this.lastStackTrace = lastStackTrace;
		this.lastStackTraceDetail = getLastStackTrace();
		this.lastErrorTime = System.currentTimeMillis();
	}

	public void setLastStackTraceDetail(String lastStackTraceDetail) {
		this.lastStackTraceDetail = lastStackTraceDetail;
	}

	public String getLastStackTraceDetail() {
		return lastStackTraceDetail;
	}

	public CompositeType getThrowableCompositeType() throws JMException {
		if (THROWABLE_COMPOSITE_TYPE == null) {
			THROWABLE_COMPOSITE_TYPE = new CompositeType("Throwable",
					"Throwable", THROWABLE_NAMES, THROWABLE_DESCRIPTIONS,
					THROWABLE_TYPES);
		}
		return THROWABLE_COMPOSITE_TYPE;
	}

	public CompositeData buildErrorCompositeData(Throwable error)
			throws JMException {
		if (error == null) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>(512);
		map.put("class", error.getClass().getName());
		map.put("message", error.getMessage());
		map.put("stackTrace", getStackTrace(error));
		return new CompositeDataSupport(getThrowableCompositeType(), map);
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public long getInvokeCount() {
		return this.invokeCountUpdater.get(this);
	}

	public void setInvokeCount(long invokeCount) {
		this.invokeCountUpdater.set(this, invokeCount);
	}

	public long incrementInvokeCount() {
		return this.invokeCountUpdater.incrementAndGet(this);
	}

	public long getInvokeSuccCount() {
		return this.invokeCountUpdater.get(this);
	}

	public void setInvokeSuccCount(long invokeSuccCount) {
		this.invokeSuccCountUpdater.set(this, invokeSuccCount);
	}

	public long incrementInvokeSuccCount() {
		return this.invokeSuccCountUpdater.incrementAndGet(this);
	}

	public long getInvokeFailCount() {
		return this.invokeFailCountUpdater.get(this);
	}

	public void setInvokeFailCount(long invokeFailCount) {
		this.invokeFailCountUpdater.set(this, invokeFailCount);
	}

	public long getInvokeFilterCount() {
		return this.invokeFilterCountUpdater.get(this);
	}

	public void setInvokeFilterCount(long invokeFilterCount) {
		this.invokeFilterCountUpdater.set(this, invokeFilterCount);
	}

	public long incrementInvokeFilterCount() {
		return this.invokeFilterCountUpdater.incrementAndGet(this);
	}

	public Histogram getHistogram() {
		return histogram;
	}

	public void setHistogram(Histogram histogram) {
		this.histogram = histogram;
	}

	public long getInvokeTimespan() {
		return invokeTimespan;
	}

	public void setInvokeTimespan(long invokeTimespan) {
		this.invokeTimespan = invokeTimespan;
	}

	public long getInvokeMinTimespan() {
		return invokeMinTimespan;
	}

	public void setInvokeMinTimespan(long invokeMinTimespan) {
		this.invokeMinTimespan = invokeMinTimespan;
	}

	public long getInvokeMaxTimespan() {
		return invokeMaxTimespan;
	}

	public void setInvokeMaxTimespan(long invokeMaxTimespan) {
		this.invokeMaxTimespan = invokeMaxTimespan;
	}

	public long[] getInvokeHistogram() {
		return invokeHistogram;
	}

	public void setInvokeHistogram(long[] invokeHistogram) {
		this.invokeHistogram = invokeHistogram;
	}

	public int getHashKey() {
		return hashKey;
	}

	public void setHashKey(int hashKey) {
		this.hashKey = hashKey;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((moduleName == null) ? 0 : moduleName.hashCode());
		result = prime * result
				+ ((methodName == null) ? 0 : methodName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof ModuleMetricsVisitor)) {
			return false;
		}
		ModuleMetricsVisitor visitor = (ModuleMetricsVisitor) obj;
		return this.moduleName.equals(visitor.getModuleName())
				&& this.methodName.equals(visitor.getMethodName());
	}

	@Override
	public String toString() {
		String metrics = String
				.format("<<[moduleName:%s]-[methodName:%s]>> [invokeCount:%d][invokeSuccCount:%d][invokeFilterCount:%d][invokeTimespan:%d][invokeMinTimespan:%d][invokeMaxTimespan:%d][invokeFailCount:%d][lastErrorTime:%d][lastStackTraceDetail:%s]\n",
						moduleName, methodName, invokeCount, invokeSuccCount,
						invokeFilterCount, invokeTimespan, invokeMinTimespan,
						invokeMaxTimespan, invokeFailCount, lastErrorTime,
						lastStackTraceDetail);
		return metrics;
	}
}