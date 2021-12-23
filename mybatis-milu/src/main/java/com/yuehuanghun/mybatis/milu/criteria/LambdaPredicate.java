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

/**
 * 使用实体类的getter函数式作为查询条件，如实体类属性（的get方法）有变更能通过IDE直接感知<br>
 * 有局限性，目前不能使用关联表的属性作为查询条件<br>
 * 举例：<br>
 * 当使用实体类接收查询参数：Foo foo = ...; fooMapper.findByLambdaCriteria(predicate -&gt; predicate.apply(foo).eq(Foo:getBar));<br>
 * 当使用非实体类接口查询数：FooDTO fooDto = ...; fooMapper.findByLambdaCriteria(predicate -&gt; predicate.eq(Foo:getBar), fooDto.getBar());<br>
 * @author yuehuanghun
 *
 * @param <T> 实体类
 */
public interface LambdaPredicate<T> extends Condition {
	/**
	 * 设置实体对象查询参数
	 * @param entity 实体类对象查询条件参数
	 * @return 当前对象
	 */
	LambdaPredicate<T> apply(T entity);

	/**
	 * 设置默认的条件生效模式，默认为Mode.NOT_EMPTY
	 * @param conditionMode 条件生效模式
	 * @return 当前对象
	 */
	LambdaPredicate<T> conditionMode(Mode conditionMode);
	
	/**
	 * 与一组查询条件：AND (condition1 AND condition2 [AND conditionN])
	 * @param conditions 条件
	 * @return 当前对象
	 */
	LambdaPredicate<T> and(Condition... conditions);

	/**
	 * 给定一个LambdaPredicate进行设置
	 * @param predicate 条件
	 * @return 当前对象
	 */
	LambdaPredicate<T> and(Consumer<LambdaPredicate<T>> predicate);

	/**
	 * 给定一个Predicate进行设置
	 * @param predicate 条件
	 * @return 当前对象
	 */
	LambdaPredicate<T> andP(Consumer<Predicate> predicate);
	
	/**
	 * 或一组查询：OR (condition1 AND condition2 [AND conditionN])
	 * @param conditions 条件
	 * @return 当前对象
	 */
	LambdaPredicate<T> or(Condition... conditions);

	/**
	 * 给定一个LambdaPredicate进行设置
	 * @param predicate 条件
	 * @return 当前对象
	 */
	LambdaPredicate<T> or(Consumer<LambdaPredicate<T>> predicate);

	/**
	 * 给定一个Predicate进行设置
	 * @param predicate 条件
	 * @return 当前对象
	 */
	LambdaPredicate<T> orP(Consumer<Predicate> predicate);
	
	/**
	 * 或一组查询：NOT (condition1 AND condition2 [AND conditionN])
	 * @param conditions 条件
	 * @return 当前对象
	 */
	LambdaPredicate<T> not(Condition... conditions);

	/**
	 * 给定一个LambdaPredicate进行设置
	 * @param predicate 条件
	 * @return 当前对象
	 */
	LambdaPredicate<T> not(Consumer<LambdaPredicate<T>> predicate);

	/**
	 * 给定一个Predicate进行设置
	 * @param predicate 条件
	 * @return 当前对象
	 */
	LambdaPredicate<T> notP(Consumer<Predicate> predicate);

