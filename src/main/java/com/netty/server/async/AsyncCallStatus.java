package com.netty.server.async;

public class AsyncCallStatus {

	private long startTime;

	private long elapseTime;

	private CallStatus status;

	public AsyncCallStatus(long startTime, long elapseTime, CallStatus status) {
		this.startTime = startTime;
		this.elapseTime = elapseTime;
		this.status = status;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getElapseTime() {
		return elapseTime;
	}

	public void setElapseTime(long elapseTime) {
		this.elapseTime = elapseTime;
	}

	public CallStatus getStatus() {
		return status;
	}

	public void setStatus(CallStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "AsyncCallStatus [startTime=" + startTime + ", elapseTime="
				+ elapseTime + ", status=" + status + "]";
	}

}
