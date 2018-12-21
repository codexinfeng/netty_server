package com.netty.server.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * RPC请求的request对象
 * 
 * @author JZG
 *
 */
public class MessageRequest  {

	/**
	 * 
	 */
//	private static final long serialVersionUID = 8563410322239405135L;

	private String messageId;

	private String className;

	private String methodName;

	private Class<?>[] typeParameter;

	private Object[] parameterVal;

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class<?>[] getTypeParameter() {
		return typeParameter;
	}

	public void setTypeParameter(Class<?>[] typeParameter) {
		this.typeParameter = typeParameter;
	}

	public Object[] getParameterVal() {
		return parameterVal;
	}

	public void setParameterVal(Object[] parameterVal) {
		this.parameterVal = parameterVal;
	}

	@Override
	public String toString() {
		// return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
		// .append("messageId", messageId).append("className", className)
		// .append("methodName", methodName).toString();
		return ReflectionToStringBuilder.toStringExclude(this, new String[] {
				"typeParameter", "parameterVal" });
	}

}
