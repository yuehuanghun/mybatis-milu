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
package com.yuehuanghun.mybatis.milu.generic.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.apache.ibatis.javassist.scopedpool.SoftValueHashMap;

import com.yuehuanghun.mybatis.milu.criteria.Expression;
import com.yuehuanghun.mybatis.milu.criteria.UpdatePredicate;
import com.yuehuanghun.mybatis.milu.criteria.UpdatePredicateImpl;
import com.yuehuanghun.mybatis.milu.criteria.builder.UpdateSqlTemplateBuilder;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderSql;
import com.yuehuanghun.mybatis.milu.tool.Constants;

public class GenericUpdateByCriteriaProviderSql implements GenericProviderSql {

	private final Map<Class<?>, Map<Expression, String>> cache = new ConcurrentHashMap<>();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		SqlBuildingHelper.fill(params, false, context.getConfiguration());
		Object criteria = ((Map)params).get(Constants.CRITERIA);
		UpdatePredicate predicate;
		
		if(Consumer.class.isInstance(criteria)) {
			predicate = new UpdatePredicateImpl();			
			((Consumer<UpdatePredicate>)criteria).accept(predicate);
		} else {
			predicate = (UpdatePredicate)criteria;
		}
		
		Map<String, Object> queryParams = new HashMap<>();
		predicate.renderParams(context, queryParams, 0);
		((Map)params).putAll(queryParams);

		String sqlExpression = cache.computeIfAbsent(context.getMapperType(), (clazz) -> {
			return new SoftValueHashMap<>();
		}).computeIfAbsent(predicate, (key) -> {
			return new UpdateSqlTemplateBuilder(context, predicate).build();
		});
		
		return sqlExpression;
	}

	@Override
	public String getMethodName() {
		return "updateByCriteria";
	}

}
