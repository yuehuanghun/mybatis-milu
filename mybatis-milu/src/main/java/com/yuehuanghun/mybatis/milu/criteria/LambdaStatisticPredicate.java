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

import java.util.function.Consumer;

import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.criteria.lambda.SerializableFunction;
import com.yuehuanghun.mybatis.milu.data.Sort.Direction;

public interface LambdaStatisticPredicate<T> extends LambdaPredicate<T> {
	
	/**
	 * 列汇总计算
	 * @param getterFn 实体类的getter函数式
	 * @return 当前对象
	 */
	LambdaStatisticPredicate<T> sum(SerializableFunction<T, ?> getterFn);
	
	/**
	 * 列汇总计算
	 * @param getterFn 实体类的getter函数式
	 * @param columnAlias 统计列别名
	 * @return 当前对象
	 */
	LambdaStatisticPredicate<T> sum(SerializableFunction<T, ?> getterFn, String columnAlias);

	/**
	 * 列计数
	 * @param getterFn 实体类的getter函数式
	 * @return 当前对象
	 */
	LambdaStatisticPredicate<T> count(SerializableFunction<T, ?> getterFn);
	
	/**
	 * 列计数
	 * @param getterFn 实体类的getter函数式
	 * @param columnAlias 统计列别名
	 * @return 当前对象
	 */
	LambdaStatisticPredicate<T> count(SerializableFunction<T, ?> getterFn, String columnAlias);

	/**
	 * 列平均值计算
	 * @param getterFn 实体类的getter函数式
	 * @return 当前对象
	 */
	LambdaStatisticPredicate<T> avg(SerializableFunction<T, ?> getterFn);
	
	/**
	 * 列平均值计算
	 * @param getterFn 实体类的getter函数式
	 * @param columnAlias 统计列别名
	 * @return 当前对象
	 */
	LambdaStatisticPredicate<T> avg(SerializableFunction<T, ?> getterFn, String columnAlias);

	/**
	 * 列最小值计算
	 * @param getterFn 实体类的getter函数式
	 * @return 当前对象
	 */
	LambdaStatisticPredicate<T> min(SerializableFunction<T, ?> getterFn);
	
	/**
	 * 列最小值计算
	 * @param getterFn 实体类的getter函数式
	 * @param columnAlias 统计列别名
	 * @return 当前对象
	 */
	LambdaStatisticPredicate<T> min(SerializableFunction<T, ?> getterFn, String columnAlias);

	/**
	 * 列最大值计算
	 * @param getterFn 实体类的getter函数式
	 * @return 当前对象
	 */
	LambdaStatisticPredicate<T> max(SerializableFunction<T, ?> getterFn);
	
	/**
	 * 列最大值计算
	 * @param getterFn 实体类的getter函数式
	 * @param columnAlias 统计列别名
	 * @return 当前对象
	 */
	LambdaStatisticPredicate<T> max(SerializableFunction<T, ?> getterFn, String columnAlias);

	/**
	 * 分组
	 * @param getterFns 实体类的getter函数式
	 * @return 当前对象
	 */
	@SuppressWarnings("unchecked")
	LambdaStatisticPredicate<T> groupBy(SerializableFunction<T, ?>... getterFns);
	
	/**
	 * 添加排序，不指定排序方向，以数据库默认排序为准
	 * @param getterFns 实体类的getter函数式
	 * @return 当前对象
	 */
	@SuppressWarnings("unchecked")
	LambdaStatisticPredicate<T> order(SerializableFunction<T, ?>... getterFns);
	
	/**
	 * 添加排序，指定排序方向
	 * @param direction 排序方向枚举
	 * @param getterFns 实体类的getter函数式
	 * @return 当前对象
	 */
	@SuppressWarnings("unchecked")
	LambdaStatisticPredicate<T> order(Direction direction, SerializableFunction<T, ?>... getterFns);

	/**
	 * 添加升序排序
	 * @param getterFns 实体类的getter函数式
	 * @return 当前对象
	 */
	@SuppressWarnings("unchecked")
	LambdaStatisticPredicate<T> orderAsc(SerializableFunction<T, ?>... getterFns);
	
