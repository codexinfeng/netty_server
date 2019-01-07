package com.netty.server.serialize.support;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 
 * @author JZG
 *
 */
public class MessageDecoder extends ByteToMessageDecoder {

	public final static int MESSAGE_LENGTH = MessageCodeUtil.MESSAGE_LENGTH;

	private MessageCodeUtil util = null;

	public MessageDecoder(MessageCodeUtil util) {
		this.util = util;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		if (in.readableBytes() < MessageDecoder.MESSAGE_LENGTH) {
			return;
		}
		in.markReaderIndex();
		int messageLength = in.readInt();
		if (messageLength < 0) {
			ctx.close();
		}
		if (in.readableBytes() < messageLength) {
			in.resetReaderIndex();
			return;
		} else {
			byte[] messageBody = new byte[messageLength];
			in.readBytes(messageBody);
			try {
				Object obj = util.decode(messageBody);
				out.add(obj);
			} catch (Exception e) {
				Logger.getLogger(MessageDecoder.class.getName()).log(
						Level.SEVERE, null, e);
			}

		}

	}

}
