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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.apache.ibatis.javassist.scopedpool.SoftValueHashMap;

import com.yuehuanghun.mybatis.milu.criteria.Expression;
import com.yuehuanghun.mybatis.milu.criteria.UpdatePredicate;
import com.yuehuanghun.mybatis.milu.criteria.UpdatePredicateImpl;
import com.yuehuanghun.mybatis.milu.criteria.builder.LogicDeleteSqlTemplateBuilder;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderSql;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.tool.Constants;
import com.yuehuanghun.mybatis.milu.tool.logicdel.LogicDeleteProvider;

public class GenericResumeLogicDeleteByCriteriaProviderSql implements GenericProviderSql {

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
		
		try {
			Entity entity = context.getEntity();
			Object entityObj = entity.getJavaType().newInstance();
			entity.getLogicDeleteAttributes().forEach(attr -> {
				try {
					if(attr.getProvider() != null) {
						attr.getSetter().invoke(entityObj, new Object[] {attr.getProvider().resumeValue(new LogicDeleteProvider.Context(entity.getJavaType(), attr.getJavaType(), attr.getName()))});
					} else {
						attr.getSetter().invoke(entityObj, new Object[] {attr.getResumeValue()});
					}
				} catch (IllegalAccessException | InvocationTargetException e) {
					throw new SqlExpressionBuildingException(e);
				}
			});
			
			((Map)params).put(Constants.ENTITY, entityObj);
			SqlBuildingHelper.fill(entityObj, false, context.getConfiguration());
		} catch (InstantiationException | IllegalAccessException e) {
			throw new SqlExpressionBuildingException(e);
		}
		
		Map<String, Object> queryParams = new HashMap<>();
		predicate.renderParams(queryParams, 0);
		((Map)params).putAll(queryParams);

		return cache.computeIfAbsent(context.getMapperType(), (clazz) -> {
			return new SoftValueHashMap<>();
		}).computeIfAbsent(predicate, (key) -> {
			return new LogicDeleteSqlTemplateBuilder(context.getEntity(), context.getConfiguration(), predicate).build();
		});
	}

	@Override
	public String getMethodName() {
		return "resumeLogicDeletedByCriteria";
	}

}
