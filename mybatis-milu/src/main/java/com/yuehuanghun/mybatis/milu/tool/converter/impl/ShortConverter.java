package com.yuehuanghun.mybatis.milu.tool.converter.impl;

import com.yuehuanghun.mybatis.milu.tool.StringUtils;
import com.yuehuanghun.mybatis.milu.tool.converter.Converter;

public class ShortConverter implements Converter<Short>{

	@Override
	public Short convert(Object target) {
		if(target == null) {
			return null;
		}
		if(CharSequence.class.isInstance(target)) {
			if(StringUtils.isBlank((CharSequence) target)) {
				return null;
			}
			return Short.parseShort(target.toString());
		}
		if(Short.class == target.getClass()) {
			return (Short) target;
		}
		if(Number.class.isInstance(target)) {
			return ((Number) target).shortValue();
		}
		return Short.valueOf(target.toString());
	}

}
