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

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.yuehuanghun.mybatis.milu.criteria.QueryPredicate;
import com.yuehuanghun.mybatis.milu.data.Sort;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.metamodel.FetchRef;
import com.yuehuanghun.mybatis.milu.pagehelper.Pageable;
import com.yuehuanghun.mybatis.milu.tool.Constants;

public class GenericFindByExampleProviderSql extends GenericFindByCriteriaProviderSql {

	@SuppressWarnings({ "unchecked"})
	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		Map<String, Object> paramMap = (Map<String, Object>)params;
		Object example = paramMap.remove(Constants.EXAMPLE);
		QueryPredicate queryPredicate = SqlBuildingHelper.exampleToQueryPredicate(context.getEntity(), example);
		
		if(paramMap.containsKey(Constants.SORT)) {
			queryPredicate.order((Sort)paramMap.remove(Constants.SORT));
		}

		if(paramMap.containsKey(Constants.PAGE)) {
			queryPredicate.limit((Pageable)paramMap.remove(Constants.PAGE));
		} else if(example instanceof Pageable) {
			Pageable page = (Pageable)example;
			if(page.getPageNum() > 0 && page.getPageSize() > 0) {
				queryPredicate.limit(page);
			}
		}
		
		if(paramMap.containsKey(Constants.GROUP)) {
			String group = (String) paramMap.remove(Constants.GROUP);
			FetchRef fetchRef = context.getEntity().getFetchRefs(group);
			if(fetchRef == null) {
				throw new SqlExpressionBuildingException(String.format("实体类%s没有组名为%s的引用查询配置", context.getEntity().getJavaType().getName(), group));
			}
			String refChain = Stream.of(fetchRef.getRefAttrs()).map(name -> name + "*").collect(Collectors.joining(","));
			queryPredicate.selects("*," + refChain);
			queryPredicate.joinMode(fetchRef.getJoinMode());
		} else {
			FetchRef fetchRef = context.getEntity().getFetchRefs("default");
			if(fetchRef != null) {
				String refChain = Stream.of(fetchRef.getRefAttrs()).map(name -> name + "*").collect(Collectors.joining(","));
				queryPredicate.selects("*," + refChain);
				queryPredicate.joinMode(fetchRef.getJoinMode());
			}
		}
		
		paramMap.put(Constants.CRITERIA, queryPredicate);
		
		return super.provideSql(context, paramMap);
	}

	@Override
	public String getMethodName() {
		return "findByExample";
	}

}
