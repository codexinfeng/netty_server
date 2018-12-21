package com.netty.server.enums;

/**
 * ¶ÓÁÐ¾Ü¾ø²ßÂÔ
 * 
 * @author JZG
 *
 */
public enum RejectedPolicyType {

	ABORT_POLICY("AbortPolicy"), BLOCKING_POLICY("BlockingPolicy"), CALLER_RUNS_POLICY(
			"CallerRunsPolicy"), DISCARDED_POLICY("DiscardedPolicy"), REJECTED_POLICY(
			"RejectedPolicy");

	private String value;

	private RejectedPolicyType(String value) {
		this.value = value;
	}

	public static RejectedPolicyType fromString(String value) {
		for (RejectedPolicyType type : RejectedPolicyType.values()) {
			if (type.getValue().equals(value)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Mismatched type with value:"
				+ value);
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value;
	}

}