	/**
	 * 增加一个值相等查询条件，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> eq(SerializableFunction<T, ?> getterFn);
	
	/**
	 * 增加一个值相等查询条件
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> eq(boolean accept, SerializableFunction<T, ?> getterFn);
	
	/**
	 * 增加一个值不等查询条件，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> neq(SerializableFunction<T, ?> getterFn);
	
	/**
	 * 增加一个值不等查询条件
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> neq(boolean accept, SerializableFunction<T, ?> getterFn);

	/**
	 * 增加一个值小于查询条件，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> lt(SerializableFunction<T, ?> getterFn);
	
	/**
	 * 增加一个值小于查询条件
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> lt(boolean accept, SerializableFunction<T, ?> getterFn);

	/**
	 * 增加一个值小于等于查询条件，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> lte(SerializableFunction<T, ?> getterFn);
	
	/**
	 * 增加一个值小于等于查询条件
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> lte(boolean accept, SerializableFunction<T, ?> getterFn);

	/**
	 * 增加一个值大于查询条件，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> gt(SerializableFunction<T, ?> getterFn);
	
	/**
	 * 增加一个值大于查询条件
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> gt(boolean accept, SerializableFunction<T, ?> getterFn);

	/**
	 * 增加一个值大于等于查询条件，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> gte(SerializableFunction<T, ?> getterFn);
	
	/**
	 * 增加一个值大于等于查询条件
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> gte(boolean accept, SerializableFunction<T, ?> getterFn);

	/**
	 * 增加一个匹配查询条件，注：该查询值不会自动加匹配符，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> like(SerializableFunction<T, ?> getterFn);
	
	/**
	 * 增加一个匹配查询条件，注：该查询值不会自动加匹配符
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> like(boolean accept, SerializableFunction<T, ?> getterFn);

	/**
	 * 增加一个非匹配查询条件，注：该查询值不会自动加匹配符，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> notLike(SerializableFunction<T, ?> getterFn);

	/**
	 * 增加一个非匹配查询条件，注：该查询值不会自动加匹配符
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> notLike(boolean accept, SerializableFunction<T, ?> getterFn);
	
	/**
	 * 增加一个包含值查询条件，在值前后增加%，column LIKE CONCAT('%', value, '%')，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> contain(SerializableFunction<T, ?> getterFn);
	
	/**
	 * 增加一个包含值查询条件，在值前后增加%，column LIKE CONCAT('%', value, '%')
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> contain(boolean accept, SerializableFunction<T, ?> getterFn);

	/**
	 * 增加一个非包含值查询条件，在值前后增加%，column NOT LIKE CONCAT('%', value, '%')，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> notContain(SerializableFunction<T, ?> getterFn);
	
	/**
	 * 增加一个非包含值查询条件，在值前后增加%，column NOT LIKE CONCAT('%', value, '%')
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> notContain(boolean accept, SerializableFunction<T, ?> getterFn);

	/**
	 * 增加一个左匹配查询条件，在值后面增加%，column LIKE CONCAT(value, '%')，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> startWith(SerializableFunction<T, ?> getterFn);

	/**
	 * 增加一个左匹配查询条件，在值后面增加%，column LIKE CONCAT(value, '%')
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> startWith(boolean accept, SerializableFunction<T, ?> getterFn);
	
	/**
	 * 增加一个右匹配查询条件，在值前面增加%，column LIKE CONCAT('%', value)，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> endWith(SerializableFunction<T, ?> getterFn);
	
	/**
	 * 增加一个右匹配查询条件，在值前面增加%，column LIKE CONCAT('%', value)
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> endWith(boolean accept, SerializableFunction<T, ?> getterFn);

	/**
	 * 增加一个范围匹配查询条件， column BETWEEN startValue AND endValue<br>
	 * startValue和endValue不允许为null，如果可能为null值，可使用gte、lte替代
	 * @param getterFn 实体类的get方法函数式
	 * @param startValue 开始值
	 * @param endValue 结束值 
	 * @return 当前对象
	 */
	LambdaPredicate<T> between(SerializableFunction<T, ?> getterFn, Object startValue, Object endValue);
	
	/**
	 * 增加一个范围匹配查询条件， column BETWEEN startValue AND endValue<br>
	 * startValue和endValue不允许为null，如果可能为null值，可使用gte、lte替代
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @param startValue 开始值
	 * @param endValue 结束值 
	 * @return 当前对象
	 */
	LambdaPredicate<T> between(boolean accept, SerializableFunction<T, ?> getterFn, Object startValue, Object endValue);

