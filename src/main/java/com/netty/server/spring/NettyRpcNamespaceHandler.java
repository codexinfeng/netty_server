package com.netty.server.spring;

import java.io.InputStreamReader;
import java.io.Reader;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.google.common.io.CharStreams;

public class NettyRpcNamespaceHandler extends NamespaceHandlerSupport {

	static {
		try {
			Resource resource = new ClassPathResource("NettyRpcLogo.txt");
			if (resource.exists()) {
				Reader reader = new InputStreamReader(
						resource.getInputStream(), "UTF-8");
				String text = CharStreams.toString(reader);
				System.out.print(text);
				reader.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("");
			System.out
					.println(" _      _____ _____ _____ ___  _ ____  ____  ____ ");
			System.out
					.println("/ \\  /|/  __//__ __Y__ __\\\\  \\///  __\\/  __\\/   _\\");
			System.out
					.println("| |\\ |||  \\    / \\   / \\   \\  / |  \\/||  \\/||  /  ");
			System.out
					.println("| | \\|||  /_   | |   | |   / /  |    /|  __/|  \\_ ");
			System.out
					.println("\\_/  \\|\\____\\  \\_/   \\_/  /_/   \\_/\\_\\\\_/   \\____/");
			System.out.println("[NettyRPC 2.0,Build 2016/10/7");
			System.out.println("");
		}

	}

	@Override
	public void init() {
		registerBeanDefinitionParser("reference", new NettyRpcReferenceParser());
		registerBeanDefinitionParser("registry", new NettyRpcRegisteryParser());
		registerBeanDefinitionParser("service", new NettyRpcServiceParser());

	}

}
