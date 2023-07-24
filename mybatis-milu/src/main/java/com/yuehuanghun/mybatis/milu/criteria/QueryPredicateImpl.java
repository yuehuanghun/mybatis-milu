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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import javax.persistence.LockModeType;

import com.yuehuanghun.mybatis.milu.annotation.JoinMode;
import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.data.Sort.Direction;
import com.yuehuanghun.mybatis.milu.data.Sort.NullHandling;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.pagehelper.Pageable;
import com.yuehuanghun.mybatis.milu.tool.Assert;
import com.yuehuanghun.mybatis.milu.tool.Constants;
import com.yuehuanghun.mybatis.milu.tool.Segment;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

import lombok.Getter;

public class QueryPredicateImpl extends PredicateImpl implements QueryPredicate {

	private final Sort sort = new SortImpl();
	private Limit limit;
	private Lock lock;
	private Boolean distinct = Boolean.FALSE;
	@Getter
	private final Set<String> selectAttrs = new LinkedHashSet<>();
	@Getter
	private final Set<String> exselectAttrs = new HashSet<>();
	@Getter
	private final Map<String, Join> joinModeMap = new HashMap<>();

	@Override
	public QueryPredicate select(String... attrNames) {
		for (String attrName : attrNames) {
			this.selectAttrs.add(attrName);
		}
		return this;
	}

	@Override
	public QueryPredicate selects(String attrNameChain) {
		String[] arry = attrNameChain.split(Segment.COMMA);
		for (String attrName : arry) {
			if (StringUtils.isNotBlank(attrName)) {
				selectAttrs.add(attrName.trim());
			}
		}
		return this;
	}

	@Override
	public QueryPredicate exselect(String... attrNames) {
		for (String attrName : attrNames) {
			this.exselectAttrs.add(attrName);
		}
		return this;
	}

	@Override
	public QueryPredicate exselects(String attrNameChain) {
		String[] arry = attrNameChain.split(Segment.COMMA);
		for (String attrName : arry) {
			if (StringUtils.isNotBlank(attrName)) {
				exselectAttrs.add(attrName.trim());
			}
		}
		return this;
	}

	@Override
	public QueryPredicate order(String... attrNames) {
		for (String attrName : attrNames) {
			sort.add(attrName);
		}
		return this;
	}

	@Override
	public QueryPredicate order(NullHandling nullHandling, String... attrNames) {
		for (String attrName : attrNames) {
			sort.add(attrName, nullHandling);
		}
		return this;
	}

	@Override
	public QueryPredicate order(Direction direction, String... attrNames) {
		for (String attrName : attrNames) {
			sort.add(attrName, direction);
		}
		return this;
	}

	@Override
	public QueryPredicate order(Direction direction, NullHandling nullHandling, String... attrNames) {
		for (String attrName : attrNames) {
			sort.add(attrName, direction, nullHandling);
		}
		return this;
	}
	
	@Override
	public QueryPredicate order(com.yuehuanghun.mybatis.milu.data.Sort sort) {
		if(sort != null) {
			sort.forEach(order -> this.sort.add(order.getProperty(), order.getDirection(), order.getNullHandling()));
		}
		return this;
	}

	@Override
	public QueryPredicate orderAsc(String... attrNames) {
		this.order(Direction.ASC, attrNames);
		return this;
	}

	@Override
	public QueryPredicate orderAsc(NullHandling nullHandling, String... attrNames) {
		this.order(Direction.ASC, nullHandling, attrNames);
		return this;
	}

	@Override
	public QueryPredicate orderDesc(String... attrNames) {
		this.order(Direction.DESC, attrNames);
		return this;
	}

	@Override
	public QueryPredicate orderDesc(NullHandling nullHandling, String... attrNames) {
		this.order(Direction.DESC, nullHandling, attrNames);
		return this;
	}

	@Override
	public QueryPredicate limit(int pageSize) {
		this.limit = new LimitImpl(pageSize);
		return this;
	}

	@Override
	public QueryPredicate limit(int pageSize, boolean count) {
		this.limit = new LimitImpl(pageSize, count);
		return this;
	}

	@Override
	public QueryPredicate limit(int pageNum, int pageSize) {
		this.limit = new LimitImpl(pageNum, pageSize, true);
		return this;
	}

