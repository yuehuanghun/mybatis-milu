package com.yuehuanghun.mybatis.milu.tool.converter;

public interface Converter<T> {
	T convert(Object target);
}
