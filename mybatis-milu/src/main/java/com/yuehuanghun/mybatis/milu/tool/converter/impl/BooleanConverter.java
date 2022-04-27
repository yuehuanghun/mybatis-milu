package com.yuehuanghun.mybatis.milu.tool.converter.impl;

import com.yuehuanghun.mybatis.milu.tool.StringUtils;
import com.yuehuanghun.mybatis.milu.tool.converter.Converter;

public class BooleanConverter implements Converter<Boolean>{

	@Override
	public Boolean convert(Object target) {
		if(target == null) {
			return null;
		}
		if(CharSequence.class.isInstance(target)) {
			if(StringUtils.isBlank((CharSequence) target)) {
				return null;
			}
			if(StringUtils.isInteger((CharSequence) target)) {
				return Long.parseLong(target.toString()) != 0;
			}
			return "true".equals(((CharSequence) target).toString());
		}
		if(Boolean.class == target.getClass()) {
			return (Boolean) target;
		}
		if(Number.class.isInstance(target)) {
			return ((Number) target).doubleValue() > 0;
		}
		return true;
	}

}
