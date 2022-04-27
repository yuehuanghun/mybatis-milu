package com.yuehuanghun.mybatis.milu.tool.converter.impl;

import java.math.BigDecimal;

import com.yuehuanghun.mybatis.milu.tool.StringUtils;
import com.yuehuanghun.mybatis.milu.tool.converter.Converter;

public class BigDecimalConverter implements Converter<BigDecimal>{

	@Override
	public BigDecimal convert(Object target) {
		if(target == null) {
			return null;
		}
		if(CharSequence.class.isInstance(target)) {
			if(StringUtils.isBlank((CharSequence) target)) {
				return null;
			}
			return new BigDecimal(target.toString());
		}
		if(BigDecimal.class == target.getClass()) {
			return (BigDecimal) target;
		}
		return new BigDecimal(target.toString());
	}

}
