package com.yuehuanghun.mybatis.milu.tool.converter.impl;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

import com.yuehuanghun.mybatis.milu.exception.OrmRuntimeException;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;
import com.yuehuanghun.mybatis.milu.tool.converter.Converter;

public class LocalTimeConverter implements Converter<LocalTime> {
	@Override
	public LocalTime convert(Object target) {
		if(target == null) {
			return null;
		}
		if(LocalTime.class.isInstance(target)) {
			return (LocalTime) target;
		}
		
		if(CharSequence.class.isInstance(target)) {
			if(StringUtils.isBlank((CharSequence) target)) {
				return null;
			}
			return DateUtil.parseLocalTime(target.toString());
		}
		
		if(Number.class.isInstance(target)) {
			return new java.util.Date(((Number)target).longValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
		}
		
		if(Date.class.isInstance(target)) {
			return ((Date)target).toInstant().atZone(ZoneOffset.systemDefault()).toLocalTime();
		}
		throw new OrmRuntimeException(String.format("未支持从%s类型转为%s类型", target.getClass().getName(), LocalTime.class.getName()));
	}		
}