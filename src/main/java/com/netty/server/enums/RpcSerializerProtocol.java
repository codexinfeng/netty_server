package com.netty.server.enums;

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
