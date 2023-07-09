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
import java.util.function.Consumer;

import com.yuehuanghun.mybatis.milu.criteria.LambdaPredicate;
import com.yuehuanghun.mybatis.milu.criteria.LambdaPredicateImpl;
import com.yuehuanghun.mybatis.milu.criteria.Predicate;
import com.yuehuanghun.mybatis.milu.exception.OrmBuildingException;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.tool.Constants;

public class GenericFindByLambdaCriteriaUnionProviderSql extends GenericFindByCriteriaUnionProviderSql {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		Map paramMap = ((Map)params);
		Object[] criteriaGroups = (Object[]) paramMap.remove(Constants.CRITERIA);
		if(criteriaGroups == null || criteriaGroups.length == 0) {
			throw new OrmBuildingException("联合查询条件组数量不可小于1");
		}
		
		Predicate[] predicates = new Predicate[criteriaGroups.length];
		
		for(int i = 0; i < criteriaGroups.length; i++) {
			LambdaPredicateImpl<?> predicate = new LambdaPredicateImpl<>();
			((Consumer<LambdaPredicate<?>>)criteriaGroups[i]).accept(predicate);
			predicates[i] = predicate.getDelegate();
		}
		
		paramMap.put(Constants.CRITERIA, predicates);
		
		return super.provideSql(context, params);
	}

	@Override
	public String getMethodName() {
		return "findByLambdaCriteriaUnion";
	}

}
