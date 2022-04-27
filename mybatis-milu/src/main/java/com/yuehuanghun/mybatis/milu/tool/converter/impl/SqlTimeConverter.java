package com.yuehuanghun.mybatis.milu.tool.converter.impl;

import com.yuehuanghun.mybatis.milu.tool.StringUtils;
import com.yuehuanghun.mybatis.milu.tool.converter.Converter;

public class SqlTimeConverter implements Converter<java.sql.Time> {
		@Override
		public java.sql.Time convert(Object target) {
			if(CharSequence.class.isInstance(target)) {
				if(StringUtils.isBlank((CharSequence) target)) {
					return null;
				}
				return DateUtil.parseSqlTime(target.toString());
			} else if(Number.class.isInstance(target)) {
				return new java.sql.Time(((Number)target).longValue());
			}
			return null;
		}		
	}