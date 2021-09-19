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
package com.yuehuanghun.mybatis.milu.tool.converter;

import com.yuehuanghun.mybatis.milu.metamodel.KeyType;

/**
 * ExampleQuery中动态参数值类型转换器
 * @see com.yuehuanghun.mybatis.milu.tool.converter.DefaultExampleQueryConverter
 * @see com.yuehuanghun.mybatis.milu.tool.converter.ExampleQueryConverter.NullConverter
 * @author yuehuanghun
 *
 */
public interface ExampleQueryConverter {
	/**
	 * 无转换，原值是什么就返回什么
	 * @author yuehuanghun
	 *
	 */
	public static final class NullConverter implements ExampleQueryConverter {
		@Override
		public Object convert(Object target, Class<?> attrJavaType, String keyName, KeyType keyType) {
			return target;
		}
	}
	
	/**
	 * 自动，即使用默认的转换类<br>
	 * 本来仅作标识使用
	 * @author yuehuanghun
	 *
	 */
	public static final class AutoConverter implements ExampleQueryConverter {
		@Override
		public Object convert(Object target, Class<?> attrJavaType, String keyName, KeyType keyType) {
			throw new RuntimeException("不可用");
		}
		
	}

	Object convert(Object target, Class<?> attrJavaType, String keyName, KeyType keyType);
}
