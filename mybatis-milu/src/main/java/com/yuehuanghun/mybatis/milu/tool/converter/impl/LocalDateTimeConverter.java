package com.yuehuanghun.mybatis.milu.tool.converter.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.yuehuanghun.mybatis.milu.exception.OrmRuntimeException;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;
import com.yuehuanghun.mybatis.milu.tool.converter.Converter;

public class LocalDateTimeConverter implements Converter<LocalDateTime> {
	@Override
	public LocalDateTime convert(Object target) {
		if(target == null) {
			return null;
		}
		if(LocalDateTimeConverter.class.isInstance(target)) {
			return (LocalDateTime) target;
		}
		if(CharSequence.class.isInstance(target)) {
			if(StringUtils.isBlank((CharSequence) target)) {
				return null;
			}
			return DateUtil.parseLocalDateTime(target.toString());
		}
		if(Number.class.isInstance(target)) {
			return new java.util.Date(((Number)target).longValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		}
		throw new OrmRuntimeException(String.format("未支持从%s类型转为%s类型", target.getClass().getName(), LocalDateTime.class.getName()));
	}		
}