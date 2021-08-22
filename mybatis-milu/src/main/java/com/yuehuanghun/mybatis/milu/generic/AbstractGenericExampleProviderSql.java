/*
 * Copyright 2020-2021 the original author or authors.
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
package com.yuehuanghun.mybatis.milu.generic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.scripting.xmltags.OgnlCache;

import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.RangeCondition;
import com.yuehuanghun.mybatis.milu.tool.Constants;
import com.yuehuanghun.mybatis.milu.tool.converter.ExampleQueryConverter;

public abstract class AbstractGenericExampleProviderSql extends GenericCachingProviderSql {
	private static Map<Class<? extends ExampleQueryConverter>, ExampleQueryConverter> converterCache = new ConcurrentHashMap<>();

	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		String sql =  super.provideSql(context, params);
		convertRangQueryValue(context, params);
		return sql;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void convertRangQueryValue(GenericProviderContext context, Object params) {
		Object example = ((Map)params).get(Constants.EXAMPLE);
		if(example == null) {
			return;
		}
		for(Attribute attr : context.getEntity().getAttributes()) {
			for(RangeCondition condition : attr.getRangeList()) {
				if(!condition.getKeyName().contains(".")) { //实体类直接属性不处理
					continue;
				}
				
				int index = condition.getKeyName().lastIndexOf(".");
				String preKey = condition.getKeyName().substring(0, index);
				Object containerObj = OgnlCache.getValue(preKey, example);
				if(!(containerObj instanceof Map)) { //非Map不处理，只有Map的元素可以转换后覆盖
					continue;
				}
				
				Map container = (Map)containerObj;
				String lastKey = condition.getKeyName().substring(index + 1);
				Object value = container.get(lastKey);
				if(value == null) {
					continue;
				}
				
				Class<? extends ExampleQueryConverter> valueConverterClass = condition.getValueConverter();
				ExampleQueryConverter converter = converterCache.computeIfAbsent(valueConverterClass, clazz -> {
					try {
						return clazz.newInstance();
					} catch (InstantiationException | IllegalAccessException e) {
						throw new SqlExpressionBuildingException(e);
					}
				});
				Object convertedValue = converter.convert(value, condition.getAttrJavaType(), condition.getKeyName(), condition.getKeyType());
				container.put(lastKey, convertedValue);
			}
		}
	}
	
	
}
