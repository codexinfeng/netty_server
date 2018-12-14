package com.netty.server.serialize.support;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * RPC消息对象编码成二进制流
 * 
 * @author JZG
 *
 */
public class MessageEncoder extends MessageToByteEncoder<Object> {

	private MessageCodeUtil util = null;

	public MessageEncoder(final MessageCodeUtil util) {
		this.util = util;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out)
			throws Exception {

		util.encode(out, msg);

	}

}
