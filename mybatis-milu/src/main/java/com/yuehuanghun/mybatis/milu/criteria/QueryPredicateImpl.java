/*
 * Copyright 2020-2021 the original author or authors.
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

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import javax.persistence.LockModeType;

import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.data.Sort.Direction;
import com.yuehuanghun.mybatis.milu.pagehelper.Pageable;
import com.yuehuanghun.mybatis.milu.tool.Segment;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

import lombok.Getter;

public class QueryPredicateImpl extends PredicateImpl implements QueryPredicate {

	private final Sort sort = new SortImpl();
	private Limit limit;
	private Lock lock;
	@Getter
	private final Set<String> selectAttrs = new HashSet<>();
	@Getter
	private final Set<String> exselectAttrs = new HashSet<>();

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
	public QueryPredicate order(Direction direction, String... attrNames) {
		for (String attrName : attrNames) {
			sort.add(attrName, direction);
		}
		return this;
	}

	@Override
	public QueryPredicate orderAsc(String... attrNames) {
		this.order(Direction.ASC, attrNames);
		return this;
	}

	@Override
	public QueryPredicate orderDesc(String... attrNames) {
		this.order(Direction.DESC, attrNames);
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
		this.limit = new LimitImpl(page);
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
	public QueryPredicate notNull(String attrName) {
		super.notNull(attrName);
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
	public int renderSqlTemplate(MiluConfiguration configuration, StringBuilder expressionBuilder, Set<String> columns,
			int paramIndex) {
		paramIndex = super.renderSqlTemplate(configuration, expressionBuilder, columns, paramIndex);

		sort.renderSqlTemplate(configuration, expressionBuilder, columns, paramIndex);

		if (limit != null) {
			limit.renderSqlTemplate(configuration, expressionBuilder, columns, paramIndex);
		}

		if (lock != null) {
			lock.renderSqlTemplate(configuration, expressionBuilder, columns, paramIndex);
		}

		return paramIndex;
	}

	@Override
	public int renderParams(Map<String, Object> params, int paramIndex) {
		paramIndex = super.renderParams(params, paramIndex);
		paramIndex = sort.renderParams(params, paramIndex);
		if (limit != null) {
			paramIndex = limit.renderParams(params, paramIndex);
		}

		if (lock != null) {
			paramIndex = lock.renderParams(params, paramIndex);
		}
		return paramIndex;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();

		result = 31 * result + sort.hashCode();
		result = 31 * result + selectAttrs.hashCode();
		result = 31 * result + exselectAttrs.hashCode();
		result = 31 * result + (lock == null ? 0 : lock.hashCode());
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
				&& Objects.equals(this.selectAttrs, that.selectAttrs) && Objects.equals(this.lock, that.lock);
	}
}
