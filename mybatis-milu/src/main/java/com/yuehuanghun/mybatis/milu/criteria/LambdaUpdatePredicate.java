package com.yuehuanghun.mybatis.milu.criteria;

import java.util.function.Consumer;

import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.criteria.lambda.SerializableFunction;

public interface LambdaUpdatePredicate<T> extends LambdaPredicate<T> {
	/**
	 * 设置默认的条件生效模式，默认为Mode.NOT_NULL
	 * @param updateMode 更新字段生效模式
	 * @return 当前对象
	 */
	LambdaUpdatePredicate<T> updateMode(Mode updateMode);
	
	/**
	 * 返回更新模式
	 * @return 更新模式
	 */
	Mode getUpdateMode();
	
	@Override
	LambdaUpdatePredicate<T> apply(T entity);
	
	@Override
	LambdaUpdatePredicate<T> conditionMode(Mode conditionMode);
	
	@Override
	LambdaUpdatePredicate<T> and(Condition... conditions);
	
	@Override
	LambdaUpdatePredicate<T> and(Consumer<LambdaPredicate<T>> predicate);

	@Override
	LambdaUpdatePredicate<T> or(Condition... conditions);
	
	@Override
	LambdaUpdatePredicate<T> or(Consumer<LambdaPredicate<T>> predicate);

	@Override
	LambdaUpdatePredicate<T> not(Condition... conditions);
	
	@Override
	LambdaUpdatePredicate<T> not(Consumer<LambdaPredicate<T>> predicate);

	@Override
	LambdaUpdatePredicate<T> andP(Consumer<Predicate> predicate);
	
	@Override
	LambdaUpdatePredicate<T> orP(Consumer<Predicate> predicate);
	
	@Override
	LambdaUpdatePredicate<T> notP(Consumer<Predicate> predicate);

	@Override
	LambdaUpdatePredicate<T> eq(SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> eq(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> neq(SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> neq(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> lt(SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> lt(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> lte(SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> lte(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> gt(SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> gt(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> gte(SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> gte(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> like(SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> like(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> notLike(SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> notLike(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> contain(SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> contain(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> notContain(SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> notContain(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> startWith(SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> startWith(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> endWith(SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> endWith(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> between(SerializableFunction<T, ?> fn, Object startValue, Object endValue);

	@Override
	LambdaUpdatePredicate<T> between(boolean accept, SerializableFunction<T, ?> fn, Object startValue,
			Object endValue);

	@Override
	LambdaUpdatePredicate<T> isNull(SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> isNull(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> notNull(SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> notNull(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> in(SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> in(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> notIn(SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> notIn(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaUpdatePredicate<T> eq(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> eq(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> neq(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> neq(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> lt(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> lt(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> lte(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> lte(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> gt(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> gt(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> gte(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> gte(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> like(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> like(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> notLike(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> notLike(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> contain(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> contain(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> notContain(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> notContain(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> startWith(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> startWith(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> endWith(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> endWith(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> in(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> in(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> notIn(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> notIn(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> regex(SerializableFunction<T, ?> getterFn, Object value);
	
	@Override
	LambdaUpdatePredicate<T> regex(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaUpdatePredicate<T> undeleted();
	
	@Override
	LambdaUpdatePredicate<T> deleted();

	@Override
	LambdaUpdatePredicate<T> exists(Exists<?> exists);

	@Override
	LambdaUpdatePredicate<T> notExists(Exists<?> exists);
}
