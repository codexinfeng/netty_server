package com.netty.server.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class MessageResponse {

	/**
	 * 
	 */
	// private static final long serialVersionUID = 3800819758626036488L;

	private String messageId;

	private String error;

	private Object resultDesc;

	private boolean returnNotNull;

	public boolean isReturnNotNull() {
		return returnNotNull;
	}

	public void setReturnNotNull(boolean returnNotNull) {
		this.returnNotNull = returnNotNull;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Object getResultDesc() {
		return resultDesc;
	}

	public void setResultDesc(Object resultDesc) {
		this.resultDesc = resultDesc;
	}

	@Override
	public String toString() {
		// return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
		// .append("messageId", messageId).append("error", error)
		// .toString();
		return ReflectionToStringBuilder.toString(this);
	}

}
