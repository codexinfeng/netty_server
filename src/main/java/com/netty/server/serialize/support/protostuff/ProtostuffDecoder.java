package com.netty.server.serialize.support.protostuff;

import com.netty.server.serialize.support.MessageCodeUtil;
import com.netty.server.serialize.support.MessageDecoder;

public class ProtostuffDecoder extends MessageDecoder {

	public ProtostuffDecoder(MessageCodeUtil util) {
		super(util);
	}

}
