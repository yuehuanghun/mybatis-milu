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
package com.yuehuanghun.mybatis.milu.generic.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.ibatis.javassist.scopedpool.SoftValueHashMap;

import com.github.pagehelper.PageHelper;
import com.yuehuanghun.mybatis.milu.criteria.CriteriaSqlBuilder;
import com.yuehuanghun.mybatis.milu.criteria.Expression;
import com.yuehuanghun.mybatis.milu.criteria.Predicate;
import com.yuehuanghun.mybatis.milu.criteria.QueryPredicateImpl;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderSql;
import com.yuehuanghun.mybatis.milu.mapping.ResultMapHelper;
import com.yuehuanghun.mybatis.milu.pagehelper.Pageable;
import com.yuehuanghun.mybatis.milu.tool.Constants;

public class GenericFindByCriteriaProviderSql implements GenericProviderSql {

	private final Map<Expression, String> cache = new SoftValueHashMap<>();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		Map paramMap = ((Map)params);
		Object criteria = paramMap.get(Constants.CRITERIA);
		Expression expression;
		
		if(Consumer.class.isInstance(criteria)) {
			Predicate predicate = new QueryPredicateImpl();			
			((Consumer<Predicate>)criteria).accept(predicate);
			expression = predicate;
		} else {
			expression = (Expression)criteria;
		}

		if(paramMap.containsKey(Constants.RESULT_TYPE)) { //动态resultType
			ResultMapHelper.setResultType((Class<?>) paramMap.remove(Constants.RESULT_TYPE));
		}
		
		Map<String, Object> queryParams = new HashMap<>();
		expression.renderParams(queryParams, 0);		
		paramMap.putAll(queryParams);
		
		if(paramMap.containsKey(Constants.PAGE_KEY)) {
			Pageable page = (Pageable)paramMap.get(Constants.PAGE_KEY);
			PageHelper.startPage(page.getPageNum(), page.getPageSize(), page.isCount());
		}

		String sqlExpression = cache.computeIfAbsent(expression, (key) -> {
			StringBuilder expressionBuilder = new StringBuilder(256);
			Set<String> columns = new HashSet<>();
			expression.renderSqlTemplate(context.getConfiguration(), expressionBuilder, columns, 0);
			CriteriaSqlBuilder builder = CriteriaSqlBuilder.instance(context.getEntity().getJavaType(), expressionBuilder.toString(), columns, context.getConfiguration());
			if(QueryPredicateImpl.class.isInstance(expression)) {
				builder.setSelectAttrs(((QueryPredicateImpl)expression).getSelectAttrs()).setExselectAttrs(((QueryPredicateImpl)expression).getExselectAttrs());
			}
			return builder.build();
		});
		
		return sqlExpression;
	}

	@Override
	public String getMethodName() {
		return "findByCriteria";
	}

}
