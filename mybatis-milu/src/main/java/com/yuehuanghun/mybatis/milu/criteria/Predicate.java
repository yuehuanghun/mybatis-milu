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

/**
 * 查询逻辑条件
 * @author yuehuanghun
 *
 */
public interface Predicate extends Condition {

	public static enum Logic {
		AND, OR, NOT
	}
	
	/**
	 * 设置默认的条件生效模式，默认为Mode.NOT_EMPTY
	 * @param mode
	 */
	Predicate conditionMode(Mode conditionMode);
	
	/**
	 * 与一组查询条件：AND (condition1 AND condition2 [AND conditionN])
	 * @param conditions
	 * @return
	 */
	Predicate and(Condition... conditions);
	
	/**
	 * 给定一个Predicate进行设置
	 * @param predicate
	 * @return
	 */
	Predicate and(Consumer<Predicate> predicate);

	/**
	 * 或一组查询：OR (condition1 AND condition2 [AND conditionN])
	 * @param conditions
	 * @return
	 */
	Predicate or(Condition... conditions);
	
	/**
	 * 给定一个Predicate进行设置
	 * @param predicate
	 * @return
	 */
	Predicate or(Consumer<Predicate> predicate);
	
	/**
	 * 否一组查询：NOT (condition1 AND condition2 [AND conditionN])
	 * @param conditions
	 * @return
	 */
	Predicate not(Condition... conditions);
	
	/**
	 * 给定一个Predicate进行设置
	 * @param predicate
	 * @return
	 */
	Predicate not(Consumer<Predicate> predicate);

	/**
	 * 增加一个值相等查询条件，当value != null，条件生效
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return
	 */
	Predicate eq(String attrName, Object value);
	
	/**
	 * 增加一个值相等查询条件
	 * @param accept 当值为true时，条件生效
	 * @param attrName 查询属性名
	 * @param value值
	 * @return
	 */
	Predicate eq(boolean accept, String attrName, Object value);
	
	/**
	 * 增加一个值不等查询条件，当value != null，条件生效
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return
	 */
	Predicate neq(String attrName, Object value);
	
	/**
	 * 增加一个值不等查询条件
	 * @param accept 当值为true时，条件生效
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return
	 */
	Predicate neq(boolean accept, String attrName, Object value);

	/**
	 * 增加一个值小于查询条件，当value != null，条件生效
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return
	 */
	Predicate lt(String attrName, Object value);
	
	/**
	 * 增加一个值小于查询条件
	 * @param accept 当值为true时，条件生效
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return
	 */
	Predicate lt(boolean accept, String attrName, Object value);

	/**
	 * 增加一个值小于等于查询条件，当value != null，条件生效
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return
	 */
	Predicate lte(String attrName, Object value);
	
	/**
	 * 增加一个值小于等于查询条件
	 * @param accept 当值为true时，条件生效
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return
	 */
	Predicate lte(boolean accept, String attrName, Object value);

	/**
	 * 增加一个值大于查询条件，当value != null，条件生效
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return
	 */
	Predicate gt(String attrName, Object value);
	
	/**
	 * 增加一个值大于查询条件
	 * @param accept 当值为true时，条件生效
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return
	 */
	Predicate gt(boolean accept, String attrName, Object value);

	/**
	 * 增加一个值大于等于查询条件，当value != null，条件生效
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return
	 */
	Predicate gte(String attrName, Object value);
	
	/**
	 * 增加一个值大于等于查询条件
	 * @param accept 当值为true时，条件生效
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return
	 */
	Predicate gte(boolean accept, String attrName, Object value);

	/**
	 * 增加一个匹配查询条件，注：该查询值不会自动加匹配符，当value != null，条件生效
	 * @param attrName
	 * @param value 值应包括匹配符
	 * @return
	 */
	Predicate like(String attrName, Object value);
	
	/**
	 * 增加一个匹配查询条件，注：该查询值不会自动加匹配符
	 * @param accept 当值为true时，条件生效
	 * @param attrName
	 * @param value 值应包括匹配符
	 * @return
	 */
	Predicate like(boolean accept, String attrName, Object value);

	/**
	 * 增加一个非匹配查询条件，注：该查询值不会自动加匹配符，当value != null，条件生效
	 * @param attrName
	 * @param value 值应包括匹配符
	 * @return
	 */
	Predicate notLike(String attrName, Object value);

