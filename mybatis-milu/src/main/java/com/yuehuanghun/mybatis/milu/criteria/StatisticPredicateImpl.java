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

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.data.Sort.Direction;
import com.yuehuanghun.mybatis.milu.dialect.Dialect;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.tool.Constants;
import com.yuehuanghun.mybatis.milu.tool.Segment;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

public class StatisticPredicateImpl extends PredicateImpl implements StatisticPredicate {
	private Select select = new SelectImpl();
	private final Sort sort = new SortImpl();
	private Limit limit;
	private final Group group = new GroupImpl();

	@Override
	public StatisticPredicate sum(String attrName) {
		select.add(Dialect.SUM, attrName, null);
		return this;
	}

	@Override
	public StatisticPredicate sum(String attrName, String columnAlias) {
		select.add(Dialect.SUM, attrName, columnAlias);
		return this;
	}

	@Override
	public StatisticPredicate count(String attrName) {
		select.add(Dialect.COUNT, attrName, null);
		return this;
	}

	@Override
	public StatisticPredicate count(String attrName, String columnAlias) {
		select.add(Dialect.COUNT, attrName, columnAlias);
		return this;
	}

	@Override
	public StatisticPredicate countDistinct(String attrName) {
		select.add(Dialect.COUNT_DISTINCT, attrName, null);
		return this;
	}

	@Override
	public StatisticPredicate countDistinct(String attrName, String columnAlias) {
		select.add(Dialect.COUNT_DISTINCT, attrName, columnAlias);
		return this;
	}

	@Override
	public StatisticPredicate avg(String attrName) {
		select.add(Dialect.AVG, attrName, null);
		return this;
	}

	@Override
	public StatisticPredicate avg(String attrName, String columnAlias) {
		select.add(Dialect.AVG, attrName, columnAlias);
		return this;
	}

	@Override
	public StatisticPredicate min(String attrName) {
		select.add(Dialect.MIN, attrName, null);
		return this;
	}

	@Override
	public StatisticPredicate min(String attrName, String columnAlias) {
		select.add(Dialect.MIN, attrName, columnAlias);
		return this;
	}

	@Override
	public StatisticPredicate max(String attrName) {
		select.add(Dialect.MAX, attrName, null);
		return this;
	}

	@Override
	public StatisticPredicate max(String attrName, String columnAlias) {
		select.add(Dialect.MAX, attrName, columnAlias);
		return this;
	}

	@Override
	public StatisticPredicate order(String... attrNames) {
		for(String attrName : attrNames) {
			sort.add(attrName);
		}
		return this;
	}

	@Override
	public StatisticPredicate order(Direction direction, String... attrNames) {
		for(String attrName : attrNames) {
			sort.add(attrName, direction);
		}
		return this;
	}

	@Override
	public StatisticPredicate orderAsc(String... attrNames) {
		this.order(Direction.ASC, attrNames);
		return this;
	}

	@Override
	public StatisticPredicate orderDesc(String... attrNames) {
		this.order(Direction.DESC, attrNames);
		return this;
	}

	@Override
	public StatisticPredicate limit(int pageSize) {
		this.limit = new LimitImpl(pageSize);
		return this;
	}
	
	@Override
	public StatisticPredicate limit(int pageSize, boolean count) {
		this.limit = new LimitImpl(pageSize, count);
		return this;
	}

	@Override
	public StatisticPredicate limit(int pageNum, int pageSize) {
		this.limit = new LimitImpl(pageNum, pageSize, true);
		return this;
	}
	
	@Override
	public StatisticPredicate limit(int pageNum, int pageSize, boolean count) {
		this.limit = new LimitImpl(pageNum, pageSize, count);
		return this;
	}
	
	@Override
	public StatisticPredicate conditionMode(Mode conditionMode) {
		super.conditionMode(conditionMode);
		return this;
	}

	@Override
	public StatisticPredicate and(Condition... conditions) {
		super.and(conditions);
		return this;
	}
	
	@Override
	public StatisticPredicate and(Consumer<Predicate> predicate) {
		super.and(predicate);
		return this;
	}

	@Override
	public StatisticPredicate or(Condition... conditions) {
		super.or(conditions);
		return this;
	}
	
	@Override
	public StatisticPredicate or(Consumer<Predicate> predicate) {
		super.or(predicate);
		return this;
	}
	
	@Override
	public StatisticPredicate not(Condition... conditions) {
		super.or(conditions);
		return this;
	}
	
	@Override
	public StatisticPredicate not(Consumer<Predicate> predicate) {
		super.not(predicate);
		return this;
	}

