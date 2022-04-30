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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.apache.ibatis.javassist.scopedpool.SoftValueHashMap;
import org.apache.ibatis.reflection.MetaClass;

import com.yuehuanghun.mybatis.milu.criteria.Expression;
import com.yuehuanghun.mybatis.milu.criteria.UpdatePredicate;
import com.yuehuanghun.mybatis.milu.criteria.UpdatePredicateImpl;
import com.yuehuanghun.mybatis.milu.criteria.builder.UpdateSqlTemplateBuilder;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderSql;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.tool.Constants;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

public class GenericUpdateAttrByCriteriaProviderSql implements GenericProviderSql {

	private final Map<Class<?>, Map<Expression, String>> cache = new ConcurrentHashMap<>();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		Entity entity = context.getEntity();
		Map paramMap = (Map)params;
		String attrName = (String) paramMap.get(Constants.ATTR_NAME);
		if(StringUtils.isBlank(attrName)) {
			throw new SqlExpressionBuildingException("attrName不能为空");
		}
		
		if(!entity.hasAttribute(attrName)) {
			throw new SqlExpressionBuildingException(String.format("实体类%s不存在属性%s", entity.getJavaType().getName(), paramMap.get(Constants.ATTR_NAME)));
		}
		
		try {
			Object entityObj = entity.getJavaType().newInstance();
			
			Object value = paramMap.get(Constants.VALUE);
			if(value != null) {
				MetaClass metaClass = MetaClass.forClass(entity.getJavaType(), context.getConfiguration().getReflectorFactory());
				metaClass.getSetInvoker(attrName).invoke(entityObj, new Object[] {value});
			}
			
			paramMap.put(Constants.ENTITY, entityObj);
			SqlBuildingHelper.fill(params, false, context.getConfiguration());
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new SqlExpressionBuildingException(e);
		}
		
		Object criteria = ((Map)params).get(Constants.CRITERIA);
		UpdatePredicate predicate;
		
		if(Consumer.class.isInstance(criteria)) {
			predicate = new UpdatePredicateImpl();			
			((Consumer<UpdatePredicate>)criteria).accept(predicate);
		} else {
			predicate = (UpdatePredicate)criteria;
		}
		
		Map<String, Object> queryParams = new HashMap<>();
		predicate.renderParams(queryParams, 0);
		((Map)params).putAll(queryParams);

		String sqlExpression = cache.computeIfAbsent(context.getMapperType(), (clazz) -> {
			return new SoftValueHashMap<>();
		}).computeIfAbsent(predicate, (key) -> {
			return new UpdateSqlTemplateBuilder(context.getEntity(), context.getConfiguration(), predicate).setNullableUpdateAttrNames(Arrays.asList(attrName)).build();
		});
		
		return sqlExpression;
	}

	@Override
	public String getMethodName() {
		return "updateAttrByCriteria";
	}

}
