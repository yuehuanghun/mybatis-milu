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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.apache.ibatis.javassist.scopedpool.SoftValueHashMap;

import com.github.pagehelper.PageHelper;
import com.yuehuanghun.mybatis.milu.criteria.Expression;
import com.yuehuanghun.mybatis.milu.criteria.Limit;
import com.yuehuanghun.mybatis.milu.criteria.LimitOffset;
import com.yuehuanghun.mybatis.milu.criteria.Predicate;
import com.yuehuanghun.mybatis.milu.criteria.StatisticPredicate;
import com.yuehuanghun.mybatis.milu.criteria.StatisticPredicateImpl;
import com.yuehuanghun.mybatis.milu.criteria.builder.StatisticSqlTemplateBuilder;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderSql;
import com.yuehuanghun.mybatis.milu.mapping.ResultMapHelper;
import com.yuehuanghun.mybatis.milu.pagehelper.Pageable;
import com.yuehuanghun.mybatis.milu.tool.Constants;

public class GenericStatisticByCriteriaProviderSql implements GenericProviderSql {

	private final Map<Class<?>, Map<Expression, String>> cache = new ConcurrentHashMap<>();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		Map paramMap = ((Map)params);
		Object criteria = paramMap.get(Constants.CRITERIA);		
		
		if(paramMap.containsKey(Constants.RESULT_TYPE)) { //动态resultType
			ResultMapHelper.setResultType((Class<?>) paramMap.remove(Constants.RESULT_TYPE));
		}
		
		StatisticPredicate predicate;
		
		if(Consumer.class.isInstance(criteria)) {
			predicate = new StatisticPredicateImpl();			
			((Consumer<Predicate>)criteria).accept(predicate);
		} else {
			predicate = (StatisticPredicate)criteria;
		}
		predicate.end();
		
		Map<String, Object> queryParams = new HashMap<>();
		predicate.renderParams(context, queryParams, 0);		
		((Map)params).putAll(queryParams);
		
		if(queryParams.containsKey(Constants.PAGE_KEY)) {
			Limit limit = (Limit)queryParams.remove(Constants.PAGE_KEY);
			if(limit instanceof Pageable) {
				Pageable page = (Pageable)limit;
				PageHelper.startPage(page.getPageNum(), page.getPageSize(), page.isCount());
			} else if(limit instanceof LimitOffset) {
				LimitOffset limitOffset = (LimitOffset)limit;
				PageHelper.offsetPage(limitOffset.getOffset(), limitOffset.getSize(), limitOffset.isCount());
			}
		}

		String sqlExpression = cache.computeIfAbsent(context.getMapperType(), (clazz) -> {
			return new SoftValueHashMap<>();
		}).computeIfAbsent(predicate, (key) -> {
			return new StatisticSqlTemplateBuilder(context,  predicate).build();
		});
		
		return sqlExpression;
	}

	@Override
	public String getMethodName() {
		return "statisticByCriteria";
	}

}
