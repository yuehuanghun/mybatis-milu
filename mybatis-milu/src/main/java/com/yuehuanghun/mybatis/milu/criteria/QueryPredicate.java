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

import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.data.Sort.Direction;

public interface QueryPredicate extends Predicate {
	
	/**
	 * 指定查询的（实体类）属性，未指定时查询实体所有属性
	 * @param attrNames 需要查询的实体属性
	 * @return
	 */
	QueryPredicate select(String... attrNames);
	
	/**
	 * 指定查询的（实体类）属性，未指定时查询实体所有属性
	 * @param attrNameChain 多个属性名用英文逗号隔开，举例：name, age, createTime
	 * @return
	 */
	QueryPredicate selects(String attrNameChain);
	
	/**
	 * 排除查询指定的（实体类）属性，select比exselect优先级更高<br>
	 * 例如文本或二进制大字段，影响IO效率，在查询时排除掉
	 * @param attrNames 需要查询的实体属性
	 * @return
	 */
	QueryPredicate exselect(String... attrNames);
	
	/**
	 * 排除查询指定的（实体类）属性，select比exselect优先级更高<br>
	 * 例如文本或二进制大字段，影响IO效率，在查询时排除掉
	 * @param attrNameChain 多个属性名用英文逗号隔开，举例：content, fileData
	 * @return
	 */
	QueryPredicate exselects(String attrNameChain);

	/**
	 * 添加排序，不指定排序方向，以数据库默认排序为准
	 * @param attrNames 属性名
	 * @return
	 */
	QueryPredicate order(String... attrNames);
	
	/**
	 * 添加排序，指定排序方向
	 * @param direction 排序方向枚举
	 * @param attrNames 属性名
	 * @return
	 */
	QueryPredicate order(Direction direction, String... attrNames);

	/**
	 * 获取第1页的pageSize条数据
	 * @param pageSize 每页行数
	 * @return
	 */
	QueryPredicate limit(int pageSize);
	
	/**
	 * 获取第1页的pageSize条数据
	 * @param pageSize 每页行数
	 * @param count 是否查询符合的总行数
	 * @return
	 */
	QueryPredicate limit(int pageSize, boolean count);
	
	/**
	 * 获取第pageNum页的pageSize条数据
	 * @param pageNum 起始页，从1开始
	 * @param pageSize 每页行数
	 * @return
	 */
	QueryPredicate limit(int pageNum, int pageSize);
	
	/**
	 * 
	 * 获取第pageNum页的pageSize条数据
	 * @param pageNum 起始页，从1开始
	 * @param pageSize 每页行数
	 * @param count 是否查询符合的总行数
	 * @return
	 */
	QueryPredicate limit(int pageNum, int pageSize, boolean count);
	
	@Override
	QueryPredicate conditionMode(Mode conditionMode);

	@Override
	QueryPredicate and(Condition... conditions);

	@Override
	QueryPredicate or(Condition... conditions);

	@Override
	QueryPredicate not(Condition... conditions);

	@Override
	QueryPredicate eq(String attrName, Object value);

	@Override
	QueryPredicate neq(String attrName, Object value);

	@Override
	QueryPredicate lt(String attrName, Object value);

	@Override
	QueryPredicate lte(String attrName, Object value);

	@Override
	QueryPredicate gt(String attrName, Object value);

	@Override
	QueryPredicate gte(String attrName, Object value);

	@Override
	QueryPredicate like(String attrName, Object value);

	@Override
	QueryPredicate notLike(String attrName, Object value);

	@Override
	QueryPredicate contain(String attrName, Object value);

	@Override
	QueryPredicate notContain(String attrName, Object value);

	@Override
	QueryPredicate startWith(String attrName, Object value);

	@Override
	QueryPredicate endWith(String attrName, Object value);

	@Override
	QueryPredicate between(String attrName, Object startValue, Object endValue);

	@Override
	QueryPredicate between(boolean accept, String attrName, Object startValue, Object endValue);

	@Override
	QueryPredicate isNull(String attrName);

	@Override
	QueryPredicate notNull(String attrName);

	@Override
	QueryPredicate in(String attrName, Object value);

	@Override
	QueryPredicate notIn(String attrName, Object value);

	@Override
	QueryPredicate eq(boolean accept, String attrName, Object value);

	@Override
	QueryPredicate neq(boolean accept, String attrName, Object value);

	@Override
	QueryPredicate lt(boolean accept, String attrName, Object value);

	@Override
	QueryPredicate lte(boolean accept, String attrName, Object value);

	@Override
	QueryPredicate gt(boolean accept, String attrName, Object value);

	@Override
	QueryPredicate gte(boolean accept, String attrName, Object value);

	@Override
	QueryPredicate like(boolean accept, String attrName, Object value);

	@Override
	QueryPredicate notLike(boolean accept, String attrName, Object value);

	@Override
	QueryPredicate contain(boolean accept, String attrName, Object value);

	@Override
	QueryPredicate notContain(boolean accept, String attrName, Object value);

	@Override
	QueryPredicate startWith(boolean accept, String attrName, Object value);

	@Override
	QueryPredicate endWith(boolean accept, String attrName, Object value);

	@Override
	QueryPredicate in(boolean accept, String attrName, Object value);

	@Override
	QueryPredicate notIn(boolean accept, String attrName, Object value);
}
