package com.netty.server.serialize.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * –Ú¡–∫≈
 * 
 * @author JZG
 *
 */
public interface RpcSerialize {

	void serialize(OutputStream output, Object object) throws IOException;

	Object deserialize(InputStream input) throws IOException;
}
