package com.netty.server.core;

public enum RpcSerializerProtocol {

	JDK_SERIALLZE("jdknative"), KRYO_SERIALIZE("kryo"), HESSIAN_SERIALIZE(
			"hessian");
	private String serializeProtocol;

	private RpcSerializerProtocol(String serializeProtocol) {
		this.serializeProtocol = serializeProtocol;
	}

	public String getProtocol() {
		return serializeProtocol;
	}
}
