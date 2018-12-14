package com.netty.server.serialize.support;

import io.netty.channel.ChannelPipeline;

import com.netty.server.core.RpcSerializerProtocol;

public interface RpcSerializeFrame {

	/**
	 * ѡ��������кŵ�Э��
	 * 
	 * @param protocol
	 * @param pipeline
	 */
	public void select(RpcSerializerProtocol protocol, ChannelPipeline pipeline);
}