	/**
	 * 添加降序排序
	 * @param getterFns 实体类的getter函数式
	 * @return 当前对象
	 */
	@SuppressWarnings("unchecked")
	LambdaStatisticPredicate<T> orderDesc(SerializableFunction<T, ?>... getterFns);

	/**
	 * 获取第1页的pageSize条数据
	 * @param pageSize 每页行数
	 * @return 当前对象
	 */
	LambdaStatisticPredicate<T> limit(int pageSize);
	
	/**
	 * 获取第1页的pageSize条数据
	 * @param pageSize 每页行数
	 * @param count 是否查询符合的总行数
	 * @return 当前对象
	 */
	LambdaStatisticPredicate<T> limit(int pageSize, boolean count);
	
	/**
	 * 获取第pageNum页的pageSize条数据
	 * @param pageNum 起始页，从1开始
	 * @param pageSize 每页行数
	 * @return 当前对象
	 */
	LambdaStatisticPredicate<T> limit(int pageNum, int pageSize);
	
	/**
	 * 
	 * 获取第pageNum页的pageSize条数据
	 * @param pageNum 起始页，从1开始
	 * @param pageSize 每页行数
	 * @param count 是否查询符合的总行数
	 * @return 当前对象
	 */
	LambdaStatisticPredicate<T> limit(int pageNum, int pageSize, boolean count);

	@Override
	LambdaStatisticPredicate<T> apply(T entity);

	@Override
	LambdaStatisticPredicate<T> conditionMode(Mode conditionMode);
	
	@Override
	LambdaStatisticPredicate<T> and(Condition... conditions);
	
	@Override
	LambdaStatisticPredicate<T> and(Consumer<LambdaPredicate<T>> predicate);

	@Override
	LambdaStatisticPredicate<T> or(Condition... conditions);
	
	@Override
	LambdaStatisticPredicate<T> or(Consumer<LambdaPredicate<T>> predicate);

	@Override
	LambdaStatisticPredicate<T> not(Condition... conditions);
	
	@Override
	LambdaStatisticPredicate<T> not(Consumer<LambdaPredicate<T>> predicate);

	@Override
	LambdaStatisticPredicate<T> andP(Consumer<Predicate> predicate);
	
	@Override
	LambdaStatisticPredicate<T> orP(Consumer<Predicate> predicate);
	
	@Override
	LambdaStatisticPredicate<T> notP(Consumer<Predicate> predicate);

	@Override
	LambdaStatisticPredicate<T> eq(SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> eq(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> neq(SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> neq(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> lt(SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> lt(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> lte(SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> lte(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> gt(SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> gt(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> gte(SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> gte(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> like(SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> like(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> notLike(SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> notLike(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> contain(SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> contain(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> notContain(SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> notContain(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> startWith(SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> startWith(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> endWith(SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> endWith(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> between(SerializableFunction<T, ?> fn, Object startValue, Object endValue);

	@Override
	LambdaStatisticPredicate<T> between(boolean accept, SerializableFunction<T, ?> fn, Object startValue,
			Object endValue);

	@Override
	LambdaStatisticPredicate<T> isNull(SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> isNull(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> notNull(SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> notNull(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> in(SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> in(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> notIn(SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> notIn(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaStatisticPredicate<T> eq(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> eq(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> neq(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> neq(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> lt(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> lt(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> lte(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> lte(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> gt(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> gt(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> gte(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> gte(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> like(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> like(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> notLike(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> notLike(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> contain(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> contain(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> notContain(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> notContain(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> startWith(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> startWith(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> endWith(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> endWith(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> in(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> in(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> notIn(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> notIn(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> regex(SerializableFunction<T, ?> getterFn, Object value);
	
	@Override
	LambdaStatisticPredicate<T> regex(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaStatisticPredicate<T> undeleted();
	
	@Override
	LambdaStatisticPredicate<T> deleted();

}
