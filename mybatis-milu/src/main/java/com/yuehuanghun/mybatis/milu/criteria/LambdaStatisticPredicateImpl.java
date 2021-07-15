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

import java.util.Objects;
import java.util.function.Consumer;

import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.criteria.lambda.LambdaReflections;
import com.yuehuanghun.mybatis.milu.criteria.lambda.SerializableFunction;
import com.yuehuanghun.mybatis.milu.data.Sort.Direction;

import lombok.Getter;

public class LambdaStatisticPredicateImpl<T> extends LambdaPredicateImpl<T> implements LambdaStatisticPredicate<T> {
	
	@Getter
	private StatisticPredicateImpl delegate = new StatisticPredicateImpl();

	@Override
	public LambdaStatisticPredicate<T> sum(SerializableFunction<T, ?> getterFn) {
		getDelegate().sum(LambdaReflections.fnToFieldName(getterFn));
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> sum(SerializableFunction<T, ?> getterFn, String columnAlias) {
		getDelegate().sum(LambdaReflections.fnToFieldName(getterFn), columnAlias);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> count(SerializableFunction<T, ?> getterFn) {
		getDelegate().count(LambdaReflections.fnToFieldName(getterFn));
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> count(SerializableFunction<T, ?> getterFn, String columnAlias) {
		getDelegate().count(LambdaReflections.fnToFieldName(getterFn), columnAlias);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> avg(SerializableFunction<T, ?> getterFn) {
		getDelegate().avg(LambdaReflections.fnToFieldName(getterFn));
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> avg(SerializableFunction<T, ?> getterFn, String columnAlias) {
		getDelegate().avg(LambdaReflections.fnToFieldName(getterFn), columnAlias);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> min(SerializableFunction<T, ?> getterFn) {
		getDelegate().min(LambdaReflections.fnToFieldName(getterFn));
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> min(SerializableFunction<T, ?> getterFn, String columnAlias) {
		getDelegate().min(LambdaReflections.fnToFieldName(getterFn), columnAlias);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> max(SerializableFunction<T, ?> getterFn) {
		getDelegate().max(LambdaReflections.fnToFieldName(getterFn));
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> max(SerializableFunction<T, ?> getterFn, String columnAlias) {
		getDelegate().max(LambdaReflections.fnToFieldName(getterFn), columnAlias);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public LambdaStatisticPredicate<T> groupBy(SerializableFunction<T, ?>... getterFns) {
		String[] attrNames = new String[getterFns.length];
		for(int i = 0; i < getterFns.length; i++) {
			SerializableFunction<T, ?> getterFn = getterFns[i];
			attrNames[i] = LambdaReflections.fnToFieldName(getterFn);
		}
		getDelegate().groupBy(attrNames);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public LambdaStatisticPredicate<T> order(SerializableFunction<T, ?>... getterFns) {
		this.order(null, getterFns);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public LambdaStatisticPredicate<T> order(Direction direction, SerializableFunction<T, ?>... getterFns) {
		String[] attrNames = new String[getterFns.length];
		for(int i = 0; i < getterFns.length; i++) {
			SerializableFunction<T, ?> getterFn = getterFns[i];
			attrNames[i] = LambdaReflections.fnToFieldName(getterFn);
		}
		getDelegate().order(direction, attrNames);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public LambdaStatisticPredicate<T> orderAsc(SerializableFunction<T, ?>... getterFns) {
		this.order(Direction.ASC, getterFns);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public LambdaStatisticPredicate<T> orderDesc(SerializableFunction<T, ?>... getterFns) {
		this.order(Direction.DESC, getterFns);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> limit(int pageSize) {
		getDelegate().limit(pageSize);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> limit(int pageSize, boolean count) {
		getDelegate().limit(pageSize, count);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> limit(int pageNum, int pageSize) {
		getDelegate().limit(pageNum, pageSize);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> limit(int pageNum, int pageSize, boolean count) {
		getDelegate().limit(pageNum, pageSize, count);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> apply(T entity) {
		super.apply(entity);
		return this;
	}
	
	@Override
	public LambdaStatisticPredicate<T> conditionMode(Mode conditionMode) {
		super.conditionMode(conditionMode);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> and(Condition... conditions) {
		super.and(conditions);
		return this;
	}
	
	@Override
	public LambdaStatisticPredicate<T> and(Consumer<LambdaPredicate<T>> predicate) {
		super.and(predicate);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> or(Condition... conditions) {
		super.or(conditions);
		return this;
	}
	
	@Override
	public LambdaStatisticPredicate<T> or(Consumer<LambdaPredicate<T>> predicate) {
		super.or(predicate);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> not(Condition... conditions) {
		super.not(conditions);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> not(Consumer<LambdaPredicate<T>> predicate) {
		super.not(predicate);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> eq(SerializableFunction<T, ?> getterFn) {
		super.eq(getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> eq(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.eq(accept, getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> neq(SerializableFunction<T, ?> getterFn) {
		super.neq(getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> neq(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.neq(accept, getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> lt(SerializableFunction<T, ?> getterFn) {
		super.lt(getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> lt(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.lt(accept, getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> lte(SerializableFunction<T, ?> getterFn) {
		super.lte(getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> lte(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.lte(accept, getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> gt(SerializableFunction<T, ?> getterFn) {
		super.gt(getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> gt(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.gt(accept, getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> gte(SerializableFunction<T, ?> getterFn) {
		super.gte(getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> gte(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.gte(accept, getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> like(SerializableFunction<T, ?> getterFn) {
		super.like(getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> like(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.like(accept, getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> notLike(SerializableFunction<T, ?> getterFn) {
		super.notLike(getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> notLike(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.notLike(accept, getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> contain(SerializableFunction<T, ?> getterFn) {
		super.contain(getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> contain(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.contain(accept, getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> notContain(SerializableFunction<T, ?> getterFn) {
		super.notContain(getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> notContain(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.notContain(accept, getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> startWith(SerializableFunction<T, ?> getterFn) {
		super.startWith(getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> startWith(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.startWith(accept, getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> endWith(SerializableFunction<T, ?> getterFn) {
		super.endWith(getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> endWith(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.endWith(accept, getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> between(SerializableFunction<T, ?> getterFn, Object startValue, Object endValue) {
		super.between(getterFn, startValue, endValue);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> between(boolean accept, SerializableFunction<T, ?> getterFn, Object startValue,
			Object endValue) {
		super.between(accept, getterFn, startValue, endValue);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> isNull(SerializableFunction<T, ?> getterFn) {
		super.isNull(getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> notNull(SerializableFunction<T, ?> getterFn) {
		super.notNull(getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> in(SerializableFunction<T, ?> getterFn) {
		super.in(getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> in(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.in(accept, getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> notIn(SerializableFunction<T, ?> getterFn) {
		super.notIn(getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> notIn(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.notIn(accept, getterFn);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> eq(SerializableFunction<T, ?> getterFn, Object value) {
		super.eq(getterFn, value);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> eq(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.eq(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> neq(SerializableFunction<T, ?> getterFn, Object value) {
		super.neq(getterFn, value);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> neq(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.neq(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> lt(SerializableFunction<T, ?> getterFn, Object value) {
		super.lt(getterFn, value);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> lt(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.lt(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> lte(SerializableFunction<T, ?> getterFn, Object value) {
		super.lte(getterFn, value);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> lte(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.lte(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> gt(SerializableFunction<T, ?> getterFn, Object value) {
		super.gt(getterFn, value);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> gt(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.gt(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> gte(SerializableFunction<T, ?> getterFn, Object value) {
		super.gte(getterFn, value);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> gte(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.gte(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> like(SerializableFunction<T, ?> getterFn, Object value) {
		super.like(getterFn, value);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> like(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.like(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> notLike(SerializableFunction<T, ?> getterFn, Object value) {
		super.notLike(getterFn, value);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> notLike(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.notLike(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> contain(SerializableFunction<T, ?> getterFn, Object value) {
		super.contain(getterFn, value);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> contain(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.contain(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> notContain(SerializableFunction<T, ?> getterFn, Object value) {
		super.notContain(getterFn, value);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> notContain(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.notContain(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> startWith(SerializableFunction<T, ?> getterFn, Object value) {
		super.startWith(getterFn, value);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> startWith(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.startWith(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> endWith(SerializableFunction<T, ?> getterFn, Object value) {
		super.endWith(getterFn, value);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> endWith(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.endWith(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> in(SerializableFunction<T, ?> getterFn, Object value) {
		super.in(getterFn, value);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> in(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.in(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> notIn(SerializableFunction<T, ?> getterFn, Object value) {
		super.notIn(getterFn, value);
		return this;
	}

	@Override
	public LambdaStatisticPredicate<T> notIn(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.notIn(accept, getterFn, value);
		return this;
	}
	
	@Override
	public LambdaStatisticPredicate<T> regex(SerializableFunction<T, ?> getterFn, Object value) {
		super.regex(getterFn, value);
		return this;
	}
	
	@Override
	public LambdaStatisticPredicate<T> regex(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.regex(accept, getterFn, value);
		return this;
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	@Override
	public boolean equals(Object that) {
		if(!this.getClass().isInstance(that)) {
			return false;
		}
		return Objects.equals(delegate, ((LambdaStatisticPredicateImpl<?>)that).getDelegate());
	}
}
