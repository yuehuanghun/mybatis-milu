package com.yuehuanghun.mybatis.milu.tool.converter.impl;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.yuehuanghun.mybatis.milu.tool.converter.Converter;

public class StringConverter implements Converter<String> {

	@Override
	public String convert(Object target) {
		if(target == null) {
			return null;
		}
		if(target instanceof Collection) {
			Collection<?> c = (Collection<?>) target;
			return c.stream().map(String::valueOf).collect(Collectors.joining(","));
			
		} else if(target.getClass().isArray()){
			int length = Array.getLength(target);
			StringJoiner joiner = new StringJoiner(",");
			IntStream.range(0, length).forEach(i -> {
				joiner.add(String.valueOf(Array.get(target, i)));
			});
		}
		return target.toString();
	}

}
