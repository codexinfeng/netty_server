package com.netty.server.exception;

public class InvokeModuleException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvokeModuleException() {
		super();
	}

	public InvokeModuleException(String message) {
		super(message);
	}

	public InvokeModuleException(Throwable cause) {
		super(cause);
	}

	public InvokeModuleException(String message, Throwable cause) {
		super(message, cause);
	}
}
