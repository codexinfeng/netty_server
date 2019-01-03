package com.netty.server.spring;

import static org.springframework.beans.factory.BeanFactoryUtils.beanNamesForTypeIncludingAncestors;
import static org.springframework.beans.factory.BeanFactoryUtils.beansOfTypeIncludingAncestors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

public class BeanFactoryUtils implements BeanFactoryAware {

	private static BeanFactory beanFactory;

	@SuppressWarnings("static-access")
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	private static boolean isContains(String[] values, String value) {
		if (value != null && value.length() > 0 && values != null
				&& values.length > 0) {
			for (String v : values) {
				if (value.equals(v)) {
					return true;
				}
			}

		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		if (beanFactory == null) {
			return null;
		}
		try {
			return (T) beanFactory.getBean(name);
		} catch (NoSuchBeanDefinitionException e) {
			return null;
		}
	}

	public static <T> T getOptionalBean(ListableBeanFactory beanFactory,
			String beanName, Class<T> beanType) {
		String[] allBeanNames = beanNamesForTypeIncludingAncestors(beanFactory,
				beanType);
		if (!isContains(allBeanNames, beanName)) {
			return null;
		}
		Map<String, T> beansOfType = beansOfTypeIncludingAncestors(beanFactory,
				beanType);
		return beansOfType.get(beanName);
	}

	public static <T> List<T> getBean(ListableBeanFactory beanFactory,
			String[] beanNames, Class<T> beanType) {
		String[] allBeanNames = beanNamesForTypeIncludingAncestors(beanFactory,
				beanType);
		List<T> beans = new ArrayList<>(beanNames.length);
		for (String beanName : beanNames) {
			if (isContains(allBeanNames, beanName)) {
				beans.add(beanFactory.getBean(beanName, beanType));
			}
		}
		return Collections.unmodifiableList(beans);
	}
}
