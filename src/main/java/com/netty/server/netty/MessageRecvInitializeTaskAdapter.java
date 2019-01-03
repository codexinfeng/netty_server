package com.netty.server.netty;

import java.util.Map;

import com.netty.server.model.MessageRequest;
import com.netty.server.model.MessageResponse;

public class MessageRecvInitializeTaskAdapter extends
		AbstractMessageRecvInitializeTask {

	public MessageRecvInitializeTaskAdapter(MessageRequest request,
			MessageResponse response, Map<String, Object> handlerMap) {
		super(request, response, handlerMap);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void injectInvoke() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void injectSuccInvoke(long invokeTimespan) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void injectFailInvoke(Throwable error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void injectFilterInvoke() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void acquire() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void release() {
		// TODO Auto-generated method stub
		
	}

}
