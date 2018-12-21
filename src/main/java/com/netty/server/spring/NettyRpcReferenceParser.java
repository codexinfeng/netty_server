package com.netty.server.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class NettyRpcReferenceParser implements BeanDefinitionParser {

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String interfaceName = element.getAttribute("interfaceName");
		String ipAddr = element.getAttribute("ipAddr");
		String protocol = element.getAttribute("protocol");
		String id = element.getAttribute("id");
		// 自己定义beanDefinition
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(NettyRpcReference.class);
		beanDefinition.setLazyInit(false);
		beanDefinition.getPropertyValues().addPropertyValue("interfaceName",
				interfaceName);
		beanDefinition.getPropertyValues().addPropertyValue("ipAddr", ipAddr);
		beanDefinition.getPropertyValues().addPropertyValue("protocol",
				protocol);
		parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
		return beanDefinition;
	}
}
