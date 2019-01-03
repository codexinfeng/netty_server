package com.netty.server.serialize.support;

import io.netty.channel.ChannelPipeline;

import com.netty.server.enums.RpcSerializerProtocol;

public interface RpcSerializeFrame {

	/**
	 * 
	 * @param protocol
	 * @param pipeline
	 */
	public void select(RpcSerializerProtocol protocol, ChannelPipeline pipeline);
}
