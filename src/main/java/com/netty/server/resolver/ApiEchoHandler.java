package com.netty.server.resolver;

import com.netty.server.core.RpcSystemConfig;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;

public class ApiEchoHandler extends ChannelInboundHandlerAdapter {

	private static final String CONTENT_TYPE = "Content-Type";
	private static final String CONTENT_LENGTH = "Content-Length";
	private static final String CONNECTION = "Connection";
	private static final String KEEP_ALIVE = "keep-alive";
	private static final String METRICS = "metrics";
	private static final String METRICS_ERR_MSG = "NettyRpc nettyrpc.jmx.invoke.metrics attribute is closed!";

	public ApiEchoHandler() {
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof HttpRequest) {
			HttpRequest req = (HttpRequest) msg;

		}
	}

	private byte[] buildResponseMsg(HttpRequest req) {
		byte[] content = null;
		boolean metrics = (req.getUri().indexOf(METRICS) != -1);
		if (RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_SUPPORT && metrics) {
		}
	}
}
