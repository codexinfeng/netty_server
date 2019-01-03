package com.netty.server.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class NettyRpcRegisteryParser implements BeanDefinitionParser {

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String id = element.getAttribute("id");
		String ipAddr = element.getAttribute("ipAddr");
		String echoApiPort = element.getAttribute("echoApiPort");
		String protocolType = element.getAttribute("protocol");
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(NettyRpcRegistery.class);
		beanDefinition.getPropertyValues().addPropertyValue("idAddr", ipAddr);
		beanDefinition.getPropertyValues().addPropertyValue("echoApiPort",
				echoApiPort);
		beanDefinition.getPropertyValues().addPropertyValue("protocol",
				protocolType);
		// 将beanDefinition注册到BeanDefinitionMap
		parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
		return beanDefinition;
	}

}
