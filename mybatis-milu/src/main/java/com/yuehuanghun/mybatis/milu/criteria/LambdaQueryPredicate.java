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
import com.yuehuanghun.mybatis.milu.criteria.lambda.SerializableFunction;
import com.yuehuanghun.mybatis.milu.data.Sort.Direction;

public interface LambdaQueryPredicate<T> extends LambdaPredicate<T> {
	/**
	 * 指定查询的（实体类）属性，未指定时查询实体所有属性
	 * @param getterFns 实体类的getter函数式
	 * @return 当前对象
	 */
	@SuppressWarnings("unchecked")
	LambdaQueryPredicate<T> select(SerializableFunction<T, ?>... getterFns);
	
	/**
	 * 指定查询的（实体类）属性，未指定时查询实体所有属性
	 * @param attrNameChain 多个属性名用英文逗号隔开，举例：name, age, createTime
	 * @return 当前对象
	 */
	LambdaQueryPredicate<T> selects(String attrNameChain);
	
	/**
	 * 排除查询指定的（实体类）属性，select比exselect优先级更高<br>
	 * 例如文本或二进制大字段，影响IO效率，在查询时排除掉
	 * @param getterFns 实体类的getter函数式
	 * @return 当前对象
	 */
	@SuppressWarnings("unchecked")
	LambdaQueryPredicate<T> exselect(SerializableFunction<T, ?>... getterFns);
	
	/**
	 * 排除查询指定的（实体类）属性，select比exselect优先级更高<br>
	 * 例如文本或二进制大字段，影响IO效率，在查询时排除掉
	 * @param attrNameChain 多个属性名用英文逗号隔开，举例：content, fileData
	 * @return 当前对象
	 */
	LambdaQueryPredicate<T> exselects(String attrNameChain);
	/**
	 * 添加排序，不指定排序方向，以数据库默认排序为准
	 * @param getterFns 实体类的getter函数式
	 * @return 当前对象
	 */
	@SuppressWarnings("unchecked")
	LambdaQueryPredicate<T> order(SerializableFunction<T, ?>... getterFns);
	
	/**
	 * 添加排序，指定排序方向
	 * @param direction 排序方向枚举
	 * @param getterFns 实体类的getter函数式
	 * @return 当前对象
	 */
	@SuppressWarnings("unchecked")
	LambdaQueryPredicate<T> order(Direction direction, SerializableFunction<T, ?>... getterFns);
	

	/**
	 * 获取第1页的pageSize条数据
	 * @param pageSize 每页行数
	 * @return 当前对象
	 */
	LambdaQueryPredicate<T> limit(int pageSize);
	
	/**
	 * 获取第1页的pageSize条数据
	 * @param pageSize 每页行数
	 * @param count 是否查询符合的总行数
	 * @return 当前对象
	 */
	LambdaQueryPredicate<T> limit(int pageSize, boolean count);
	
	/**
	 * 获取第pageNum页的pageSize条数据
	 * @param pageNum 起始页，从1开始
	 * @param pageSize 每页行数
	 * @return 当前对象
	 */
	LambdaQueryPredicate<T> limit(int pageNum, int pageSize);
	
	/**
	 * 
	 * 获取第pageNum页的pageSize条数据
	 * @param pageNum 起始页，从1开始
	 * @param pageSize 每页行数
	 * @param count 是否查询符合的总行数
	 * @return 当前对象
	 */
	LambdaQueryPredicate<T> limit(int pageNum, int pageSize, boolean count);

	@Override
	LambdaQueryPredicate<T> apply(T entity);

	@Override
	LambdaQueryPredicate<T> conditionMode(Mode conditionMode);
	
	@Override
	LambdaQueryPredicate<T> and(Condition... conditions);
	
	@Override
	LambdaQueryPredicate<T> and(Consumer<LambdaPredicate<T>> predicate);

	@Override
	LambdaQueryPredicate<T> or(Condition... conditions);
	
	@Override
	LambdaQueryPredicate<T> or(Consumer<LambdaPredicate<T>> predicate);

	@Override
	LambdaQueryPredicate<T> not(Condition... conditions);
	
	@Override
	LambdaQueryPredicate<T> not(Consumer<LambdaPredicate<T>> predicate);

	@Override
	LambdaQueryPredicate<T> eq(SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> eq(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> neq(SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> neq(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> lt(SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> lt(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> lte(SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> lte(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> gt(SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> gt(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> gte(SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> gte(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> like(SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> like(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> notLike(SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> notLike(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> contain(SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> contain(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> notContain(SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> notContain(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> startWith(SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> startWith(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> endWith(SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> endWith(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> between(SerializableFunction<T, ?> fn, Object startValue, Object endValue);

	@Override
	LambdaQueryPredicate<T> between(boolean accept, SerializableFunction<T, ?> fn, Object startValue,
			Object endValue);

	@Override
	LambdaQueryPredicate<T> isNull(SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> notNull(SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> in(SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> in(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> notIn(SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> notIn(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> eq(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> eq(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> neq(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> neq(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> lt(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> lt(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> lte(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> lte(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> gt(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> gt(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> gte(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> gte(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> like(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> like(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> notLike(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> notLike(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> contain(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> contain(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> notContain(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> notContain(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> startWith(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> startWith(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> endWith(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> endWith(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> in(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> in(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> notIn(SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> notIn(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	@Override
	LambdaQueryPredicate<T> regex(SerializableFunction<T, ?> getterFn, Object value);
	
	@Override
	LambdaQueryPredicate<T> regex(boolean accept, SerializableFunction<T, ?> getterFn, Object value);
}
