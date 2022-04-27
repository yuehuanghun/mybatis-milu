package com.yuehuanghun.mybatis.milu.tool.converter.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.yuehuanghun.mybatis.milu.tool.StringUtils;
import com.yuehuanghun.mybatis.milu.tool.converter.Converter;

public class LocalDateTimeConverter implements Converter<LocalDateTime> {
	@Override
	public LocalDateTime convert(Object target) {
		if(CharSequence.class.isInstance(target)) {
			if(StringUtils.isBlank((CharSequence) target)) {
				return null;
			}
			return DateUtil.parseLocalDateTime(target.toString());
		} else if(Number.class.isInstance(target)) {
			return new java.util.Date(((Number)target).longValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		}
		return null;
	}		
}