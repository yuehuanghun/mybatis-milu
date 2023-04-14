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
import com.yuehuanghun.mybatis.milu.criteria.Predicate;
import com.yuehuanghun.mybatis.milu.criteria.QueryPredicate;
import com.yuehuanghun.mybatis.milu.criteria.QueryPredicateImpl;
import com.yuehuanghun.mybatis.milu.criteria.builder.QuerySqlTemplateBuilder;
import com.yuehuanghun.mybatis.milu.criteria.builder.QuerySqlTemplateBuilder.BuildResult;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderSql;
import com.yuehuanghun.mybatis.milu.mapping.ResultMapHelper;
import com.yuehuanghun.mybatis.milu.pagehelper.Pageable;
import com.yuehuanghun.mybatis.milu.tool.Constants;

public class GenericFindByCriteriaProviderSql implements GenericProviderSql {

	private final Map<Class<?>, Map<Expression, BuildResult>> cache = new ConcurrentHashMap<>();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		Map paramMap = ((Map)params);
		Object criteria = paramMap.remove(Constants.CRITERIA);
		QueryPredicate predicate;
		
		if(Consumer.class.isInstance(criteria)) {
			predicate = new QueryPredicateImpl();			
			((Consumer<Predicate>)criteria).accept(predicate);
		} else {
			predicate = (QueryPredicate) criteria;
		}

		if(paramMap.containsKey(Constants.RESULT_TYPE)) { //动态resultType
			ResultMapHelper.setResultType((Class<?>) paramMap.remove(Constants.RESULT_TYPE));
		}
		
		Map<String, Object> queryParams = new HashMap<>();
		predicate.renderParams(context, queryParams, 0);
		
		 // 处理主动使用PageHelper发起分页设置的，转换排序中的属性为列名
		SqlBuildingHelper.convertLocalPageOrder(context.getEntity(), context.getConfiguration());
		
		if(queryParams.containsKey(Constants.PAGE_KEY)) {
			Pageable page = (Pageable)queryParams.remove(Constants.PAGE_KEY);
			PageHelper.startPage(page.getPageNum(), page.getPageSize(), page.isCount());
		}
		
		paramMap.putAll(queryParams);

		BuildResult result = cache.computeIfAbsent(context.getMapperType(), (clazz) -> {
			return new SoftValueHashMap<>();
		}).computeIfAbsent(predicate, (key) -> {
			return new QuerySqlTemplateBuilder(context, predicate).build();
		});
		
		if(!result.getResultMappings().isEmpty()) {
			ResultMapHelper.setResultMappingList(result.getResultMappings());
		}
		
		return result.getSqlTemplate();
	}

	@Override
	public String getMethodName() {
		return "findByCriteria";
	}

}
