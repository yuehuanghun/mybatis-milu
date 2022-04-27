package com.yuehuanghun.mybatis.milu.tool.converter.impl;

import com.yuehuanghun.mybatis.milu.tool.StringUtils;
import com.yuehuanghun.mybatis.milu.tool.converter.Converter;

public class FloatConverter implements Converter<Float>{

	@Override
	public Float convert(Object target) {
		if(target == null) {
			return null;
		}
		if(CharSequence.class.isInstance(target)) {
			if(StringUtils.isBlank((CharSequence) target)) {
				return null;
			}
			return Float.parseFloat(target.toString());
		}
		if(Float.class == target.getClass()) {
			return (Float) target;
		}
		if(Number.class.isInstance(target)) {
			return ((Number) target).floatValue();
		}
		return Float.valueOf(target.toString());
	}

}
