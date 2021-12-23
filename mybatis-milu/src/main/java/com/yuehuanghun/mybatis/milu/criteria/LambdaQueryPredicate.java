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

import javax.persistence.LockModeType;
import javax.persistence.Version;

import com.yuehuanghun.mybatis.milu.annotation.JoinMode;
import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.criteria.lambda.SerializableFunction;
import com.yuehuanghun.mybatis.milu.data.Sort.Direction;
import com.yuehuanghun.mybatis.milu.pagehelper.Pageable;

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
	 * 添加升序排序
	 * @param getterFns 实体类的getter函数式
	 * @return 当前对象
	 */
	@SuppressWarnings("unchecked")
	LambdaQueryPredicate<T> orderAsc(SerializableFunction<T, ?>... getterFns);
	
	/**
	 * 添加降序排序
	 * @param getterFns 实体类的getter函数式
	 * @return 当前对象
	 */
	@SuppressWarnings("unchecked")
	LambdaQueryPredicate<T> orderDesc(SerializableFunction<T, ?>... getterFns);

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

	/**
	 * 使用一个Pageable对象传递分页信息
	 * @param page 分页，getPageNum()、getPageSize()值需大于0
	 * @return 当前对象
	 */
	LambdaQueryPredicate<T> limit(Pageable page);

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
	LambdaQueryPredicate<T> isNull(boolean accept, SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> notNull(SerializableFunction<T, ?> fn);

	@Override
	LambdaQueryPredicate<T> notNull(boolean accept, SerializableFunction<T, ?> fn);

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
	
	/**
	 * 去除重复
	 * @return 当前对象
	 */
	LambdaQueryPredicate<T> distinct();
	
	/**
	 * 设置数据库锁模式
	 * @param lockModeType {@link LockModeType}锁模式<br>
	 * 本框架下模式READ/WRITE/OPTIMISTIC/OPTIMISTIC_FORCE_INCREMENT都等同于NONE，即无锁，乐观锁是实体在声明{@link Version}之后自动使用的<br>
	 * PESSIMISTIC_WRITE等于PESSIMISTIC_FORCE_INCREMENT，即使用悲观写锁，如果有{@link Version}声明属性，则自增。<br>
	 * 如果数据库无读锁（共享锁）则PESSIMISTIC_READ跟PESSIMISTIC_WRITE功能一致
	 * @return 当前对象
	 */
	LambdaQueryPredicate<T> lock(LockModeType lockModeType);
	
	/**
	 * 设置数据库锁，并且锁模式为悲观写锁{@link LockModeType#PESSIMISTIC_WRITE}
	 * @return 当前对象
	 */
	LambdaQueryPredicate<T> lock();
	
	
	/**
	 * 设置全局联结模式
	 * @param joinMode 联结模式
	 * @return 当前对象
	 */
	LambdaQueryPredicate<T> joinMode(JoinMode joinMode);
	
	/**
	 * 设置关联属性的实体联结模式
	 * @param refGetterFn 实体类属性的getter函数式
	 * @param joinMode 联结模式
	 * @return 当前对象
	 */
	LambdaQueryPredicate<T> joinMode(SerializableFunction<T, ?> refGetterFn, JoinMode joinMode);
}
