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

import com.yuehuanghun.mybatis.milu.criteria.LambdaUpdatePredicate;
import com.yuehuanghun.mybatis.milu.criteria.LambdaUpdatePredicateImpl;
import com.yuehuanghun.mybatis.milu.criteria.lambda.LambdaReflections;
import com.yuehuanghun.mybatis.milu.criteria.lambda.SerializableFunction;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.tool.Constants;

public class GenericUpdateAttrByLambdaCriteriaProviderSql extends GenericUpdateAttrByCriteriaProviderSql {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		Map paramMap = (Map)params;
		Object criteria = paramMap.get(Constants.CRITERIA);
		
		LambdaUpdatePredicateImpl<?> predicate = new LambdaUpdatePredicateImpl<>();
		((Consumer<LambdaUpdatePredicate>)criteria).accept(predicate);
		
		paramMap.put(Constants.CRITERIA, predicate.getDelegate()); //覆盖
		paramMap.put(Constants.ATTR_NAME, LambdaReflections.fnToFieldName((SerializableFunction) paramMap.get(Constants.ATTR_NAME)));
		
		return super.provideSql(context, params);
	}

	@Override
	public String getMethodName() {
		return "updateAttrByLambdaCriteria";
	}

}
