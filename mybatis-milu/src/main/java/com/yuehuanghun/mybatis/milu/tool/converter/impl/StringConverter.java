package com.yuehuanghun.mybatis.milu.tool.converter.impl;

import com.yuehuanghun.mybatis.milu.tool.converter.Converter;

public class StringConverter implements Converter<String> {

	@Override
	public String convert(Object target) {
		return target == null ? null : target.toString();
	}

}
