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

import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.data.Sort.Direction;

/**
 * 
 * @see Predicates
 *
 */
public interface StatisticPredicate extends Predicate {
	
	/**
	 * 列汇总计算<br>
	 * 查询字段名为被统计属性名+统计函数，例如：sum("amount") -> amountSum
	 * @param attrName 属性名
	 * @return 当前对象
	 */
	StatisticPredicate sum(String attrName);
	
	/**
	 * 列汇总计算
	 * @param attrName 属性名
	 * @param columnAlias 统计列别名
	 * @return 当前对象
	 */
	StatisticPredicate sum(String attrName, String columnAlias);

	/**
	 * 列计数<br>
	 * 查询字段名为被统计属性名+统计函数，例如：count("id") -> idCount
	 * @param attrName 属性名
	 * @return 当前对象
	 */
	StatisticPredicate count(String attrName);
	
	/**
	 * 列计数
	 * @param attrName 属性名
	 * @param columnAlias 统计列别名
	 * @return 当前对象
	 */
	StatisticPredicate count(String attrName, String columnAlias);

	/**
	 * 列去重后计数<br>
	 * 查询字段名为被统计属性名+统计函数，例如：countDistinct("status") -> statusCountDistinct
	 * @param attrName 属性名
	 * @return 当前对象
	 */
	StatisticPredicate countDistinct(String attrName);
	
	/**
	 * 列去重后计数
	 * @param attrName 属性名
	 * @param columnAlias 统计列别名
	 * @return 当前对象
	 */
	StatisticPredicate countDistinct(String attrName, String columnAlias);

	/**
	 * 列平均值计算<br>
	 * 查询字段名为被统计属性名+统计函数，例如：avg("amount") -> amountAvg
	 * @param attrName 属性名
	 * @return 当前对象
	 */
	StatisticPredicate avg(String attrName);
	
	/**
	 * 列平均值计算
	 * @param attrName 属性名
	 * @param columnAlias 统计列别名
	 * @return 当前对象
	 */
	StatisticPredicate avg(String attrName, String columnAlias);

	/**
	 * 列最小值计算<br>
	 * 查询字段名为被统计属性名+统计函数，例如：min("amount") -> amountMin
	 * @param attrName 属性名
	 * @return 当前对象
	 */
	StatisticPredicate min(String attrName);
	
	/**
	 * 列最小值计算
	 * @param attrName 属性名
	 * @param columnAlias 统计列别名
	 * @return 当前对象
	 */
	StatisticPredicate min(String attrName, String columnAlias);

	/**
	 * 列最大值计算<br>
	 * 查询字段名为被统计属性名+统计函数，例如：max("amount") -> amountMax
	 * @param attrName 属性名
	 * @return 当前对象
	 */
	StatisticPredicate max(String attrName);
	
	/**
	 * 列最大值计算
	 * @param attrName 属性名
	 * @param columnAlias 统计列别名
	 * @return 当前对象
	 */
	StatisticPredicate max(String attrName, String columnAlias);
	
	/**
	 * 添加排序，不指定排序方向，以数据库默认排序为准
	 * @param attrNames 属性名
	 * @return 当前对象
	 */
	StatisticPredicate order(String... attrNames);
	
	/**
	 * 添加排序，指定排序方向
	 * @param direction 排序方向枚举
	 * @param attrNames 属性名
	 * @return 当前对象
	 */
	StatisticPredicate order(Direction direction, String... attrNames);
	
	/**
	 * 添加升序排序
	 * @param attrNames 属性名
	 * @return 当前对象
	 */
	StatisticPredicate orderAsc(String... attrNames);
	
	/**
	 * 添加降序排序
	 * @param attrNames 属性名
	 * @return 当前对象
	 */
	StatisticPredicate orderDesc(String... attrNames);
	
	/**
	 * 分组
	 * @param attrNames 属性名
	 * @return 当前对象
	 */
	StatisticPredicate groupBy(String... attrNames);	

	/**
	 * 分组，并指定分组属性在查询字段中的别名
	 * @param attrName 属性名
	 * @param alias 别名
	 * @return 当前对象
	 */
	StatisticPredicate groupByAs(String attrName, String alias);

