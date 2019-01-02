package com.netty.server.exception;

public class InvokeTimeoutException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvokeTimeoutException() {
		super();
	}

	public InvokeTimeoutException(String message) {
		super(message);
	}

	public InvokeTimeoutException(Throwable cause) {
		super(cause);
	}

	public InvokeTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}
}
