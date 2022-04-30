/*
 * Copyright 2020-2022 the original author or authors.
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
package com.yuehuanghun.mybatis.milu.tool.converter;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import com.yuehuanghun.mybatis.milu.metamodel.KeyType;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

/**
 * 默认的转换器<br>
 * 目前仅会转换以下类型：<br>
 * java.util.Date <br>
 * java.sql.Date <br>
 * java.sql.Time <br>
 * java.time.LocalDate <br>
 * java.time.LocalDateTime <br>
 * java.time.LocalTime <br>
 * 
 * @author yuehuanghun
 *
 */
public class DefaultExampleQueryConverter implements ExampleQueryConverter {
	private static final Log log = LogFactory.getLog(DefaultExampleQueryConverter.class);

	@Override
	public Object convert(Object target, Class<?> attrJavaType, String keyName, KeyType keyType) {
		if(target == null) {
			return null;
		}
		
		if(attrJavaType.isInstance(target)) {
			return target;
		}
		
		Converter<?> converter = ConverterUtils.getConverter(attrJavaType).orElse(null);
		if(converter != null) {
			Object result = converter.convert(target);
			if(result != null) {
				return result;
			} else {
				if(!isEmpty(target)) { //减少警告
					log.warn(String.format("键%s的对象%s无法转为%s类型对象", keyName, target, attrJavaType.getName()));
				}				
			}
		}
		
		return target;
	}
	
	private static boolean isEmpty(Object target) {
		return !StringUtils.notBlank(target);
	}
	
}
