package com.netty.server.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class NettyServerMain {

	public static void main(String[] args) {
		ApplicationContext ac = new ClassPathXmlApplicationContext(
				"classpath:rpc-invoke-config-kryo.xml");
		System.out.println("rpc server");
	}
}
