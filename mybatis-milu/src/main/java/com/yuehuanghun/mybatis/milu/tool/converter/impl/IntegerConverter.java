package com.yuehuanghun.mybatis.milu.tool.converter.impl;

import com.yuehuanghun.mybatis.milu.tool.StringUtils;
import com.yuehuanghun.mybatis.milu.tool.converter.Converter;

public class IntegerConverter implements Converter<Integer>{

	@Override
	public Integer convert(Object target) {
		if(target == null) {
			return null;
		}
		if(CharSequence.class.isInstance(target)) {
			if(StringUtils.isBlank((CharSequence) target)) {
				return null;
			}
			return Integer.parseInt(target.toString());
		}
		if(Integer.class == target.getClass()) {
			return (Integer) target;
		}
		if(Number.class.isInstance(target)) {
			return ((Number) target).intValue();
		}
		return Integer.valueOf(target.toString());
	}

}