	@Override
	public StatisticPredicate eq(String attrName, Object value) {
		super.eq(attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate neq(String attrName, Object value) {
		super.neq(attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate lt(String attrName, Object value) {
		super.lt(attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate lte(String attrName, Object value) {
		super.lte(attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate gt(String attrName, Object value) {
		super.gt(attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate gte(String attrName, Object value) {
		super.gte(attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate like(String attrName, Object value) {
		super.like(attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate notLike(String attrName, Object value) {
		super.notLike(attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate contain(String attrName, Object value) {
		super.contain(attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate notContain(String attrName, Object value) {
		super.notContain(attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate startWith(String attrName, Object value) {
		super.startWith(attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate endWith(String attrName, Object value) {
		super.endWith(attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate between(String attrName, Object startValue, Object endValue) {
		super.between(attrName, startValue, endValue);
		return this;
	}
	
	@Override
	public StatisticPredicate between(boolean accept, String attrName, Object startValue, Object endValue) {
		super.between(accept, attrName, startValue, endValue);
		return this;
	}

	@Override
	public StatisticPredicate isNull(String attrName) {
		super.isNull(attrName);
		return this;
	}

	@Override
	public StatisticPredicate isNull(boolean accept, String attrName) {
		super.isNull(accept, attrName);
		return this;
	}

	@Override
	public StatisticPredicate notNull(String attrName) {
		super.notNull(attrName);
		return this;
	}

	@Override
	public StatisticPredicate notNull(boolean accept, String attrName) {
		super.notNull(accept, attrName);
		return this;
	}
	
	@Override
	public StatisticPredicate in(String attrName, Object value) {
		super.in(attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate notIn(String attrName, Object value) {
		super.notIn(attrName, value);
		return this;
	}
	
	@Override
	public StatisticPredicate eq(boolean accept, String attrName, Object value) {
		super.eq(accept, attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate neq(boolean accept, String attrName, Object value) {
		super.neq(accept, attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate lt(boolean accept, String attrName, Object value) {
		super.lt(accept, attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate lte(boolean accept, String attrName, Object value) {
		super.lte(accept, attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate gt(boolean accept, String attrName, Object value) {
		super.gt(accept, attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate gte(boolean accept, String attrName, Object value) {
		super.gte(accept, attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate like(boolean accept, String attrName, Object value) {
		super.like(accept, attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate notLike(boolean accept, String attrName, Object value) {
		super.notLike(accept, attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate contain(boolean accept, String attrName, Object value) {
		super.contain(accept, attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate notContain(boolean accept, String attrName, Object value) {
		super.notContain(accept, attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate startWith(boolean accept, String attrName, Object value) {
		super.startWith(accept, attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate endWith(boolean accept, String attrName, Object value) {
		super.endWith(accept, attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate in(boolean accept, String attrName, Object value) {
		super.in(accept, attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate notIn(boolean accept, String attrName, Object value) {
		super.notIn(accept, attrName, value);
		return this;
	}
	
	@Override
	public StatisticPredicate regex(String attrName, Object value) {
		super.regex(attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate groupBy(String... attrNames) {
		for(String attrName : attrNames){
			group.add(attrName);
			select.add(attrName);
		}
		return this;
	}
	
	@Override
	public StatisticPredicate groupByAs(String attrName, String alias) {
		if(StringUtils.isBlank(alias)) {
			select.add(attrName);
		} else {
			select.add(StringUtils.EMPTY, attrName, alias);
		}
		group.add(attrName);
		return this;
	}

	@Override
	public StatisticPredicate regex(boolean accept, String attrName, Object value) {
		super.regex(accept, attrName, value);
		return this;
	}

	@Override
	public StatisticPredicate undeleted() {
		super.undeleted();
		return this;
	}

	@Override
	public StatisticPredicate deleted() {
		super.deleted();
		return this;
	}
	
	@Override
	public int renderSqlTemplate(GenericProviderContext context, StringBuilder expressionBuilder, Set<String> columns,
			int paramIndex) {
		paramIndex = select.renderSqlTemplate(context, expressionBuilder, columns, paramIndex);
		
		expressionBuilder.append(Segment.FROM_B).append(Constants.TABLE_HOLDER).append(Segment.WHERE_LABEL);
		
		StringBuilder conditionBuilder = new StringBuilder();
		paramIndex = super.renderSqlTemplate(context, expressionBuilder, columns, paramIndex);
		
		expressionBuilder.append(conditionBuilder).append(Segment.WHERE_LABEL_END);
		
		paramIndex = group.renderSqlTemplate(context, expressionBuilder, columns, paramIndex);
		
		sort.renderSqlTemplate(context, expressionBuilder, columns, paramIndex);
	
		if(limit != null) {
			limit.renderSqlTemplate(context, expressionBuilder, columns, paramIndex);
		}
		
		return paramIndex;
	}

	@Override
	public int renderParams(GenericProviderContext context, Map<String, Object> params, int paramIndex) {
		paramIndex = select.renderParams(context, params, paramIndex);
		
		paramIndex = super.renderParams(context, params, paramIndex);
		
		paramIndex = group.renderParams(context, params, paramIndex);;
		
		paramIndex = sort.renderParams(context, params, paramIndex);
	
		if(limit != null) {
			paramIndex = limit.renderParams(context, params, paramIndex);
		}
		
		return paramIndex;
	}	

	@Override
	public int hashCode() {
		int result = super.hashCode();

		result = 31 * result + sort.hashCode();
		result = 31 * result + select.hashCode();
		result = 31 * result + group.hashCode();
		result = 31 * result + (limit == null ? 0 : limit.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!this.getClass().isInstance(obj)) {
			return false;
		}
		if (!super.equals(obj)) {
			return false;
		}
		
		StatisticPredicateImpl that = (StatisticPredicateImpl) obj;

		return Objects.equals(this.sort, that.sort) && Objects.equals(this.select, that.select)
				&& Objects.equals(this.group, that.group) && Objects.equals(this.limit, that.limit);
	}

}
