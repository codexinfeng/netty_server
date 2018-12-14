package com.netty.server.serialize.support;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * RPC ±‡Ω‚¬Î
 * 
 * @author JZG
 *
 */
public interface MessageCodeUtil {

	public final int MESSAGE_LENGTH = 4;

	/**
	 * º”√‹
	 * 
	 * @param out
	 * @param message
	 * @throws IOException
	 */
	public void encode(final ByteBuf out, final Object message)
			throws IOException;

	/**
	 * Ω‚√‹
	 * 
	 * @param body
	 * @return
	 * @throws IOException
	 */
	public Object decode(byte[] body) throws IOException;

}
