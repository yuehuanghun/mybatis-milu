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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;

@SuppressWarnings("rawtypes")
public class SupplierHelper {
	private static final Map<Class<?>, AttributeValueSupplier<?>> classSupplierCache = new HashMap<>();
	private static final Map<Object, AttributeValueSupplier> valueSupplierCache = new HashMap<>();
	private SupplierHelper() {
		
	}
	
	public final static AttributeValueSupplier<?> getSupplier(Class<? extends AttributeValueSupplier<?>> supplierClass){
		return classSupplierCache.computeIfAbsent(supplierClass, (key) -> {
			try {
				return supplierClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new SqlExpressionBuildingException("实例化AttributeValueSupplier失败", e);
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public final static <T> AttributeValueSupplier<T> getSupplier(T value, Function<T, AttributeValueSupplier<T>> supplierLoader){
		return valueSupplierCache.computeIfAbsent(value, (key) -> {
			return supplierLoader.apply(value);
		});
	}
	
	// 目前无合适触发时间，未使用
	public static void release() {
		classSupplierCache.clear();
		valueSupplierCache.clear();
	}
}
