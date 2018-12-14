package com.netty.server.core;

import com.google.common.reflect.Reflection;

/**
 * 客户端消息发送
 * 
 * @author JZG
 *
 */
public class MessageSendExecutor {

	private RpcServerLoader loader = RpcServerLoader.getInstance();

	public MessageSendExecutor() {
	}

	public MessageSendExecutor(String serverAddress) {
		loader.load(serverAddress, RpcSerializerProtocol.JDK_SERIALLZE);
	}

	public MessageSendExecutor(String serverAddress,
			RpcSerializerProtocol protocol) {
		loader.load(serverAddress, protocol);
	}

	public void setRpcServerLoader(String serverAddress,
			RpcSerializerProtocol protocol) {
		loader.load(serverAddress, protocol);
	}

	public void stop() {
		loader.unLoad();
	}

	// V1
	// 创建rpcInterface类的代理（动态创建代理）
	// @SuppressWarnings("unchecked")
	// public static <T> T execute(Class<T> rpcInterface) {
	// return (T) Proxy.newProxyInstance(rpcInterface.getClassLoader(),
	// new Class[] { rpcInterface },
	// new MessageSendProxy<T>(rpcInterface));
	// }

	public static <T> T execute(Class<T> rpcInterface) {
		return (T) Reflection.newProxy(rpcInterface, new MessageSendProxy<T>());
	}
}
