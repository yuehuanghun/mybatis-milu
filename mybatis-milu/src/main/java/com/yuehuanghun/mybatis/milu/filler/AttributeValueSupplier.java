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
package com.yuehuanghun.mybatis.milu.filler;

/**
 * 属性值提供值
 * @author yuehuanghun
 *
 * @param <T> 返回值类
 */
public interface AttributeValueSupplier<T> {

	/**
	 * 当插入数据时取值
	 * @param clazz 属性类型
	 * @return 值
	 */
	T getValueOnInsert(Class<?> clazz);
	
	/**
	 * 当更新数据时取值
	 * @param clazz 属性类型
	 * @return 值
	 */
	T getValueOnUpdate(Class<?> clazz);
	
	/**
	 * 是否支持此类型
	 * @param clazz 属性类型
	 * @return true/false
	 */
	boolean support(Class<?> clazz);
}