	@Override
	public QueryPredicate limit(int pageNum, int pageSize, boolean count) {
		this.limit = new LimitImpl(pageNum, pageSize, count);
		return this;
	}

	@Override
	public QueryPredicate limit(Pageable page) {
		this.limit = page == null ? null : new LimitImpl(page);
		return this;
	}
	
	@Override
	public QueryPredicate limitOffset(int offset, int size, boolean count) {
		this.limit = new LimitOffset(offset, size, count);
		return this;
	}

	@Override
	public QueryPredicate limitOffset(int offset, int size) {
		this.limit = new LimitOffset(offset, size);
		return this;
	}

	@Override
	public QueryPredicate conditionMode(Mode conditionMode) {
		super.conditionMode(conditionMode);
		return this;
	}

	@Override
	public QueryPredicate and(Condition... conditions) {
		super.and(conditions);
		return this;
	}

	@Override
	public QueryPredicate and(Consumer<Predicate> predicate) {
		super.and(predicate);
		return this;
	}

	@Override
	public QueryPredicate or(Condition... conditions) {
		super.or(conditions);
		return this;
	}

	@Override
	public QueryPredicate or(Consumer<Predicate> predicate) {
		super.or(predicate);
		return this;
	}

	@Override
	public QueryPredicate not(Condition... conditions) {
		super.or(conditions);
		return this;
	}

	@Override
	public QueryPredicate not(Consumer<Predicate> predicate) {
		super.not(predicate);
		return this;
	}

