package com.netty.server.exception;

public class RejectResponseException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RejectResponseException() {
		super();
	}

	public RejectResponseException(String message) {
		super(message);
	}

	public RejectResponseException(Throwable cause) {
		super(cause);
	}

	public RejectResponseException(String message, Throwable cause) {
		super(message, cause);
	}
}
