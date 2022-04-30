package com.yuehuanghun.mybatis.milu.tool.converter.impl;

import java.sql.Time;

import com.yuehuanghun.mybatis.milu.exception.OrmRuntimeException;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;
import com.yuehuanghun.mybatis.milu.tool.converter.Converter;

public class SqlTimeConverter implements Converter<Time> {
	@Override
	public Time convert(Object target) {
		if (target == null) {
			return null;
		}
		if (Time.class.isInstance(target)) {
			return (Time) target;
		}

		if (CharSequence.class.isInstance(target)) {
			if (StringUtils.isBlank((CharSequence) target)) {
				return null;
			}
			return DateUtil.parseSqlTime(target.toString());
		}

		if (Number.class.isInstance(target)) {
			return new java.sql.Time(((Number) target).longValue());
		}
		throw new OrmRuntimeException(String.format("未支持从%s类型转为%s类型", target.getClass().getName(), Time.class.getName()));
	}
}