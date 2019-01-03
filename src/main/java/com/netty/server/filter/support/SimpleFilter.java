package com.netty.server.filter.support;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;

import com.netty.server.filter.Filter;

public class SimpleFilter implements Filter {

	@Override
	public boolean before(Method method, Object processor,
			Object[] requestObjects) {
		System.out.println(StringUtils
				.center("[SimpleFilter##before]", 48, "*"));
		return true;
	}

	@Override
	public void after(Method method, Object processor, Object[] requestObjects) {
		System.out.println(StringUtils.center("[SimpleFilte##after]", 48, "*"));
	}

}
