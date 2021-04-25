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

import java.util.function.Consumer;

import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.criteria.lambda.LambdaReflections;
import com.yuehuanghun.mybatis.milu.criteria.lambda.SerializableFunction;
import com.yuehuanghun.mybatis.milu.data.Sort.Direction;

import lombok.Getter;

public class LambdaQueryPredicateImpl<T> extends LambdaPredicateImpl<T> implements LambdaQueryPredicate<T> {
	
	@Getter
	private QueryPredicateImpl delegate = new QueryPredicateImpl();

	@SuppressWarnings("unchecked")
	@Override
	public LambdaQueryPredicate<T> select(SerializableFunction<T, ?>... getterFns) {
		String[] attrNames = new String[getterFns.length];
		for(int i = 0; i < getterFns.length; i++) {
			attrNames[i] = LambdaReflections.fnToFieldName(getterFns[i]);
		}
		getDelegate().select(attrNames);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> selects(String attrNameChain) {
		getDelegate().selects(attrNameChain);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public LambdaQueryPredicate<T> exselect(SerializableFunction<T, ?>... getterFns) {
		String[] attrNames = new String[getterFns.length];
		for(int i = 0; i < getterFns.length; i++) {
			attrNames[i] = LambdaReflections.fnToFieldName(getterFns[i]);
		}
		getDelegate().exselect(attrNames);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> exselects(String attrNameChain) {
		getDelegate().exselects(attrNameChain);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public LambdaQueryPredicate<T> order(SerializableFunction<T, ?>... getterFns) {
		this.order(null, getterFns);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public LambdaQueryPredicate<T> order(Direction direction, SerializableFunction<T, ?>... getterFns) {
		String[] attrNames = new String[getterFns.length];
		for(int i = 0; i < getterFns.length; i++) {
			SerializableFunction<T, ?> getterFn = getterFns[i];
			attrNames[i] = LambdaReflections.fnToFieldName(getterFn);
		}
		getDelegate().order(direction, attrNames);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> limit(int pageSize) {
		getDelegate().limit(pageSize);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> limit(int pageSize, boolean count) {
		getDelegate().limit(pageSize, count);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> limit(int pageNum, int pageSize) {
		getDelegate().limit(pageNum, pageSize);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> limit(int pageNum, int pageSize, boolean count) {
		getDelegate().limit(pageNum, pageSize, count);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> apply(T entity) {
		super.apply(entity);
		return this;
	}
	
	@Override
	public LambdaQueryPredicate<T> conditionMode(Mode conditionMode) {
		super.conditionMode(conditionMode);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> and(Condition... conditions) {
		super.and(conditions);
		return this;
	}
	
	@Override
	public LambdaQueryPredicate<T> and(Consumer<LambdaPredicate<T>> predicate) {
		super.and(predicate);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> or(Condition... conditions) {
		super.or(conditions);
		return this;
	}
	
	@Override
	public LambdaQueryPredicate<T> or(Consumer<LambdaPredicate<T>> predicate) {
		super.or(predicate);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> not(Condition... conditions) {
		super.not(conditions);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> not(Consumer<LambdaPredicate<T>> predicate) {
		super.not(predicate);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> eq(SerializableFunction<T, ?> getterFn) {
		super.eq(getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> eq(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.eq(accept, getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> neq(SerializableFunction<T, ?> getterFn) {
		super.neq(getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> neq(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.neq(accept, getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> lt(SerializableFunction<T, ?> getterFn) {
		super.lt(getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> lt(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.lt(accept, getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> lte(SerializableFunction<T, ?> getterFn) {
		super.lte(getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> lte(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.lte(accept, getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> gt(SerializableFunction<T, ?> getterFn) {
		super.gt(getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> gt(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.gt(accept, getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> gte(SerializableFunction<T, ?> getterFn) {
		super.gte(getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> gte(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.gte(accept, getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> like(SerializableFunction<T, ?> getterFn) {
		super.like(getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> like(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.like(accept, getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> notLike(SerializableFunction<T, ?> getterFn) {
		super.notLike(getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> notLike(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.notLike(accept, getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> contain(SerializableFunction<T, ?> getterFn) {
		super.contain(getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> contain(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.contain(accept, getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> notContain(SerializableFunction<T, ?> getterFn) {
		super.notContain(getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> notContain(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.notContain(accept, getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> startWith(SerializableFunction<T, ?> getterFn) {
		super.startWith(getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> startWith(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.startWith(accept, getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> endWith(SerializableFunction<T, ?> getterFn) {
		super.endWith(getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> endWith(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.endWith(accept, getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> between(SerializableFunction<T, ?> getterFn, Object startValue, Object endValue) {
		super.between(getterFn, startValue, endValue);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> between(boolean accept, SerializableFunction<T, ?> getterFn, Object startValue,
			Object endValue) {
		super.between(accept, getterFn, startValue, endValue);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> isNull(SerializableFunction<T, ?> getterFn) {
		super.isNull(getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> notNull(SerializableFunction<T, ?> getterFn) {
		super.notNull(getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> in(SerializableFunction<T, ?> getterFn) {
		super.in(getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> in(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.in(accept, getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> notIn(SerializableFunction<T, ?> getterFn) {
		super.notIn(getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> notIn(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.notIn(accept, getterFn);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> eq(SerializableFunction<T, ?> getterFn, Object value) {
		super.eq(getterFn, value);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> eq(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.eq(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> neq(SerializableFunction<T, ?> getterFn, Object value) {
		super.neq(getterFn, value);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> neq(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.neq(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> lt(SerializableFunction<T, ?> getterFn, Object value) {
		super.lt(getterFn, value);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> lt(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.lt(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> lte(SerializableFunction<T, ?> getterFn, Object value) {
		super.lte(getterFn, value);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> lte(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.lte(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> gt(SerializableFunction<T, ?> getterFn, Object value) {
		super.gt(getterFn, value);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> gt(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.gt(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> gte(SerializableFunction<T, ?> getterFn, Object value) {
		super.gte(getterFn, value);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> gte(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.gte(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> like(SerializableFunction<T, ?> getterFn, Object value) {
		super.like(getterFn, value);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> like(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.like(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> notLike(SerializableFunction<T, ?> getterFn, Object value) {
		super.notLike(getterFn, value);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> notLike(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.notLike(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> contain(SerializableFunction<T, ?> getterFn, Object value) {
		super.contain(getterFn, value);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> contain(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.contain(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> notContain(SerializableFunction<T, ?> getterFn, Object value) {
		super.notContain(getterFn, value);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> notContain(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.notContain(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> startWith(SerializableFunction<T, ?> getterFn, Object value) {
		super.startWith(getterFn, value);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> startWith(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.startWith(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> endWith(SerializableFunction<T, ?> getterFn, Object value) {
		super.endWith(getterFn, value);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> endWith(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.endWith(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> in(SerializableFunction<T, ?> getterFn, Object value) {
		super.in(getterFn, value);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> in(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.in(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> notIn(SerializableFunction<T, ?> getterFn, Object value) {
		super.notIn(getterFn, value);
		return this;
	}

	@Override
	public LambdaQueryPredicate<T> notIn(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.notIn(accept, getterFn, value);
		return this;
	}
	
	@Override
	public LambdaQueryPredicate<T> regex(SerializableFunction<T, ?> getterFn, Object value) {
		super.regex(getterFn, value);
		return this;
	}
	
	@Override
	public LambdaQueryPredicate<T> regex(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.regex(accept, getterFn, value);
		return this;
	}
}
