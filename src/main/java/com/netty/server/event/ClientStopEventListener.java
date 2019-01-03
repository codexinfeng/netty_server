package com.netty.server.event;

import com.netty.server.netty.MessageSendExecutor;

public class ClientStopEventListener {

	public int lastMessage = 0;

	public void listener(ClientStopEvent event) {
		lastMessage = event.getMessage();
		MessageSendExecutor.getInstance().stop();
	}

	public int getLastMessage() {
		return lastMessage;
	}

}
