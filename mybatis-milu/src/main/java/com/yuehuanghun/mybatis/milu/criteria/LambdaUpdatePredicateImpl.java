package com.yuehuanghun.mybatis.milu.criteria;

import java.util.function.Consumer;

import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.criteria.lambda.SerializableFunction;

import lombok.Getter;

public class LambdaUpdatePredicateImpl<T> extends LambdaPredicateImpl<T> implements LambdaUpdatePredicate<T> {

	@Getter
	private UpdatePredicateImpl delegate = new UpdatePredicateImpl();
	
	@Override
	public LambdaUpdatePredicate<T> updateMode(Mode updateMode) {
		getDelegate().updateMode(updateMode);
		return this;
	}

	@Override
	public Mode getUpdateMode() {
		return getDelegate().getUpdateMode();
	}
	
	@Override
	public LambdaUpdatePredicate<T> apply(T entity) {
		super.apply(entity);
		return this;
	}
	
	@Override
	public LambdaUpdatePredicate<T> conditionMode(Mode conditionMode) {
		super.conditionMode(conditionMode);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> and(Condition... conditions) {
		super.and(conditions);
		return this;
	}
	
	@Override
	public LambdaUpdatePredicate<T> and(Consumer<LambdaPredicate<T>> predicate) {
		super.and(predicate);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> or(Condition... conditions) {
		super.or(conditions);
		return this;
	}
	
	@Override
	public LambdaUpdatePredicate<T> or(Consumer<LambdaPredicate<T>> predicate) {
		super.or(predicate);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> not(Condition... conditions) {
		super.not(conditions);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> not(Consumer<LambdaPredicate<T>> predicate) {
		super.not(predicate);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> eq(SerializableFunction<T, ?> getterFn) {
		super.eq(getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> eq(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.eq(accept, getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> neq(SerializableFunction<T, ?> getterFn) {
		super.neq(getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> neq(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.neq(accept, getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> lt(SerializableFunction<T, ?> getterFn) {
		super.lt(getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> lt(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.lt(accept, getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> lte(SerializableFunction<T, ?> getterFn) {
		super.lte(getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> lte(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.lte(accept, getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> gt(SerializableFunction<T, ?> getterFn) {
		super.gt(getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> gt(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.gt(accept, getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> gte(SerializableFunction<T, ?> getterFn) {
		super.gte(getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> gte(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.gte(accept, getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> like(SerializableFunction<T, ?> getterFn) {
		super.like(getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> like(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.like(accept, getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> notLike(SerializableFunction<T, ?> getterFn) {
		super.notLike(getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> notLike(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.notLike(accept, getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> contain(SerializableFunction<T, ?> getterFn) {
		super.contain(getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> contain(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.contain(accept, getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> notContain(SerializableFunction<T, ?> getterFn) {
		super.notContain(getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> notContain(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.notContain(accept, getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> startWith(SerializableFunction<T, ?> getterFn) {
		super.startWith(getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> startWith(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.startWith(accept, getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> endWith(SerializableFunction<T, ?> getterFn) {
		super.endWith(getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> endWith(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.endWith(accept, getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> between(SerializableFunction<T, ?> getterFn, Object startValue, Object endValue) {
		super.between(getterFn, startValue, endValue);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> between(boolean accept, SerializableFunction<T, ?> getterFn, Object startValue,
			Object endValue) {
		super.between(accept, getterFn, startValue, endValue);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> isNull(SerializableFunction<T, ?> getterFn) {
		super.isNull(getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> notNull(SerializableFunction<T, ?> getterFn) {
		super.notNull(getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> in(SerializableFunction<T, ?> getterFn) {
		super.in(getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> in(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.in(accept, getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> notIn(SerializableFunction<T, ?> getterFn) {
		super.notIn(getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> notIn(boolean accept, SerializableFunction<T, ?> getterFn) {
		super.notIn(accept, getterFn);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> eq(SerializableFunction<T, ?> getterFn, Object value) {
		super.eq(getterFn, value);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> eq(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.eq(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> neq(SerializableFunction<T, ?> getterFn, Object value) {
		super.neq(getterFn, value);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> neq(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.neq(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> lt(SerializableFunction<T, ?> getterFn, Object value) {
		super.lt(getterFn, value);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> lt(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.lt(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> lte(SerializableFunction<T, ?> getterFn, Object value) {
		super.lte(getterFn, value);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> lte(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.lte(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> gt(SerializableFunction<T, ?> getterFn, Object value) {
		super.gt(getterFn, value);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> gt(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.gt(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> gte(SerializableFunction<T, ?> getterFn, Object value) {
		super.gte(getterFn, value);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> gte(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.gte(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> like(SerializableFunction<T, ?> getterFn, Object value) {
		super.like(getterFn, value);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> like(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.like(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> notLike(SerializableFunction<T, ?> getterFn, Object value) {
		super.notLike(getterFn, value);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> notLike(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.notLike(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> contain(SerializableFunction<T, ?> getterFn, Object value) {
		super.contain(getterFn, value);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> contain(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.contain(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> notContain(SerializableFunction<T, ?> getterFn, Object value) {
		super.notContain(getterFn, value);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> notContain(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.notContain(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> startWith(SerializableFunction<T, ?> getterFn, Object value) {
		super.startWith(getterFn, value);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> startWith(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.startWith(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> endWith(SerializableFunction<T, ?> getterFn, Object value) {
		super.endWith(getterFn, value);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> endWith(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.endWith(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> in(SerializableFunction<T, ?> getterFn, Object value) {
		super.in(getterFn, value);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> in(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.in(accept, getterFn, value);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> notIn(SerializableFunction<T, ?> getterFn, Object value) {
		super.notIn(getterFn, value);
		return this;
	}

	@Override
	public LambdaUpdatePredicate<T> notIn(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.notIn(accept, getterFn, value);
		return this;
	}
	
	@Override
	public LambdaUpdatePredicate<T> regex(SerializableFunction<T, ?> getterFn, Object value) {
		super.regex(getterFn, value);
		return this;
	}
	
	@Override
	public LambdaUpdatePredicate<T> regex(boolean accept, SerializableFunction<T, ?> getterFn, Object value) {
		super.regex(accept, getterFn, value);
		return this;
	}

}
