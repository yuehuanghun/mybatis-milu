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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.ibatis.scripting.xmltags.OgnlCache;

import com.yuehuanghun.mybatis.milu.criteria.PredicateImpl;
import com.yuehuanghun.mybatis.milu.criteria.QueryPredicate;
import com.yuehuanghun.mybatis.milu.data.Sort;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.LogicDeleteAttribute;
import com.yuehuanghun.mybatis.milu.metamodel.FetchRef;
import com.yuehuanghun.mybatis.milu.pagehelper.Pageable;
import com.yuehuanghun.mybatis.milu.tool.Constants;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;
import com.yuehuanghun.mybatis.milu.tool.logicdel.LogicDeleteProvider.Context;

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
		
		if(context.getEntity().isFilterLogicDeletedData()) {
			List<LogicDeleteAttribute> logicDeleteAttributes = context.getEntity().getLogicDeleteAttributes();
			LogicDeleteAttribute logicDeleteAttribute;
			if(logicDeleteAttributes.size() == 1) {
				logicDeleteAttribute = logicDeleteAttributes.get(0);
			} else {
				logicDeleteAttribute = logicDeleteAttributes.stream().filter(item -> item.isMain()).findAny().orElse(null);
			}
			if(logicDeleteAttribute == null || OgnlCache.getValue(logicDeleteAttribute.getName(), example) == null) {
				queryPredicate.undeleted();
			}
		}
		
		FetchRef fetchRef;
		if(paramMap.containsKey(Constants.GROUP)) {
			String group = (String) paramMap.remove(Constants.GROUP);
			fetchRef = context.getEntity().getFetchRefs(group);
			if(fetchRef == null) {
				throw new SqlExpressionBuildingException(String.format("实体类%s没有组名为%s的引用查询配置", context.getEntity().getJavaType().getName(), group));
			}
		} else {
			fetchRef = context.getEntity().getFetchRefs("default");
		}
		
		if(fetchRef != null) {
			String refChain = Stream.of(fetchRef.getRefAttrs()).map(name -> name + "*").collect(Collectors.joining(","));
			queryPredicate.selects("*," + refChain);
			queryPredicate.joinMode(fetchRef.getJoinMode());
			
			for(String refAttr : fetchRef.getRefAttrs()) {
				Entry<String, Object> pair = getRefLogicDeleteValue(context, refAttr);
				if(pair != null) {
					queryPredicate.joinMode(refAttr, fetchRef.getJoinMode(), new PredicateImpl().eq(pair.getKey(), pair.getValue()));
				}
			}
		}
		
//		if(fetchRefAttrs != null) {
//			for(String refAttr : fetchRefAttrs) {
//				Class<?> refEntityClass = context.getEntity().getAttribute(refAttr).getEntityClass();
//				Entity refEntity = context.getConfiguration().getMetaModel().getEntity(refEntityClass);
//				
//				if(!refEntity.isFilterLogicDeletedData()) {
//					continue;
//				}
//				
//				List<LogicDeleteAttribute> refEntitylogicDeleteAttributes = refEntity.getLogicDeleteAttributes();
//				
//				LogicDeleteAttribute logicDeleteAttribute;
//				if(refEntitylogicDeleteAttributes.size() == 1) {
//					logicDeleteAttribute = refEntitylogicDeleteAttributes.get(0);
//				} else {
//					logicDeleteAttribute = refEntitylogicDeleteAttributes.stream().filter(item -> item.isMain()).findAny().orElse(null);
//				}
//				
//				if(logicDeleteAttribute != null) {
//					String attr = refAttr + StringUtils.capitalize(logicDeleteAttribute.getName());
//					queryPredicate.and(ap -> ap.eq(attr, logicDeleteAttribute.getProvider().resumeValue(new Context(refEntityClass, refEntity.getJavaType(), refEntity.getName()))).or(op -> op.isNull(attr)));
//				}
//			}
//		}
		
		paramMap.put(Constants.CRITERIA, queryPredicate);
		
		return super.provideSql(context, paramMap);
	}
	
	private Entry<String, Object> getRefLogicDeleteValue(GenericProviderContext context, String refAttr) {
		Class<?> refEntityClass = context.getEntity().getAttribute(refAttr).getEntityClass();
		Entity refEntity = context.getConfiguration().getMetaModel().getEntity(refEntityClass);
		
		if(!refEntity.isFilterLogicDeletedData()) {
			return null;
		}
		
		List<LogicDeleteAttribute> refEntitylogicDeleteAttributes = refEntity.getLogicDeleteAttributes();
		
		LogicDeleteAttribute logicDeleteAttribute;
		if(refEntitylogicDeleteAttributes.size() == 1) {
			logicDeleteAttribute = refEntitylogicDeleteAttributes.get(0);
		} else {
			logicDeleteAttribute = refEntitylogicDeleteAttributes.stream().filter(item -> item.isMain()).findAny().orElse(null);
		}
		
		if(logicDeleteAttribute != null) {
			String attr = refAttr + StringUtils.capitalize(logicDeleteAttribute.getName());
			Object val = logicDeleteAttribute.getProvider().resumeValue(new Context(refEntityClass, refEntity.getJavaType(), refEntity.getName()));
			return new Entry<String, Object>() {				
				@Override
				public String setValue(Object value) {
					return null;
				}
				
				@Override
				public Object getValue() {
					return val;
				}
				
				@Override
				public String getKey() {
					return attr;
				}
			};
		}
		return null;
	}

	@Override
	public String getMethodName() {
		return "findByExample";
	}

}
