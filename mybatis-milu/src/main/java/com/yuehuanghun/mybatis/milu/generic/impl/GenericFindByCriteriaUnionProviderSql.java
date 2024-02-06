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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.ibatis.javassist.scopedpool.SoftValueHashMap;
import org.apache.ibatis.mapping.ResultMapping;

import com.github.pagehelper.PageHelper;
import com.yuehuanghun.mybatis.milu.criteria.Join;
import com.yuehuanghun.mybatis.milu.criteria.Limit;
import com.yuehuanghun.mybatis.milu.criteria.LimitOffset;
import com.yuehuanghun.mybatis.milu.criteria.Predicate;
import com.yuehuanghun.mybatis.milu.criteria.Predicates;
import com.yuehuanghun.mybatis.milu.criteria.QueryPredicate;
import com.yuehuanghun.mybatis.milu.criteria.QueryPredicateImpl;
import com.yuehuanghun.mybatis.milu.criteria.builder.QuerySqlTemplateBuilder;
import com.yuehuanghun.mybatis.milu.criteria.builder.QuerySqlTemplateBuilder.BuildResult;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.exception.OrmBuildingException;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderSql;
import com.yuehuanghun.mybatis.milu.mapping.ResultMapHelper;
import com.yuehuanghun.mybatis.milu.pagehelper.Pageable;
import com.yuehuanghun.mybatis.milu.tool.Constants;
import com.yuehuanghun.mybatis.milu.tool.Segment;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

public class GenericFindByCriteriaUnionProviderSql implements GenericProviderSql {

	private final Map<Class<?>, Map<Object, BuildResult>> cache = new ConcurrentHashMap<>();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		Map paramMap = ((Map)params);
		Object[] criteriaGroups = (Object[]) paramMap.remove(Constants.CRITERIA);
		if(criteriaGroups == null || criteriaGroups.length == 0) {
			throw new OrmBuildingException("联合查询条件组数量不可小于1");
		}
		
		String[] selectAttrNames = (String[]) paramMap.remove(Constants.SELECTS);
		
		List<QueryPredicate> predicates = new ArrayList<>();
				
		for(Object criteria : criteriaGroups) {
			QueryPredicate predicate = Predicates.queryPredicate();
			if(Consumer.class.isInstance(criteria)) {		
				((Consumer<Predicate>)criteria).accept(predicate);
			} else {
				predicate.and((Predicate) criteria);
			}
			if(selectAttrNames != null && selectAttrNames.length > 0) {
				predicate.select(selectAttrNames);
			}
			predicate.end();
			predicates.add(predicate);
		}
		
		if(paramMap.containsKey(Constants.RESULT_TYPE)) { //动态resultType
			ResultMapHelper.setResultType((Class<?>) paramMap.remove(Constants.RESULT_TYPE));
		}
		
		Map<String, Object> queryParams = new HashMap<>();
		
		BuildResult result = cache.computeIfAbsent(context.getMapperType(), (clazz) -> {
			return new SoftValueHashMap<>();
		}).computeIfAbsent(predicates, (key) -> {
			List<String> sqls = new ArrayList<>();
			List<ResultMapping> resultMappings = null;
			int paramIndex = 0;
			
			for(QueryPredicate predicate : predicates) {
				int paramStartIndex = paramIndex;
				paramIndex = predicate.renderParams(context, queryParams, paramStartIndex);
				
				for(Join join : ((QueryPredicateImpl)predicate).getJoinModeMap().values()) {
					if(join.getJoinPredicate() != null) {
						paramIndex = join.getJoinPredicate().renderParams(context, queryParams, paramIndex);
					}
				}
				
				BuildResult subResult = new QuerySqlTemplateBuilder(context, predicate, paramStartIndex).build();
				sqls.add(subResult.getSqlTemplate());
				resultMappings = subResult.getResultMappings();
			}
			
			String sqlTemplate = sqls.stream().collect(Collectors.joining(" UNION ALL "));
			
			sqlTemplate = Segment.SCRIPT_LABEL + sqlTemplate.replace(Segment.SCRIPT_LABEL, StringUtils.EMPTY).replace(Segment.SCRIPT_LABEL_END, StringUtils.EMPTY) + Segment.SCRIPT_LABEL_END;
			
			return new BuildResult(sqlTemplate, resultMappings, paramIndex);
		});
		
		paramMap.putAll(queryParams);
		
		 // 处理主动使用PageHelper发起分页设置的，转换排序中的属性为列名
		SqlBuildingHelper.convertLocalPageOrder(context.getEntity(), context.getConfiguration());
		
		if(paramMap.containsKey(Constants.PAGE_KEY)) {
			Limit limit = (Limit)paramMap.remove(Constants.PAGE_KEY);
			if(limit instanceof Pageable) {
				Pageable page = (Pageable)limit;
				PageHelper.startPage(page.getPageNum(), page.getPageSize(), page.isCount());
			} else if(limit instanceof LimitOffset) {
				LimitOffset limitOffset = (LimitOffset)limit;
				PageHelper.offsetPage(limitOffset.getOffset(), limitOffset.getSize(), limitOffset.isCount());
			}
		}

		if(!result.getResultMappings().isEmpty()) {
			ResultMapHelper.setResultMappingList(result.getResultMappings());
		}
		
		return result.getSqlTemplate();
	}

	@Override
	public String getMethodName() {
		return "findByCriteriaUnion";
	}

}