	/**
	 * 增加一个非匹配查询条件，注：该查询值不会自动加匹配符
	 * @param accept 当值为true时，条件生效
	 * @param attrName
	 * @param value 值应包括匹配符
	 * @return
	 */
	Predicate notLike(boolean accept, String attrName, Object value);
	
	/**
	 * 增加一个包含值查询条件，在值前后增加%，column LIKE CONCAT('%', value, '%')，当value != null，条件生效
	 * @param attrName
	 * @param value 值
	 * @return
	 */
	Predicate contain(String attrName, Object value);
	
	/**
	 * 增加一个包含值查询条件，在值前后增加%，column LIKE CONCAT('%', value, '%')
	 * @param accept 当值为true时，条件生效
	 * @param attrName
	 * @param value 值
	 * @return
	 */
	Predicate contain(boolean accept, String attrName, Object value);

	/**
	 * 增加一个非包含值查询条件，在值前后增加%，column NOT LIKE CONCAT('%', value, '%')，当value != null，条件生效
	 * @param attrName
	 * @param value 值
	 * @return
	 */
	Predicate notContain(String attrName, Object value);
	
	/**
	 * 增加一个非包含值查询条件，在值前后增加%，column NOT LIKE CONCAT('%', value, '%')
	 * @param accept 当值为true时，条件生效
	 * @param attrName
	 * @param value 值
	 * @return
	 */
	Predicate notContain(boolean accept, String attrName, Object value);

	/**
	 * 增加一个左匹配查询条件，在值后面增加%，column LIKE CONCAT(value, '%')，当value != null，条件生效
	 * @param attrName
	 * @param value 值
	 * @return
	 */
	Predicate startWith(String attrName, Object value);

	/**
	 * 增加一个左匹配查询条件，在值后面增加%，column LIKE CONCAT(value, '%')
	 * @param accept 当值为true时，条件生效
	 * @param attrName
	 * @param value 值
	 * @return
	 */
	Predicate startWith(boolean accept, String attrName, Object value);
	
	/**
	 * 增加一个右匹配查询条件，在值前面增加%，column LIKE CONCAT('%', value)，当value != null，条件生效
	 * @param attrName
	 * @param value 值
	 * @return
	 */
	Predicate endWith(String attrName, Object value);
	
	/**
	 * 增加一个右匹配查询条件，在值前面增加%，column LIKE CONCAT('%', value)
	 * @param accept 当值为true时，条件生效
	 * @param attrName
	 * @param value 值
	 * @return
	 */
	Predicate endWith(boolean accept, String attrName, Object value);

	/**
	 * 增加一个范围匹配查询条件， column BETWEEN startValue AND endValue
	 * @param attrName
	 * @param startValue 开始值
	 * @param endValue 结束值 
	 * @return
	 */
	Predicate between(String attrName, Object startValue, Object endValue);
	
	/**
	 * 增加一个范围匹配查询条件， column BETWEEN startValue AND endValue
	 * @param accept 当值为true时，条件生效
	 * @param attrName
	 * @param startValue 开始值
	 * @param endValue 结束值 
	 * @return
	 */
	Predicate between(boolean accept, String attrName, Object startValue, Object endValue);

	/**
	 * 增加一个值非空查询条件
	 * @param attrName
	 * @return
	 */
	Predicate isNull(String attrName);

	/**
	 * 增加一个值为空查询条件
	 * @param attrName
	 * @return
	 */
	Predicate notNull(String attrName);
	
	/**
	 * 增加一个IN查询条件，column IN (value[0],value[1]...)，当value != null并且非空集合时，条件生效
	 * @param attrName
	 * @param value 值
	 * @return
	 */
	Predicate in(String attrName, Object value);
	
	/**
	 * 增加一个IN查询条件，column IN (value[0],value[1]...)
	 * @param accept 当值为true时，条件生效
	 * @param attrName
	 * @param value 值
	 * @return
	 */
	Predicate in(boolean accept, String attrName, Object value);
	
	/**
	 * 增加一个NOT IN查询条件，column NOT IN (value[0],value[1]...)
	 * @param attrName
	 * @param value 值
	 * @return
	 */
	Predicate notIn(String attrName, Object value);
	
	/**
	 * 增加一个NOT IN查询条件，column NOT IN (value[0],value[1]...)
	 * @param accept 当值为true时，条件生效
	 * @param attrName
	 * @param value 值
	 * @return
	 */
	Predicate notIn(boolean accept, String attrName, Object value);
}
