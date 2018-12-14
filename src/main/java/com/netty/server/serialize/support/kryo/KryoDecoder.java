package com.netty.server.serialize.support.kryo;

import com.netty.server.serialize.support.MessageCodeUtil;
import com.netty.server.serialize.support.MessageDecoder;

public class KryoDecoder extends MessageDecoder {

	public KryoDecoder(MessageCodeUtil util) {
		super(util);
	}

}
