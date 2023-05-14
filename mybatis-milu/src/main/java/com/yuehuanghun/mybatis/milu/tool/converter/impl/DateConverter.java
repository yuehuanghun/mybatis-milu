package com.yuehuanghun.mybatis.milu.tool.converter.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import com.yuehuanghun.mybatis.milu.exception.OrmRuntimeException;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;
import com.yuehuanghun.mybatis.milu.tool.converter.Converter;

public class DateConverter implements Converter<Date> {
	@Override
	public Date convert(Object target) {
		if(target == null) {
			return null;
		}
		if(Date.class.isInstance(target)) {
			return (Date) target;
		}
		
		if(CharSequence.class.isInstance(target)) {
			CharSequence cs = (CharSequence) target;
			if(StringUtils.isBlank(cs)) {
				return null;
			}
			if(cs.length() == 13 && StringUtils.isInteger(cs)) {
				return new Date(Long.valueOf(cs.toString()));
			}
			return DateUtil.parseDate(target.toString());
		}
		
		if(Number.class.isInstance(target)) {
			return new Date(((Number)target).longValue());
		}
		
		if(LocalDateTime.class.isInstance(target)) {
			return Date.from(((LocalDateTime)target).atZone(ZoneOffset.systemDefault()).toInstant());
		}
		
		if(LocalDate.class.isInstance(target)) {
			return Date.from(((LocalDate)target).atStartOfDay(ZoneOffset.systemDefault()).toInstant());
		}
		
		throw new OrmRuntimeException(String.format("未支持从%s类型转为%s类型", target.getClass().getName(), Date.class.getName()));
	}		
}
