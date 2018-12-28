package com.netty.server.resolver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.nio.channels.ClosedChannelException;
import java.util.concurrent.Callable;

public class ApiEchoResolver implements Callable<Boolean> {

	private static final boolean SSL = System.getProperty("ssl") != null;
	private String host;
	private int port;

	public ApiEchoResolver(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public Boolean call() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		SslContext sslCtx = null;
		//ssl链接是什么
		if (SSL) {
			SelfSignedCertificate ssc = new SelfSignedCertificate();
			sslCtx = SslContextBuilder.forServer(ssc.certificate(),
					ssc.privateKey()).build();
		}
		ServerBootstrap b = new ServerBootstrap();
		b.option(ChannelOption.SO_BACKLOG, 1024)
		.group(bossGroup,workerGroup)
		.channel(NioServerSocketChannel.class)
//		.handler(new LoggingHandler(LogLevel.INFO))
		.childHandler(childHandler)
		ChannelFuture f = b.bind(host,port).sync();
		f.channel().closeFuture().sync();
		
		return null;
	}

}
