package com.netty.server.serialize.support.protostuff;

import com.netty.server.serialize.support.MessageCodeUtil;
import com.netty.server.serialize.support.MessageEncoder;

public class ProtostuffEncoder extends MessageEncoder {

	public ProtostuffEncoder(MessageCodeUtil util) {
		super(util);
	}

}