	/**
	 * 获取第1页的pageSize条数据
	 * @param pageSize 每页行数
	 * @return 当前对象
	 */
	StatisticPredicate limit(int pageSize);
	
	/**
	 * 获取第1页的pageSize条数据
	 * @param pageSize 每页行数
	 * @param count 是否查询符合的总行数
	 * @return 当前对象
	 */
	StatisticPredicate limit(int pageSize, boolean count);
	
	/**
	 * 获取第pageNum页的pageSize条数据
	 * @param pageNum 起始页，从1开始
	 * @param pageSize 每页行数
	 * @return 当前对象
	 */
	StatisticPredicate limit(int pageNum, int pageSize);
	
	/**
	 * 
	 * 获取第pageNum页的pageSize条数据
	 * @param pageNum 起始页，从1开始
	 * @param pageSize 每页行数
	 * @param count 是否查询符合的总行数
	 * @return 当前对象
	 */
	StatisticPredicate limit(int pageNum, int pageSize, boolean count);
	
	@Override
	StatisticPredicate conditionMode(Mode conditionMode);

	@Override
	StatisticPredicate and(Condition... conditions);

	@Override
	StatisticPredicate or(Condition... conditions);

	@Override
	StatisticPredicate not(Condition... conditions);

	@Override
	StatisticPredicate eq(String attrName, Object value);

	@Override
	StatisticPredicate neq(String attrName, Object value);

	@Override
	StatisticPredicate lt(String attrName, Object value);

	@Override
	StatisticPredicate lte(String attrName, Object value);

	@Override
	StatisticPredicate gt(String attrName, Object value);

	@Override
	StatisticPredicate gte(String attrName, Object value);

	@Override
	StatisticPredicate like(String attrName, Object value);

	@Override
	StatisticPredicate notLike(String attrName, Object value);

	@Override
	StatisticPredicate contain(String attrName, Object value);

	@Override
	StatisticPredicate notContain(String attrName, Object value);

	@Override
	StatisticPredicate startWith(String attrName, Object value);

	@Override
	StatisticPredicate endWith(String attrName, Object value);

	@Override
	StatisticPredicate between(String attrName, Object startValue, Object endValue);

	@Override
	StatisticPredicate between(boolean accept, String attrName, Object startValue, Object endValue);

	@Override
	StatisticPredicate isNull(String attrName);

	@Override
	StatisticPredicate isNull(boolean accept, String attrName);

	@Override
	StatisticPredicate notNull(String attrName);

	@Override
	StatisticPredicate notNull(boolean accept, String attrName);

	@Override
	StatisticPredicate in(String attrName, Object value);

	@Override
	StatisticPredicate notIn(String attrName, Object value);

	@Override
	StatisticPredicate eq(boolean accept, String attrName, Object value);

	@Override
	StatisticPredicate neq(boolean accept, String attrName, Object value);

	@Override
	StatisticPredicate lt(boolean accept, String attrName, Object value);

	@Override
	StatisticPredicate lte(boolean accept, String attrName, Object value);

	@Override
	StatisticPredicate gt(boolean accept, String attrName, Object value);

	@Override
	StatisticPredicate gte(boolean accept, String attrName, Object value);

	@Override
	StatisticPredicate like(boolean accept, String attrName, Object value);

	@Override
	StatisticPredicate notLike(boolean accept, String attrName, Object value);

	@Override
	StatisticPredicate contain(boolean accept, String attrName, Object value);

	@Override
	StatisticPredicate notContain(boolean accept, String attrName, Object value);

	@Override
	StatisticPredicate startWith(boolean accept, String attrName, Object value);

	@Override
	StatisticPredicate endWith(boolean accept, String attrName, Object value);

	@Override
	StatisticPredicate in(boolean accept, String attrName, Object value);

	@Override
	StatisticPredicate notIn(boolean accept, String attrName, Object value);

	@Override
	StatisticPredicate regex(String attrName, Object value);

	@Override
	StatisticPredicate regex(boolean accept, String attrName, Object value);

	@Override
	StatisticPredicate undeleted();
	
	@Override
	StatisticPredicate deleted();
}
