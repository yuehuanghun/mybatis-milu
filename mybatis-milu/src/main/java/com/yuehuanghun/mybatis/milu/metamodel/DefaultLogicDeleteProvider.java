/*
 * Copyright 2020-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yuehuanghun.mybatis.milu.metamodel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.yuehuanghun.mybatis.milu.exception.OrmBuildingException;
import com.yuehuanghun.mybatis.milu.exception.OrmRuntimeException;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;
import com.yuehuanghun.mybatis.milu.tool.converter.Converter;
import com.yuehuanghun.mybatis.milu.tool.converter.ConverterUtils;
import com.yuehuanghun.mybatis.milu.tool.logicdel.LogicDeleteProvider;

//计划：#{express}为特定类型值，${el}为EL表达式
class DefaultLogicDeleteProvider implements LogicDeleteProvider {
	private Object deleteValue;
	
	private Object resumeValue;
	//表达式优先
	private ExpressValueSupplier deleteValueSupplier;
	
	private ExpressValueSupplier resumeValueSupplier;
	
	private static final Pattern EXPRESS_PT = Pattern.compile("^#\\{.+\\}$");
	private static final Map<String, ExpressValueSupplier> EXPRESS_VALUE_PROVIDER_MAP = new HashMap<>();
	
	public DefaultLogicDeleteProvider(String deleteValue, String resumeValue, Class<?> clazz) {
		if(isExpress(deleteValue)) {
			String express = getExpress(deleteValue);
			this.deleteValueSupplier = EXPRESS_VALUE_PROVIDER_MAP.computeIfAbsent(express, (key) -> {
				throw new OrmBuildingException(String.format("没有表达式%s的Supplier", key));
			});
		} else {
			Converter<?> converter = ConverterUtils.getConverter(clazz).orElseThrow(() -> new OrmBuildingException(String.format("找不到类%s的值转换器", clazz.getName())));
			this.deleteValue = converter.convert(deleteValue);
		}
		
		if(isExpress(resumeValue)) {
			String express = getExpress(resumeValue);
			this.deleteValueSupplier = EXPRESS_VALUE_PROVIDER_MAP.computeIfAbsent(express, (key) -> {
				throw new OrmBuildingException(String.format("没有表达式%s的Supplier", key));
			});
		} else {
			Converter<?> converter = ConverterUtils.getConverter(clazz).orElseThrow(() -> new OrmBuildingException(String.format("找不到类%s的值转换器", clazz.getName())));
			this.resumeValue = converter.convert(resumeValue);
		}
	}
	
	private boolean isExpress(String value) {
		if(StringUtils.isBlank(value)) {
			return false;
		}
		
		return EXPRESS_PT.matcher(value).find();
	}
	
	private String getExpress(String value) {
		return value.substring(2, value.length() - 1);
	}

	@Override
	public Object value(Context context) {
		if(deleteValueSupplier != null) {
			return deleteValueSupplier.getValue(context.getAttrType());
		}
		return deleteValue;
	}

	@Override
	public Object resumeValue(Context context) {
		if(resumeValueSupplier != null) {
			return resumeValueSupplier.getValue(context.getAttrType());
		}
		return resumeValue;
	}
	
	static {
		EXPRESS_VALUE_PROVIDER_MAP.put("now", new NowProvider());
	}

	//没有太多想法，先这样玩玩
	static interface ExpressValueSupplier {
		
		Object getValue(Class<?> clazz);
	}
	
	static class NowProvider implements ExpressValueSupplier {

		@Override
		public Object getValue(Class<?> clazz) {
			if(java.util.Date.class == clazz) {
				return new java.util.Date();
			}
			if(LocalDateTime.class == clazz) {
				return LocalDateTime.now();
			}
			if(LocalDate.class == clazz) {
				return LocalDate.now();
			}
			if(java.sql.Date.class == clazz) {
				return new java.sql.Date(System.currentTimeMillis());
			}
			if(Long.class == clazz) {
				return System.currentTimeMillis();
			}
			if(String.class == clazz) {
				return String.valueOf(System.currentTimeMillis());
			}
			return new OrmRuntimeException("无法取now表达式的值，值类型：" + clazz.getName());
		}
		
	}
}
