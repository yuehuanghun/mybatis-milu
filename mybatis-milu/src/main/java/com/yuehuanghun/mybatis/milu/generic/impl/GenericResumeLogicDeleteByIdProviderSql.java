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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.yuehuanghun.mybatis.milu.criteria.UpdatePredicate;
import com.yuehuanghun.mybatis.milu.criteria.UpdatePredicateImpl;
import com.yuehuanghun.mybatis.milu.criteria.builder.LogicDeleteSqlTemplateBuilder;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderSql;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.tool.Constants;
import com.yuehuanghun.mybatis.milu.tool.logicdel.LogicDeleteProvider;

public class GenericResumeLogicDeleteByIdProviderSql implements GenericProviderSql {
	
	private final Map<Class<?>, String> cache = new ConcurrentHashMap<>();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		Entity entity = context.getEntity();
		Map paramMap = (Map)params;
		
		if(entity.getLogicDeleteAttributes().isEmpty()) {
			throw new SqlExpressionBuildingException(String.format("实体类%s没有逻辑删除属性", entity.getName()));
		}
		
		try {
			Object entityObj = entity.getJavaType().newInstance();
			entity.getLogicDeleteAttributes().forEach(attr -> {
				try {
					attr.getSetter().invoke(entityObj, new Object[] {attr.getProvider().resumeValue(new LogicDeleteProvider.Context(entity.getJavaType(), attr.getJavaType(), attr.getName()))});
				} catch (IllegalAccessException | InvocationTargetException e) {
					throw new SqlExpressionBuildingException(e);
				}
			});
			
			paramMap.put(Constants.ENTITY, entityObj);
			UpdatePredicate predicate = new UpdatePredicateImpl();
			Attribute idAttr = entity.getId();
			if(idAttr == null) {
				throw new SqlExpressionBuildingException("id属性不存在");
			}
			predicate.eq(idAttr.getName(), paramMap.get(Constants.ID));
			
			SqlBuildingHelper.fill(entityObj, false, context.getConfiguration());
			
			Map<String, Object> queryParams = new HashMap<>();
			predicate.renderParams(context, queryParams, 0);
			((Map)params).putAll(queryParams);

			return cache.computeIfAbsent(context.getMapperType(), (key) -> {
				return new LogicDeleteSqlTemplateBuilder(context, predicate).build();
			});			
		} catch (InstantiationException | IllegalAccessException e) {
			throw new SqlExpressionBuildingException(e);
		}
	}

	@Override
	public String getMethodName() {
		return "resumeLogicDeletedById";
	}

}
