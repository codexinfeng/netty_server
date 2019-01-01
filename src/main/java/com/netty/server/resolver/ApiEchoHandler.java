package com.netty.server.resolver;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.io.UnsupportedEncodingException;

import com.netty.server.core.RpcSystemConfig;
import com.netty.server.jmx.ModuleMetricsProcessor;
import com.netty.server.view.AbilityDetailProvider;

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
			byte[] content = buildResponseMsg(req);
			FullHttpResponse response = new DefaultFullHttpResponse(
					HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
					Unpooled.wrappedBuffer(content));
			response.headers().set(CONTENT_TYPE, "text/html");
			response.headers().set(CONTENT_LENGTH,
					response.content().readableBytes());
			response.headers().set(CONNECTION, KEEP_ALIVE);
			ctx.write(response);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	private byte[] buildResponseMsg(HttpRequest req) {
		byte[] content = null;
		@SuppressWarnings("deprecation")
		boolean metrics = (req.getUri().indexOf(METRICS) != -1);
		if (RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_SUPPORT && metrics) {
			try {
				content = ModuleMetricsProcessor.getInstance()
						.buildModuleMetrics().getBytes("GBK");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (!RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_SUPPORT
				&& metrics) {
			content = METRICS_ERR_MSG.getBytes();
		} else {
			AbilityDetailProvider provider = new AbilityDetailProvider();
			content = provider.listAbilityDetail(true).toString().getBytes();
		}
		return content;
	}
}
