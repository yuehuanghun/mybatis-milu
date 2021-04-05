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

import com.yuehuanghun.mybatis.milu.criteria.CriteriaSqlBuilder;
import com.yuehuanghun.mybatis.milu.criteria.CriteriaSqlBuilder.CriteriaType;
import com.yuehuanghun.mybatis.milu.criteria.Predicate;
import com.yuehuanghun.mybatis.milu.criteria.QueryPredicateImpl;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderSql;

public class GenericDeleteByCriteriaProviderSql implements GenericProviderSql {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		Object criteria = ((Map)params).get("criteria");
		Predicate predicate;
		
		if(Consumer.class.isInstance(criteria)) {
			predicate = new QueryPredicateImpl();			
			((Consumer<Predicate>)criteria).accept(predicate);
		} else {
			predicate = (Predicate)criteria;
		}
		
		StringBuilder expressionBuilder = new StringBuilder(256);
		Map<String, Object> queryParams = new HashMap<>();
		Set<String> queryProperties = new HashSet<>();
		
		predicate.render(context.getConfiguration(), expressionBuilder, queryParams, queryProperties, 0);
		
		((Map)params).putAll(queryParams);
		
		return CriteriaSqlBuilder.instance(context.getEntity().getJavaType(), expressionBuilder.toString(), queryProperties, context.getConfiguration()).setCriteriaType(CriteriaType.DELETE).build();
	}

	@Override
	public String getMethodName() {
		return "deleteByCriteria";
	}

}