	/**
	 * 增加一个值非空查询条件
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> isNull(SerializableFunction<T, ?> getterFn);

	/**
	 * 增加一个值非空查询条件
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> isNull(boolean accept, SerializableFunction<T, ?> getterFn);

	/**
	 * 增加一个值为空查询条件
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> notNull(SerializableFunction<T, ?> getterFn);

	/**
	 * 增加一个值为空查询条件
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> notNull(boolean accept, SerializableFunction<T, ?> getterFn);
	
	/**
	 * 增加一个IN查询条件，column IN (value[0],value[1]...)，当value != null并且非空集合时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> in(SerializableFunction<T, ?> getterFn);
	
	/**
	 * 增加一个IN查询条件，column IN (value[0],value[1]...)
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> in(boolean accept, SerializableFunction<T, ?> getterFn);
	
	/**
	 * 增加一个NOT IN查询条件，column NOT IN (value[0],value[1]...)
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> notIn(SerializableFunction<T, ?> getterFn);
	
	/**
	 * 增加一个NOT IN查询条件，column NOT IN (value[0],value[1]...)
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @return 当前对象
	 */
	LambdaPredicate<T> notIn(boolean accept, SerializableFunction<T, ?> getterFn);
	
	/**
	 * 增加一个值相等查询条件，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param getterFn 实体类的get方法函数式 实体类的get方法函数式
	 * @param value 值
	 * @return 当前对象
	 */
	LambdaPredicate<T> eq(SerializableFunction<T, ?> getterFn, Object value);
	
	/**
	 * 增加一个值相等查询条件
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值
	 * @return 当前对象
	 */
	LambdaPredicate<T> eq(boolean accept, SerializableFunction<T, ?> getterFn, Object value);
	
	/**
	 * 增加一个值不等查询条件，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值
	 * @return 当前对象
	 */
	LambdaPredicate<T> neq(SerializableFunction<T, ?> getterFn, Object value);
	
