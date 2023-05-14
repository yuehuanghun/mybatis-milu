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
package com.yuehuanghun.mybatis.milu.generic.impl;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.apache.ibatis.javassist.scopedpool.SoftValueHashMap;

import com.yuehuanghun.mybatis.milu.criteria.LambdaPredicate;
import com.yuehuanghun.mybatis.milu.criteria.LambdaPredicateImpl;
import com.yuehuanghun.mybatis.milu.criteria.builder.SingleStatisticSqlTemplateBuilder;
import com.yuehuanghun.mybatis.milu.criteria.lambda.LambdaReflections;
import com.yuehuanghun.mybatis.milu.criteria.lambda.SerializableFunction;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderSql;
import com.yuehuanghun.mybatis.milu.mapping.ResultMapHelper;
import com.yuehuanghun.mybatis.milu.tool.Assert;
import com.yuehuanghun.mybatis.milu.tool.Constants;

public abstract class GenericSingleStatisticByLambdaCriteriaProviderSql implements GenericProviderSql {
	private final Map<Class<?>, Map<Object, String>> cache = new ConcurrentHashMap<>();
	
	private final String functionName;
	
	public GenericSingleStatisticByLambdaCriteriaProviderSql(String functionName) {
		Assert.notBlank(functionName, "统计函数不能为空");
		this.functionName = functionName;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		Object criteria = ((Map)params).get(Constants.CRITERIA);
		
		LambdaPredicateImpl<?> predicate = new LambdaPredicateImpl<>();
		((Consumer<LambdaPredicate>)criteria).accept(predicate);
		
		Map<String, Object> queryParams = new HashMap<>();
		predicate.renderParams(context, queryParams, 0);
		
		((Map)params).putAll(queryParams);
		
		SerializableFunction function = (SerializableFunction) ((Map)params).get(Constants.ATTR_NAME);
		String attrName = LambdaReflections.fnToFieldName(function);
		Type returnType = LambdaReflections.getReturnType(function);
		
		ResultMapHelper.setResultType((Class<?>) returnType);
		
		Map<String, Object> valueKey = new HashMap<>();
		valueKey.put(Constants.CRITERIA, predicate);
		valueKey.put(Constants.ATTR_NAME, attrName);
		
		return cache.computeIfAbsent(context.getMapperType(), (clazz) -> {
			return new SoftValueHashMap<>();
		}).computeIfAbsent(valueKey, (key) -> {
			return new SingleStatisticSqlTemplateBuilder(context, functionName, attrName, predicate.getDelegate()).build();
		});
	}

}
