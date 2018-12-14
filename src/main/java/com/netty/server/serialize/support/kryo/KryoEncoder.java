package com.netty.server.serialize.support.kryo;

import com.netty.server.serialize.support.MessageCodeUtil;
import com.netty.server.serialize.support.MessageEncoder;

public class KryoEncoder extends MessageEncoder {

	public KryoEncoder(MessageCodeUtil util) {
		super(util);
	}

}
