package com.netty.server.serialize.support.hessian;

import com.netty.server.serialize.support.MessageCodeUtil;
import com.netty.server.serialize.support.MessageDecoder;

public class HessianDecoder extends MessageDecoder {

	public HessianDecoder(MessageCodeUtil util) {
		super(util);
	}

}
