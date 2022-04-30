package com.yuehuanghun.mybatis.milu.tool.converter.impl;

import java.time.LocalDate;
import java.time.ZoneId;

import com.yuehuanghun.mybatis.milu.exception.OrmRuntimeException;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;
import com.yuehuanghun.mybatis.milu.tool.converter.Converter;

public class LocalDateConverter implements Converter<LocalDate> {
	@Override
	public LocalDate convert(Object target) {
		if(target == null) {
			return null;
		}
		if(LocalDate.class.isInstance(target)) {
			return (LocalDate) target;
		}
		if(CharSequence.class.isInstance(target)) {
			if(StringUtils.isBlank((CharSequence) target)) {
				return null;
			}
			return DateUtil.parseLocalDate(target.toString());
		}
        if(Number.class.isInstance(target)) {
			return new java.util.Date(((Number)target).longValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		}
        throw new OrmRuntimeException(String.format("未支持从%s类型转为%s类型", target.getClass().getName(), LocalDate.class.getName()));
	}		
}