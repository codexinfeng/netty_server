package com.netty.server.exception;

public class CreateProxyException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1720276705566488458L;

	public CreateProxyException() {
		super();
	}

	public CreateProxyException(String message) {
		super(message);
	}

	public CreateProxyException(Throwable cause) {
		super(cause);
	}

	public CreateProxyException(String message, Throwable cause) {
		super(message, cause);
	}
}
