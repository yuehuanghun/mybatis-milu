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
package com.yuehuanghun.mybatis.milu.tool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;

public class InstanceUtils {
	private static Map<Class<?>, Object> instanceCache = new ConcurrentHashMap<>();
	
	@SuppressWarnings("unchecked")
	public static <T> T getSigleton(Class<? extends T> clazz) {
		Object instance = instanceCache.computeIfAbsent(clazz, key -> {
			try {
				return clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new SqlExpressionBuildingException(e);
			}
		});
		
		return (T) instance;
	}
}
