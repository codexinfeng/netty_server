package com.netty.server.serialize.support;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 二进制流反序列化成消息对象
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
		// 粘包导致消息头长度不对,直接返回
		if (in.readableBytes() < MessageDecoder.MESSAGE_LENGTH) {
			return;
		}
		in.markReaderIndex();
		// 读取消息的内容长度
		int messageLength = in.readInt();
		if (messageLength < 0) {
			ctx.close();
		}
		// 读到的消息长度和报文头的已知长度不匹配,那就重置一下ByteBuf读索引的位置
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
