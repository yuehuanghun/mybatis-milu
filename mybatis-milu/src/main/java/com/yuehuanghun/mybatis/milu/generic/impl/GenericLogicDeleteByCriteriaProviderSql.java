/*
 * Copyright 2020-current the original author or authors.
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
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.apache.ibatis.cache.Cache;

import com.yuehuanghun.mybatis.milu.criteria.UpdatePredicate;
import com.yuehuanghun.mybatis.milu.criteria.UpdatePredicateImpl;
import com.yuehuanghun.mybatis.milu.criteria.builder.LogicDeleteSqlTemplateBuilder;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderSql;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.tool.Constants;
import com.yuehuanghun.mybatis.milu.tool.cache.SynchronizedLruCache;
import com.yuehuanghun.mybatis.milu.tool.logicdel.LogicDeleteProvider;

public class GenericLogicDeleteByCriteriaProviderSql implements GenericProviderSql {

	private final Map<Class<?>, Cache> cache = new ConcurrentHashMap<>();
	
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
		
		predicate.end();
		
		try {
			Entity entity = context.getEntity();
			Object entityObj;
			if(Map.class.isAssignableFrom(entity.getJavaType())) {
				Map map;
				if(Modifier.isInterface(entity.getJavaType().getModifiers()) || Modifier.isAbstract(entity.getJavaType().getModifiers())) {
					map = new HashMap<>();
				} else {
					map = (Map) entity.getJavaType().newInstance();
				}
				
				entity.getLogicDeleteAttributes().forEach(attr -> {
					Object val = attr.getProvider().value(new LogicDeleteProvider.Context(entity.getJavaType(), attr.getJavaType(), attr.getName()));
					map.put(attr.getName(), val);
				});
				
				entityObj = map;
			} else {
				entityObj = entity.getJavaType().newInstance();
				entity.getLogicDeleteAttributes().forEach(attr -> {
					try {
						attr.getSetter().invoke(entityObj, new Object[] {attr.getProvider().value(new LogicDeleteProvider.Context(entity.getJavaType(), attr.getJavaType(), attr.getName()))});
					} catch (IllegalAccessException | InvocationTargetException e) {
						throw new SqlExpressionBuildingException(e);
					}
				});
			}
			
			((Map)params).put(Constants.ENTITY, entityObj);
			SqlBuildingHelper.fill(entityObj, false, context.getConfiguration());
		} catch (InstantiationException | IllegalAccessException e) {
			throw new SqlExpressionBuildingException(e);
		}
		
		Map<String, Object> queryParams = new HashMap<>();
		predicate.renderParams(context, queryParams, 0);
		((Map)params).putAll(queryParams);

		Cache buildTemplateCache = cache.computeIfAbsent(context.getMapperType(), (clazz) -> {
			return new SynchronizedLruCache(getMethodName()); // 使用LRU缓存，最多存储1024个缓存数据
		});
		
		String sqlTemplate = (String)buildTemplateCache.getObject(predicate);
		
		if(sqlTemplate == null) {
			sqlTemplate = new LogicDeleteSqlTemplateBuilder(context, predicate).build();
			buildTemplateCache.putObject(predicate, sqlTemplate);
		}
		
		return sqlTemplate;
	}

	@Override
	public String getMethodName() {
		return "logicDeleteByCriteria";
	}

}
