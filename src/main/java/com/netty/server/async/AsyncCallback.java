package com.netty.server.async;

public interface AsyncCallback<R> {

	R call();
}
