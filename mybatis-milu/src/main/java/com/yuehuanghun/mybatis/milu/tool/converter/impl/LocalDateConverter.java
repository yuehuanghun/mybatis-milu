package com.yuehuanghun.mybatis.milu.tool.converter.impl;

import java.time.LocalDate;
import java.time.ZoneId;

import com.yuehuanghun.mybatis.milu.tool.StringUtils;
import com.yuehuanghun.mybatis.milu.tool.converter.Converter;

public class LocalDateConverter implements Converter<LocalDate> {
	@Override
	public LocalDate convert(Object target) {
		if(CharSequence.class.isInstance(target)) {
			if(StringUtils.isBlank((CharSequence) target)) {
				return null;
			}
			return DateUtil.parseLocalDate(target.toString());
		} else if(Number.class.isInstance(target)) {
			return new java.util.Date(((Number)target).longValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		}
		return null;
	}		
}