	@Override
	public QueryPredicate eq(String attrName, Object value) {
		super.eq(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate neq(String attrName, Object value) {
		super.neq(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate lt(String attrName, Object value) {
		super.lt(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate lte(String attrName, Object value) {
		super.lte(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate gt(String attrName, Object value) {
		super.gt(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate gte(String attrName, Object value) {
		super.gte(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate like(String attrName, Object value) {
		super.like(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate notLike(String attrName, Object value) {
		super.notLike(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate contain(String attrName, Object value) {
		super.contain(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate notContain(String attrName, Object value) {
		super.notContain(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate startWith(String attrName, Object value) {
		super.startWith(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate endWith(String attrName, Object value) {
		super.endWith(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate between(String attrName, Object startValue, Object endValue) {
		super.between(attrName, startValue, endValue);
		return this;
	}

	@Override
	public QueryPredicate between(boolean accept, String attrName, Object startValue, Object endValue) {
		super.between(accept, attrName, startValue, endValue);
		return this;
	}

	@Override
	public QueryPredicate isNull(String attrName) {
		super.isNull(attrName);
		return this;
	}

	@Override
	public QueryPredicate isNull(boolean accept, String attrName) {
		super.isNull(accept, attrName);
		return this;
	}

	@Override
	public QueryPredicate notNull(String attrName) {
		super.notNull(attrName);
		return this;
	}

	@Override
	public QueryPredicate notNull(boolean accept, String attrName) {
		super.notNull(accept, attrName);
		return this;
	}

	@Override
	public QueryPredicate in(String attrName, Object value) {
		super.in(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate notIn(String attrName, Object value) {
		super.notIn(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate eq(boolean accept, String attrName, Object value) {
		super.eq(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate neq(boolean accept, String attrName, Object value) {
		super.neq(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate lt(boolean accept, String attrName, Object value) {
		super.lt(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate lte(boolean accept, String attrName, Object value) {
		super.lte(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate gt(boolean accept, String attrName, Object value) {
		super.gt(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate gte(boolean accept, String attrName, Object value) {
		super.gte(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate like(boolean accept, String attrName, Object value) {
		super.like(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate notLike(boolean accept, String attrName, Object value) {
		super.notLike(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate contain(boolean accept, String attrName, Object value) {
		super.contain(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate notContain(boolean accept, String attrName, Object value) {
		super.notContain(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate startWith(boolean accept, String attrName, Object value) {
		super.startWith(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate endWith(boolean accept, String attrName, Object value) {
		super.endWith(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate in(boolean accept, String attrName, Object value) {
		super.in(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate notIn(boolean accept, String attrName, Object value) {
		super.notIn(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate regex(String attrName, Object value) {
		super.regex(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate regex(boolean accept, String attrName, Object value) {
		super.regex(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate undeleted() {
		super.undeleted();
		return this;
	}

	@Override
	public QueryPredicate deleted() {
		super.deleted();
		return this;
	}

	@Override
	public QueryPredicate distinct() {
		this.distinct = Boolean.TRUE;
		return this;
	}	
	
	/**
	 * 查询是否去重
	 * @return true/false
	 */
	public boolean isDistinct() {
		return this.distinct.booleanValue();
	}

	public QueryPredicate lock(LockModeType lockModeType) {
		if (lockModeType == null) {
			this.lock = null;
		} else {
			this.lock = new Lock(lockModeType);
		}
		return this;
	}

	@Override
	public QueryPredicate lock() {
		this.lock(LockModeType.PESSIMISTIC_WRITE);
		return this;
	}

	@Override
	public int renderSqlTemplate(GenericProviderContext context, StringBuilder expressionBuilder, Set<String> columns,
			int paramIndex) {
		paramIndex = super.renderSqlTemplate(context, expressionBuilder, columns, paramIndex);

		sort.renderSqlTemplate(context, expressionBuilder, columns, paramIndex);

		if (limit != null) {
			limit.renderSqlTemplate(context, expressionBuilder, columns, paramIndex);
		}

		if (lock != null) {
			lock.renderSqlTemplate(context, expressionBuilder, columns, paramIndex);
		}

		return paramIndex;
	}

	@Override
	public int renderParams(GenericProviderContext context, Map<String, Object> params, int paramIndex) {
		paramIndex = super.renderParams(context, params, paramIndex);
		paramIndex = sort.renderParams(context, params, paramIndex);
		if (limit != null) {
			paramIndex = limit.renderParams(context, params, paramIndex);
		}

		if (lock != null) {
			paramIndex = lock.renderParams(context, params, paramIndex);
		}
		return paramIndex;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();

		result = 31 * result + sort.hashCode();
		result = 31 * result + selectAttrs.hashCode();
		result = 31 * result + exselectAttrs.hashCode();
		result = lock == null ? result : 31 * result + lock.hashCode();
		result = 31 * result + distinct.hashCode();
		result = joinModeMap.isEmpty() ? result : 31 * result + joinModeMap.hashCode();
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
		
		QueryPredicateImpl that = (QueryPredicateImpl) obj;

		return Objects.equals(this.sort, that.sort) && Objects.equals(this.exselectAttrs, that.exselectAttrs)
				&& Objects.equals(this.selectAttrs, that.selectAttrs) && Objects.equals(this.lock, that.lock)
				&& Objects.equals(this.distinct, that.distinct) && Objects.equals(this.joinModeMap, that.joinModeMap);
	}

	@Override
	public QueryPredicate joinMode(JoinMode joinMode) {
		Assert.notNull(joinMode, "joinMode不能为null");
		this.joinModeMap.put(Constants.ANY_REF_PROPERTY, new Join(joinMode, null));
		return this;
	}

	@Override
	public QueryPredicate joinMode(String propertyName, JoinMode joinMode) {
		this.joinMode(propertyName, joinMode, (Predicate)null);
		return this;
	}

	@Override
	public QueryPredicate joinMode(String propertyName, JoinMode joinMode, Consumer<Predicate> joinPredicate) {
		Predicate predicate = Predicates.predicate();
		joinPredicate.accept(predicate);
		return joinMode(propertyName, joinMode, predicate);
	}

	@Override
	public QueryPredicate joinMode(String propertyName, JoinMode joinMode, Predicate joinPredicate) {
		Assert.notNull(joinMode, "joinMode不能为null");
		Assert.notBlank(propertyName, "propertyName不能为空");
		this.joinModeMap.put(propertyName, new Join(joinMode, joinPredicate));
		return this;
	}

	@Override
	public QueryPredicate byExample(Object example) {
		super.byExample(example);
		return this;
	}

	@Override
	public QueryPredicate fulltext(Collection<String> attrNames, String keywordExpression) {
		super.fulltext(attrNames, keywordExpression);
		return this;
	}

	@Override
	public QueryPredicate fulltext(Collection<String> attrNames, String keywordExpression, FulltextMode fulltextMode) {
		super.fulltext(attrNames, keywordExpression, fulltextMode);
		return this;
	}
	
}
