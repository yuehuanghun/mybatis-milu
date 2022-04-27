package com.yuehuanghun.mybatis.milu.tool.converter.impl;

import com.yuehuanghun.mybatis.milu.tool.StringUtils;
import com.yuehuanghun.mybatis.milu.tool.converter.Converter;

public class DoubleConverter implements Converter<Double>{

	@Override
	public Double convert(Object target) {
		if(target == null) {
			return null;
		}
		if(CharSequence.class.isInstance(target)) {
			if(StringUtils.isBlank((CharSequence) target)) {
				return null;
			}
			return Double.parseDouble(target.toString());
		}
		if(Double.class == target.getClass()) {
			return (Double) target;
		}
		if(Number.class.isInstance(target)) {
			return ((Number) target).doubleValue();
		}
		return Double.valueOf(target.toString());
	}

}
