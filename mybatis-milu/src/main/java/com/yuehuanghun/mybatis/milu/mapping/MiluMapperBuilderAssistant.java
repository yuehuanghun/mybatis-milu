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

package com.yuehuanghun.mybatis.milu.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.builder.IncompleteElementException;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMap;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;

import com.yuehuanghun.mybatis.milu.tool.StringUtils;

/**
 * 为了动态替换ResultMap中的type，方便在criteria查询中，指定绑定的返回类<br>
 * ResultMap不开放构造函数，无法通过继续进行对其方法包装以动态更改返回值。
 * @see com.yuehuanghun.mybatis.milu.mapping.DynamicResultMapList
 * @author yuehuanghun
 *
 */
public class MiluMapperBuilderAssistant extends MapperBuilderAssistant {
	private final String resource;
	private Cache currentCache;
	private boolean unresolvedCacheRef;

	public MiluMapperBuilderAssistant(Configuration configuration, String resource) {
		super(configuration, resource);
		this.resource = resource;
	}

	public MappedStatement addMappedStatement(String id, SqlSource sqlSource, StatementType statementType,
			SqlCommandType sqlCommandType, Integer fetchSize, Integer timeout, String parameterMap,
			Class<?> parameterType, String resultMap, Class<?> resultType, ResultSetType resultSetType,
			boolean flushCache, boolean useCache, boolean resultOrdered, KeyGenerator keyGenerator, String keyProperty,
			String keyColumn, String databaseId, LanguageDriver lang, String resultSets) {
		return addMappedStatement(id, sqlSource, statementType, sqlCommandType, fetchSize, timeout, parameterMap, parameterType, resultMap, resultType, resultSetType, flushCache, useCache, resultOrdered, keyGenerator, keyProperty, keyColumn, databaseId, lang, resultSets, false);
	}
	
	public MappedStatement addMappedStatement(String id, SqlSource sqlSource, StatementType statementType,
			SqlCommandType sqlCommandType, Integer fetchSize, Integer timeout, String parameterMap,
			Class<?> parameterType, String resultMap, Class<?> resultType, ResultSetType resultSetType,
			boolean flushCache, boolean useCache, boolean resultOrdered, KeyGenerator keyGenerator, String keyProperty,
			String keyColumn, String databaseId, LanguageDriver lang, String resultSets, boolean dynamicResultType) {

		if (unresolvedCacheRef) {
			throw new IncompleteElementException("Cache-ref not yet resolved");
		}

		id = applyCurrentNamespace(id, false);
		boolean isSelect = sqlCommandType == SqlCommandType.SELECT;

		MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, id, sqlSource,
				sqlCommandType).resource(resource).fetchSize(fetchSize).timeout(timeout).statementType(statementType)
						.keyGenerator(keyGenerator).keyProperty(keyProperty).keyColumn(keyColumn).databaseId(databaseId)
						.lang(lang).resultOrdered(resultOrdered).resultSets(resultSets)
						.resultMaps(getStatementResultMaps(resultMap, resultType, id, dynamicResultType)).resultSetType(resultSetType)
						.flushCacheRequired(valueOrDefault(flushCache, !isSelect))
						.useCache(valueOrDefault(useCache, isSelect)).cache(currentCache);

		ParameterMap statementParameterMap = getStatementParameterMap(parameterMap, parameterType, id);
		if (statementParameterMap != null) {
			statementBuilder.parameterMap(statementParameterMap);
		}

		MappedStatement statement = statementBuilder.build();
		configuration.addMappedStatement(statement);
		return statement;
	}

	private <T> T valueOrDefault(T value, T defaultValue) {
		return value == null ? defaultValue : value;
	}

	private ParameterMap getStatementParameterMap(String parameterMapName, Class<?> parameterTypeClass,
			String statementId) {
		parameterMapName = applyCurrentNamespace(parameterMapName, true);
		ParameterMap parameterMap = null;
		if (parameterMapName != null) {
			try {
				parameterMap = configuration.getParameterMap(parameterMapName);
			} catch (IllegalArgumentException e) {
				throw new IncompleteElementException("Could not find parameter map " + parameterMapName, e);
			}
		} else if (parameterTypeClass != null) {
			List<ParameterMapping> parameterMappings = new ArrayList<>();
			parameterMap = new ParameterMap.Builder(configuration, statementId + "-Inline", parameterTypeClass,
					parameterMappings).build();
		}
		return parameterMap;
	}

	private List<ResultMap> getStatementResultMaps(String resultMap, Class<?> resultType, String statementId, boolean dynamicResultMap) {
		resultMap = applyCurrentNamespace(resultMap, true);

		List<ResultMap> resultMaps = dynamicResultMap ? new DynamicResultMapList() : new ArrayList<>();
		if (resultMap != null) {
			String[] resultMapNames = resultMap.split(",");
			for (String resultMapName : resultMapNames) {
				try {
					resultMaps.add(configuration.getResultMap(resultMapName.trim()));
				} catch (IllegalArgumentException e) {
					throw new IncompleteElementException(
							"Could not find result map '" + resultMapName + "' referenced from '" + statementId + "'",
							e);
				}
			}
		} else if (resultType != null) {
			ResultMap inlineResultMap = new ResultMap.Builder(configuration, statementId + "-Inline", resultType,
					new ArrayList<>(), null).build();
			resultMaps.add(inlineResultMap);
		}
		return resultMaps;
	}

	public Cache useCacheRef(String namespace) {
		this.unresolvedCacheRef = true;
		Cache cache = super.useCacheRef(namespace);
		this.unresolvedCacheRef = false;
		return cache;
	}
	
	public void buildResultMapping(Class<?> resultType, Class<?> subType, List<ResultMapping> resultMappings) {
		if(subType == null || subType.isPrimitive() || subType == Object.class) {
			return;
		}
		Field[] fields = subType.getDeclaredFields();
		for(Field field : fields) {
			Class<?> fieldType = field.getType();
			if(fieldType.isArray() || Collection.class.isAssignableFrom(fieldType) || Map.class.isAssignableFrom(fieldType)) {
				continue;
			}
			if(Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			if(!hasSetter(subType, field)) {
				continue;
			}
			ResultMapping resultMapping = this.buildResultMapping(resultType, field.getName(), field.getName(), fieldType, null, null, null, null, null, null, null);
			resultMappings.add(resultMapping);
			String snakeName = StringUtils.camel2Underline(field.getName(), true);
			if(!field.getName().equals(snakeName)) {
				resultMapping = this.buildResultMapping(resultType, field.getName(), snakeName, fieldType, null, null, null, null, null, null, null);
				resultMappings.add(resultMapping);
			}
		}
		
		buildResultMapping(resultType, subType.getSuperclass(), resultMappings);
	}
	
	private static boolean hasSetter(Class<?> clazz, Field field) {
		Method[] methods = clazz.getDeclaredMethods();
		String setterName = "set" + StringUtils.capitalize(field.getName());
		for(Method method : methods) {
			if(Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			
			if(method.getName().equals(setterName) && method.getParameterCount() == 1) {
				return true;
			}
		}
		return false;
	}

}