	/**
	 * 增加一个值不等查询条件
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值
	 * @return 当前对象
	 */
	LambdaPredicate<T> neq(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	/**
	 * 增加一个值小于查询条件，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值
	 * @return 当前对象
	 */
	LambdaPredicate<T> lt(SerializableFunction<T, ?> getterFn, Object value);
	
	/**
	 * 增加一个值小于查询条件
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值
	 * @return 当前对象
	 */
	LambdaPredicate<T> lt(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	/**
	 * 增加一个值小于等于查询条件，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值
	 * @return 当前对象
	 */
	LambdaPredicate<T> lte(SerializableFunction<T, ?> getterFn, Object value);
	
	/**
	 * 增加一个值小于等于查询条件
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值
	 * @return 当前对象
	 */
	LambdaPredicate<T> lte(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	/**
	 * 增加一个值大于查询条件，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值
	 * @return 当前对象
	 */
	LambdaPredicate<T> gt(SerializableFunction<T, ?> getterFn, Object value);
	
	/**
	 * 增加一个值大于查询条件
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值
	 * @return 当前对象
	 */
	LambdaPredicate<T> gt(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	/**
	 * 增加一个值大于等于查询条件，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值
	 * @return 当前对象
	 */
	LambdaPredicate<T> gte(SerializableFunction<T, ?> getterFn, Object value);
	
	/**
	 * 增加一个值大于等于查询条件
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值
	 * @return 当前对象
	 */
	LambdaPredicate<T> gte(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	/**
	 * 增加一个匹配查询条件，注：该查询值不会自动加匹配符，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值应包括匹配符
	 * @return 当前对象
	 */
	LambdaPredicate<T> like(SerializableFunction<T, ?> getterFn, Object value);
	
	/**
	 * 增加一个匹配查询条件，注：该查询值不会自动加匹配符
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值应包括匹配符
	 * @return 当前对象
	 */
	LambdaPredicate<T> like(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	/**
	 * 增加一个非匹配查询条件，注：该查询值不会自动加匹配符，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值应包括匹配符
	 * @return 当前对象
	 */
	LambdaPredicate<T> notLike(SerializableFunction<T, ?> getterFn, Object value);

	/**
	 * 增加一个非匹配查询条件，注：该查询值不会自动加匹配符
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值应包括匹配符
	 * @return 当前对象
	 */
	LambdaPredicate<T> notLike(boolean accept, SerializableFunction<T, ?> getterFn, Object value);
	
	/**
	 * 增加一个包含值查询条件，在值前后增加%，column LIKE CONCAT('%', value, '%')，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值
	 * @return 当前对象
	 */
	LambdaPredicate<T> contain(SerializableFunction<T, ?> getterFn, Object value);
	
	/**
	 * 增加一个包含值查询条件，在值前后增加%，column LIKE CONCAT('%', value, '%')
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值
	 * @return 当前对象
	 */
	LambdaPredicate<T> contain(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	/**
	 * 增加一个非包含值查询条件，在值前后增加%，column NOT LIKE CONCAT('%', value, '%')，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值
	 * @return 当前对象
	 */
	LambdaPredicate<T> notContain(SerializableFunction<T, ?> getterFn, Object value);
	
	/**
	 * 增加一个非包含值查询条件，在值前后增加%，column NOT LIKE CONCAT('%', value, '%')
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值
	 * @return 当前对象
	 */
	LambdaPredicate<T> notContain(boolean accept, SerializableFunction<T, ?> getterFn, Object value);

	/**
	 * 增加一个左匹配查询条件，在值后面增加%，column LIKE CONCAT(value, '%')，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值
	 * @return 当前对象
	 */
	LambdaPredicate<T> startWith(SerializableFunction<T, ?> getterFn, Object value);

	/**
	 * 增加一个左匹配查询条件，在值后面增加%，column LIKE CONCAT(value, '%')
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值
	 * @return 当前对象
	 */
	LambdaPredicate<T> startWith(boolean accept, SerializableFunction<T, ?> getterFn, Object value);
	
	/**
	 * 增加一个右匹配查询条件，在值前面增加%，column LIKE CONCAT('%', value)，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值
	 * @return 当前对象
	 */
	LambdaPredicate<T> endWith(SerializableFunction<T, ?> getterFn, Object value);
	
	/**
	 * 增加一个右匹配查询条件，在值前面增加%，column LIKE CONCAT('%', value)
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值
	 * @return 当前对象
	 */
	LambdaPredicate<T> endWith(boolean accept, SerializableFunction<T, ?> getterFn, Object value);
	
	/**
	 * 增加一个IN查询条件，column IN (value[0],value[1]...)，当value != null并且非空集合时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值，允许值为数组或集合，非null并且元素个数大于0时生效
	 * @return 当前对象
	 */
	LambdaPredicate<T> in(SerializableFunction<T, ?> getterFn, Object value);
	
	/**
	 * 增加一个IN查询条件，column IN (value[0],value[1]...)
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值，允许值为数组或集合
	 * @return 当前对象
	 */
	LambdaPredicate<T> in(boolean accept, SerializableFunction<T, ?> getterFn, Object value);
	
	/**
	 * 增加一个NOT IN查询条件，column NOT IN (value[0],value[1]...)
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值，允许值为数组或集合，非null并且元素个数大于0时生效
	 * @return 当前对象
	 */
	LambdaPredicate<T> notIn(SerializableFunction<T, ?> getterFn, Object value);
	
	/**
	 * 增加一个NOT IN查询条件，column NOT IN (value[0],value[1]...)
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值，允许值为数组或集合
	 * @return 当前对象
	 */
	LambdaPredicate<T> notIn(boolean accept, SerializableFunction<T, ?> getterFn, Object value);
		
	/**
	 * 正则匹配
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值
	 * @return 当前对象
	 */
	LambdaPredicate<T> regex(SerializableFunction<T, ?> getterFn, Object value);
	
	/**
	 * 正则匹配
	 * @param accept 当值为true时，条件生效
	 * @param getterFn 实体类的get方法函数式
	 * @param value 值
	 * @return 当前对象
	 */
	LambdaPredicate<T> regex(boolean accept, SerializableFunction<T, ?> getterFn, Object value);
}
