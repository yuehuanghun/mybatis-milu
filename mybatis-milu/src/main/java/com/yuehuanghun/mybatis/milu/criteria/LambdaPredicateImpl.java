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
package com.yuehuanghun.mybatis.milu.criteria;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.criteria.lambda.LambdaReflections;
import com.yuehuanghun.mybatis.milu.criteria.lambda.SerializableFunction;
import com.yuehuanghun.mybatis.milu.tool.Assert;

import lombok.Getter;

public class LambdaPredicateImpl<T> implements LambdaPredicate<T> {
	protected T entity;
	
	@Getter
	private PredicateImpl delegate = new PredicateImpl();

	@Override
	public int renderSqlTemplate(MiluConfiguration configuration, StringBuilder expressionBuilder, Set<String> columns,
			int paramIndex) {
		return getDelegate().renderSqlTemplate(configuration, expressionBuilder, columns, paramIndex);
	}

	@Override
	public int renderParams(Map<String, Object> params, int paramIndex) {
		return getDelegate().renderParams(params, paramIndex);
	}

	@Override
	public LambdaPredicate<T> apply(T entity) {
		Assert.notNull(entity, "实体对象不能为空");
		this.entity = entity;
		return this;
	}
	
	protected void assertEntityNotNull() {
		if(entity == null) {
			throw new IllegalStateException("参数实体对象为null，需要先使用apply方法设置参数实体对象");
		}
	}

	@Override
	public LambdaPredicate<T> conditionMode(Mode conditionMode) {
		getDelegate().conditionMode(conditionMode);
		return this;
	}

	@Override
	public LambdaPredicate<T> and(Condition... conditions) {
		getDelegate().and(conditions);
		return this;
	}

	@Override
	public LambdaPredicate<T> and(Consumer<LambdaPredicate<T>> predicate) {
		LambdaPredicateImpl<T> p = new LambdaPredicateImpl<>();
		if(entity != null) {
			p.apply(entity);
		}
		predicate.accept(p);
		if(!p.isEmpty()) {
			p.getDelegate().setDepth(this.getDelegate().getDepth() + 1);
			getDelegate().and(p);
		}
		return this;
	}

	@Override
	public LambdaPredicate<T> andP(Consumer<Predicate> predicate) {
		PredicateImpl p = new PredicateImpl();
		predicate.accept(p);
		if(!p.isEmpty()) {
			p.setDepth(this.getDelegate().getDepth() + 1);
			getDelegate().and(p);
		}
		return this;
	}

	@Override
	public LambdaPredicate<T> or(Condition... conditions) {
		getDelegate().or(conditions);
		return this;
	}

	@Override
	public LambdaPredicate<T> or(Consumer<LambdaPredicate<T>> predicate) {
		LambdaPredicateImpl<T> p = new LambdaPredicateImpl<>();
		if(entity != null) {
			p.apply(entity);
		}
		predicate.accept(p);
		if(!p.isEmpty()) {
			p.getDelegate().setDepth(this.getDelegate().getDepth() + 1);
			getDelegate().or(p);
		}
		return this;
	}

	@Override
	public LambdaPredicate<T> orP(Consumer<Predicate> predicate) {
		PredicateImpl p = new PredicateImpl();
		predicate.accept(p);
		if(!p.isEmpty()) {
			p.setDepth(this.getDelegate().getDepth() + 1);
			getDelegate().or(p);
		}
		return this;
	}
	
	@Override
	public LambdaPredicate<T> not(Condition... conditions) {
		getDelegate().not(conditions);
		return this;
	}

	@Override
	public LambdaPredicate<T> not(Consumer<LambdaPredicate<T>> predicate) {
		LambdaPredicateImpl<T> p = new LambdaPredicateImpl<>();
		if(entity != null) {
			p.apply(entity);
		}
		predicate.accept(p);
		if(!p.isEmpty()) {
			p.getDelegate().setDepth(this.getDelegate().getDepth() + 1);
			getDelegate().not(p);
		}
		return this;
	}

	@Override
	public LambdaPredicate<T> notP(Consumer<Predicate> predicate) {
		PredicateImpl p = new PredicateImpl();
		predicate.accept(p);
		if(!p.isEmpty()) {
			p.setDepth(this.getDelegate().getDepth() + 1);
			getDelegate().not(p);
		}
		return this;
	}

