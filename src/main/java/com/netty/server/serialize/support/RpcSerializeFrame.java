package com.netty.server.serialize.support;

import io.netty.channel.ChannelPipeline;

import com.netty.server.core.RpcSerializerProtocol;

public interface RpcSerializeFrame {

	/**
	 * 选择具体序列号的协议
	 * 
	 * @param protocol
	 * @param pipeline
	 */
	public void select(RpcSerializerProtocol protocol, ChannelPipeline pipeline);
}
