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
package com.yuehuanghun.mybatis.milu.criteria;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.ibatis.executor.keygen.NoKeyGenerator;

import com.yuehuanghun.mybatis.milu.BaseMapper;
import com.yuehuanghun.mybatis.milu.criteria.builder.QuerySqlTemplateBuilder;
import com.yuehuanghun.mybatis.milu.criteria.builder.QuerySqlTemplateBuilder.BuildResult;
import com.yuehuanghun.mybatis.milu.criteria.lambda.LambdaReflections;
import com.yuehuanghun.mybatis.milu.criteria.lambda.SerializableFunction;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.tool.Assert;
import com.yuehuanghun.mybatis.milu.tool.Segment;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

import lombok.AccessLevel;
import lombok.Setter;

/**
 * Exists 子查询条件
 * @author yuehuanghun
 *
 * @param <Entity> 实体类
 */
public class Exists<Entity> implements Condition {

	private Map<String, String> joins = new HashMap<>();
	
	private Class<? extends BaseMapper<Entity, ?>> mapperClass;
	
	private QueryPredicateImpl predicate;
	
	@Setter(value = AccessLevel.PROTECTED)
	private boolean not = false;
	
	private Exists(Class<? extends BaseMapper<Entity, ?>> mapperClass) {
		this.mapperClass = mapperClass;
	}
	
	public static <Entity> Exists<Entity> of(Class<? extends BaseMapper<Entity, ?>> mapperClass) {
		Assert.notNull(mapperClass, "mapper类不能为空");
		return new Exists<Entity>(mapperClass);
	}
	
	/**
	 * 关联条件
	 * @param attrName 当前exist子域实体的属性
	 * @param referenceAttrName 主域实体的属性
	 */
	public Exists<Entity> join(String attrName, String referenceAttrName) {
		Assert.notBlank(attrName, "联结属性名不能为空");
		Assert.notBlank(referenceAttrName, "关联属性名不能为空");
		joins.put(attrName, referenceAttrName);
		return this;
	}
	
	/**
	 * 关联条件
	 * @param attrNameGetterFn 当前exist子域实体的属性函数
	 * @param refAttrNamegetterFn 主域实体的属性函数
	 */
	public <T, M> Exists<Entity> join(SerializableFunction<T, ?> attrNameGetterFn, SerializableFunction<M, ?> refAttrNamegetterFn) {
		Assert.notNull(attrNameGetterFn, "联结属性名不能为空");
		Assert.notNull(refAttrNamegetterFn, "关联属性名不能为空");
		joins.put(LambdaReflections.fnToFieldName(attrNameGetterFn), LambdaReflections.fnToFieldName(refAttrNamegetterFn));
		return this;
	}
	
	/**
	 * exist 字句查询条件<br>
	 * 此方法多次被调用覆盖将前面的条件
	 * @param predicate
	 */
	public Exists<Entity> criteria(Consumer<QueryPredicate> predicate) {
		QueryPredicateImpl p = new QueryPredicateImpl();
		predicate.accept(p);
		this.predicate = p;
		return this;
	}
	
	/**
	 * exist 字句查询条件<br>
	 * 此方法多次被调用覆盖将前面的条件
	 * @param predicate
	 */
	public Exists<Entity> lambdaCriteria(Consumer<LambdaQueryPredicate<Entity>> predicate) {
		LambdaQueryPredicateImpl<Entity> p = new LambdaQueryPredicateImpl<>();
		predicate.accept(p);
		this.predicate = p.getDelegate();
		return this;
	}

	@Override
	public int renderSqlTemplate(GenericProviderContext context, StringBuilder expressionBuilder, Set<String> columns,
			int paramIndex) {
		if(this.not) {
			expressionBuilder.append("NOT ");
		}
		expressionBuilder.append("EXISTS ");
		expressionBuilder.append(Segment.SPACE).append(Segment.LEFT_BRACKET);
		GenericProviderContext existContext = new GenericProviderContext(mapperClass, null, context.getConfiguration(), NoKeyGenerator.INSTANCE, context.getConfiguration().getMapperEntity(mapperClass));
		if(this.predicate == null) {
			this.predicate = new QueryPredicateImpl();
		}
		if(!joins.isEmpty()) {
			joins.forEach((key, value) -> {
				predicate.existsJoin(key, value);
				columns.add(value);
			});
		}
		BuildResult existBuildResult = new QuerySqlTemplateBuilder(existContext, predicate, paramIndex).build();
		String existTemplate = existBuildResult.getSqlTemplate();
		if(!joins.isEmpty()) {
			existTemplate = existTemplate.replace(Segment.AT, Segment.DOLLAR); // 当前域字段
		}
		existTemplate = existTemplate.replace(Segment.SCRIPT_LABEL, StringUtils.EMPTY).replace(Segment.SCRIPT_LABEL_END, StringUtils.EMPTY);
		expressionBuilder.append(existTemplate);
		expressionBuilder.append(Segment.RIGHT_BRACKET);
		return existBuildResult.getParamIndex();
	}

	@Override
	public int renderParams(GenericProviderContext context, Map<String, Object> params, int paramIndex) {
		paramIndex = predicate.renderParams(context, params, paramIndex);
		
		for(Join join : predicate.getJoinModeMap().values()) {
			if(join.getJoinPredicate() != null) {
				paramIndex = join.getJoinPredicate().renderParams(context, params, paramIndex);
			}
		}
		return paramIndex;
	}
}
