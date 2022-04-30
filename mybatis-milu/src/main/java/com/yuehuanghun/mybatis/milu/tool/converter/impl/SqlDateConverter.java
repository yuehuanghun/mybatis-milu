package com.yuehuanghun.mybatis.milu.tool.converter.impl;

import java.sql.Date;

import com.yuehuanghun.mybatis.milu.exception.OrmRuntimeException;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;
import com.yuehuanghun.mybatis.milu.tool.converter.Converter;

public class SqlDateConverter implements Converter<Date> {
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
			return DateUtil.parseSqlDate(target.toString());
		}
        
		if(Number.class.isInstance(target)) {
			return new Date(((Number)target).longValue());
		}
		throw new OrmRuntimeException(String.format("未支持从%s类型转为%s类型", target.getClass().getName(), Date.class.getName()));
	}		
}