	@Override
	public LambdaPredicate<T> eq(SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().eq(LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> eq(boolean accept, SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().eq(accept, LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> neq(SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().neq(LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> neq(boolean accept, SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().neq(accept, LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> lt(SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().lt(LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> lt(boolean accept, SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().lt(accept, LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> lte(SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().lte(LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> lte(boolean accept, SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().lte(accept, LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> gt(SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().gt(LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> gt(boolean accept, SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().gt(accept, LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> gte(SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().gte(LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> gte(boolean accept, SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().gte(accept, LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> like(SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().like(LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> like(boolean accept, SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().like(accept, LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> notLike(SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().notLike(LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> notLike(boolean accept, SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().notLike(accept, LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> contain(SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().contain(LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> contain(boolean accept, SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().contain(accept, LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> notContain(SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().notContain(LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> notContain(boolean accept, SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().notContain(accept, LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> startWith(SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().startWith(LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> startWith(boolean accept, SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().startWith(accept, LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> endWith(SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().endWith(LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> endWith(boolean accept, SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().endWith(accept, LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> between(SerializableFunction<T, ?> getterFn, Object startValue, Object endValue) {
		getDelegate().between(LambdaReflections.fnToFieldName(getterFn), startValue, endValue);
		return this;
	}
	
	@Override
	public LambdaPredicate<T> between(boolean accept, SerializableFunction<T, ?> getterFn, Object startValue, Object endValue) {
		getDelegate().between(accept, LambdaReflections.fnToFieldName(getterFn), startValue, endValue);
		return this;
	}

	@Override
	public LambdaPredicate<T> isNull(SerializableFunction<T, ?> getterFn) {
		getDelegate().isNull(LambdaReflections.fnToFieldName(getterFn));
		return this;
	}

	@Override
	public LambdaPredicate<T> isNull(boolean accept, SerializableFunction<T, ?> getterFn) {
		getDelegate().isNull(accept, LambdaReflections.fnToFieldName(getterFn));
		return this;
	}

	@Override
	public LambdaPredicate<T> notNull(SerializableFunction<T, ?> getterFn) {
		getDelegate().notNull(LambdaReflections.fnToFieldName(getterFn));
		return this;
	}

	@Override
	public LambdaPredicate<T> notNull(boolean accept, SerializableFunction<T, ?> getterFn) {
		getDelegate().notNull(accept, LambdaReflections.fnToFieldName(getterFn));
		return this;
	}

	@Override
	public LambdaPredicate<T> in(SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().in(LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> in(boolean accept, SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().in(accept, LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> notIn(SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().notIn(LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> notIn(boolean accept, SerializableFunction<T, ?> getterFn) {
		assertEntityNotNull();
		getDelegate().notIn(accept, LambdaReflections.fnToFieldName(getterFn), getterFn.apply(entity));
		return this;
	}

	@Override
	public LambdaPredicate<T> eq(SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().eq(LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> eq(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().eq(accept, LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> neq(SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().neq(LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> neq(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().neq(accept, LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> lt(SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().lt(LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> lt(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().lt(accept, LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> lte(SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().lte(LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> lte(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().lte(accept, LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> gt(SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().gt(LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> gt(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().gt(accept, LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> gte(SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().gte(LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> gte(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().gte(accept, LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> like(SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().like(LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> like(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().like(accept, LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> notLike(SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().notLike(LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> notLike(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().notLike(accept, LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> contain(SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().contain(LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> contain(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().contain(accept, LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> notContain(SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().notContain(LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> notContain(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().notContain(accept, LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> startWith(SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().startWith(LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> startWith(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().startWith(accept, LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> endWith(SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().endWith(LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> endWith(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().endWith(accept, LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> in(SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().in(LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> in(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().in(accept, LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> notIn(SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().notIn(LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> notIn(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().notIn(accept, LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> regex(SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().regex(LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public LambdaPredicate<T> regex(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		getDelegate().regex(accept, LambdaReflections.fnToFieldName(getterFn), value);
		return this;
	}

	@Override
	public int hashCode() {
		return this.getClass().hashCode() + this.getDelegate().hashCode() * 31;
	}

	@Override
	public boolean equals(Object that) {
		if(!this.getClass().isInstance(that)) {
			return false;
		}
		return Objects.equals(this.getDelegate(), ((LambdaPredicateImpl<?>)that).getDelegate());
	}

	@Override
	public boolean isEmpty() {
		return getDelegate().isEmpty();
	}
}
