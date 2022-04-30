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

package com.yuehuanghun.mybatis.milu.mapping;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;

/**
 * 目前针对通用的统计mapper方法中动态返回类型的处理<br>
 * @see com.yuehuanghun.mybatis.milu.mapping.DynamicResultMapList
 * @author yuehuanghun
 *
 */
public class ResultMapHelper {

	private final static Map<String, Map<Class<?>, ResultMap>> DYNAMIC_RESULT_MAP = new ConcurrentHashMap<>();
	
	private final static ThreadLocal<Class<?>> DYNAMIC_RETURN_TYPE = new ThreadLocal<>(); 
	
	private final static ThreadLocal<List<ResultMapping>> DYNAMIC_RESULT_MAPPINGS = new ThreadLocal<>(); 
	
	public static ResultMap replaceResultMap(ResultMap resultMap) {
		if(DYNAMIC_RESULT_MAPPINGS.get() != null) { //主表与关系表同时查询时的处理
			Configuration configuration = getConfiguration(resultMap);
			return new ResultMap.Builder(configuration, resultMap.getId(), resultMap.getType(), DYNAMIC_RESULT_MAPPINGS.get(), true).build();
		}
		
		Class<?> returnType = DYNAMIC_RETURN_TYPE.get();
		if(returnType == null || resultMap.getType().isAssignableFrom(returnType)) {
			return resultMap;
		}
		
		String resultMapId = resultMap.getId();
		
		Map<Class<?>, ResultMap> map = DYNAMIC_RESULT_MAP.get(resultMapId);
		if(map == null) {
			synchronized (resultMapId) {
				map = DYNAMIC_RESULT_MAP.get(resultMapId);
				if(map == null) {
					map = new ConcurrentHashMap<>();
					DYNAMIC_RESULT_MAP.put(resultMapId, map);
				}
			}
		}

		return map.computeIfAbsent(returnType, (key) -> {
			Configuration configuration = getConfiguration(resultMap);
			MiluMapperBuilderAssistant assistant = new MiluMapperBuilderAssistant(configuration, null); //resource ignore
			List<ResultMapping> mappingList = new ArrayList<>();
			assistant.buildResultMapping(returnType, returnType, mappingList);
			return new ResultMap.Builder(configuration, resultMapId, returnType, mappingList, true).build();
		});
	}
	
	//清除动态的resultType
	public static void clear() {
		DYNAMIC_RETURN_TYPE.remove();
		DYNAMIC_RESULT_MAPPINGS.remove();
	}
	
	//设置动态的resultType
	public static void setResultType(Class<?> resultType) {
		DYNAMIC_RETURN_TYPE.set(resultType);
	}
	
	//动态ResultMapping
	public static void setResultMappingList(List<ResultMapping> resultMappings) {
		DYNAMIC_RESULT_MAPPINGS.set(resultMappings);
	}
	
	private static Configuration getConfiguration(ResultMap resultMap) {
		try {
			Field field = resultMap.getClass().getDeclaredField("configuration");
			field.setAccessible(true);
			return (Configuration)field.get(resultMap);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
