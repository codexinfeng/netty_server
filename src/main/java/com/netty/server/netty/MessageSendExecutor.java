package com.netty.server.netty;

import com.google.common.reflect.Reflection;
import com.netty.server.enums.RpcSerializerProtocol;

/**
 * �ͻ�����Ϣ����
 * 
 * @author JZG
 *
 */
public class MessageSendExecutor {

	// ����ģʽ
	private static class MessageSendExecutorSingle {
		private static final MessageSendExecutor sendExecutor = new MessageSendExecutor();
	}

	public static MessageSendExecutor getInstance() {
		return MessageSendExecutorSingle.sendExecutor;
	}

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
	// ����rpcInterface��Ĵ�����̬��������
	// @SuppressWarnings("unchecked")
	// public static <T> T execute(Class<T> rpcInterface) {
	// return (T) Proxy.newProxyInstance(rpcInterface.getClassLoader(),
	// new Class[] { rpcInterface },
	// new MessageSendProxy<T>(rpcInterface));
	// }

	public <T> T execute(Class<T> rpcInterface) {
		return (T) Reflection.newProxy(rpcInterface, new MessageSendProxy<T>());
	}
}
