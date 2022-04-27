package com.yuehuanghun.mybatis.milu.tool.converter.impl;

import com.yuehuanghun.mybatis.milu.tool.StringUtils;
import com.yuehuanghun.mybatis.milu.tool.converter.Converter;

public class LongConverter implements Converter<Long>{

	@Override
	public Long convert(Object target) {
		if(target == null) {
			return null;
		}
		if(CharSequence.class.isInstance(target)) {
			if(StringUtils.isBlank((CharSequence) target)) {
				return null;
			}
			return Long.parseLong(target.toString());
		}
		if(Long.class == target.getClass()) {
			return (Long) target;
		}
		if(Number.class.isInstance(target)) {
			return ((Number) target).longValue();
		}
		return Long.valueOf(target.toString());
	}

}
