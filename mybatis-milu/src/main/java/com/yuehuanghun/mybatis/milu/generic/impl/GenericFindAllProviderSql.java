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

import com.yuehuanghun.mybatis.milu.criteria.QueryPredicate;
import com.yuehuanghun.mybatis.milu.criteria.QueryPredicateImpl;
import com.yuehuanghun.mybatis.milu.data.Sort;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.tool.Constants;
import com.yuehuanghun.mybatis.milu.tool.Segment;

public class GenericFindAllProviderSql extends GenericFindByCriteriaProviderSql {

	private final QueryPredicate EMPTY = new QueryPredicateImpl();
	
	@SuppressWarnings("unchecked")
	@Override
	public String provideSql(GenericProviderContext context, Object params) {		
		QueryPredicate predicate;
		if(params == null) {
			predicate = EMPTY;
			params = new HashMap<>();
		} else {
			Sort sort = (Sort)((Map<String, Object>)params).remove(Segment.SORT);
			if(sort == null) {
				predicate = EMPTY;
			} else {
				predicate = new QueryPredicateImpl();
				predicate.order(sort);
			}
		}
		
		if(context.getEntity().isFilterLogicDeletedData()) {
			if(predicate == EMPTY) {
				predicate = new QueryPredicateImpl();
			}
			predicate.undeleted(); // 自动过滤已逻辑删除的数据
		}
		
		((Map<String, Object>)params).put(Constants.CRITERIA, predicate);
		
		return super.provideSql(context, params);
	}

	@Override
	public String getMethodName() {
		return "findAll";
	}

}
