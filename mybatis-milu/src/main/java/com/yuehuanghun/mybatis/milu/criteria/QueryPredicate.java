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

import java.util.Collection;
import java.util.function.Consumer;

import javax.persistence.LockModeType;
import javax.persistence.Version;

import com.yuehuanghun.mybatis.milu.annotation.JoinMode;
import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.data.Sort.Direction;
import com.yuehuanghun.mybatis.milu.data.Sort.NullHandling;
import com.yuehuanghun.mybatis.milu.pagehelper.Pageable;

/**
 * 
 * @see Predicates
 *
 */
public interface QueryPredicate extends Predicate {
	
	/**
	 * 指定查询的（实体类）属性，未指定时查询实体所有属性
	 * @param attrNames 需要查询的实体属性
	 * @return 当前对象
	 */
	QueryPredicate select(String... attrNames);
	
	/**
	 * 指定查询的（实体类）属性，未指定时查询实体所有属性
	 * @param attrNameChain 多个属性名用英文逗号隔开，举例：name, age, createTime
	 * @return 当前对象
	 */
	QueryPredicate selects(String attrNameChain);
	
	/**
	 * 排除查询指定的（实体类）属性，select比exselect优先级更高<br>
	 * 例如文本或二进制大字段，影响IO效率，在查询时排除掉
	 * @param attrNames 需要查询的实体属性
	 * @return 当前对象
	 */
	QueryPredicate exselect(String... attrNames);
	
	/**
	 * 排除查询指定的（实体类）属性，select比exselect优先级更高<br>
	 * 例如文本或二进制大字段，影响IO效率，在查询时排除掉
	 * @param attrNameChain 多个属性名用英文逗号隔开，举例：content, fileData
	 * @return 当前对象
	 */
	QueryPredicate exselects(String attrNameChain);

	/**
	 * 添加排序，不指定排序方向，以数据库默认排序为准
	 * @param attrNames 属性名
	 * @return 当前对象
	 */
	QueryPredicate order(String... attrNames);

	/**
	 * 添加排序，不指定排序方向，以数据库默认排序为准
	 * @param nullHandling 空值排序方式
	 * @param attrNames 属性名
	 * @return 当前对象
	 */
	QueryPredicate order(NullHandling nullHandling, String... attrNames);
	
	/**
	 * 添加排序，指定排序方向
	 * @param direction 排序方向枚举
	 * @param attrNames 属性名
	 * @return 当前对象
	 */
	QueryPredicate order(Direction direction, String... attrNames);
	
	/**
	 * 添加排序，指定排序方向
	 * @param direction 排序方向枚举
	 * @param nullHandling 空值排序方式
	 * @param attrNames 属性名
	 * @return 当前对象
	 */
	QueryPredicate order(Direction direction, NullHandling nullHandling, String... attrNames);
	
	/**
	 * 指定排序
	 * @param sort 排序
	 * @return 当前对象
	 */
	QueryPredicate order(com.yuehuanghun.mybatis.milu.data.Sort sort);
	
	/**
	 * 添加升序排序
	 * @param attrNames 属性名
	 * @return 当前对象
	 */
	QueryPredicate orderAsc(String... attrNames);
	
	/**
	 * 添加升序排序
	 * @param nullHandling 空值排序方式
	 * @param attrNames 属性名
	 * @return 当前对象
	 */
	QueryPredicate orderAsc(NullHandling nullHandling, String... attrNames);
	
	/**
	 * 添加降序排序
	 * @param attrNames 属性名
	 * @return 当前对象
	 */
	QueryPredicate orderDesc(String... attrNames);
	
	/**
	 * 添加降序排序
	 * @param nullHandling 空值排序方式
	 * @param attrNames 属性名
	 * @return 当前对象
	 */
	QueryPredicate orderDesc(NullHandling nullHandling, String... attrNames);

	/**
	 * 获取第1页的pageSize条数据
	 * @param pageSize 每页行数
	 * @return 当前对象
	 */
	QueryPredicate limit(int pageSize);
	
	/**
	 * 获取第1页的pageSize条数据
	 * @param pageSize 每页行数
	 * @param count 是否查询符合的总行数
	 * @return 当前对象
	 */
	QueryPredicate limit(int pageSize, boolean count);
	
	/**
	 * 获取第pageNum页的pageSize条数据
	 * @param pageNum 起始页，从1开始
	 * @param pageSize 每页行数
	 * @return 当前对象
	 */
	QueryPredicate limit(int pageNum, int pageSize);
	
	/**
	 * 
	 * 获取第pageNum页的pageSize条数据
	 * @param pageNum 起始页，从1开始
	 * @param pageSize 每页行数
	 * @param count 是否查询符合的总行数
	 * @return 当前对象
	 */
	QueryPredicate limit(int pageNum, int pageSize, boolean count);
	
	/**
	 * 使用一个Pageable对象传递分页信息
	 * @param page 分页，getPageNum()、getPageSize()值需大于0
	 * @return 当前对象
	 */
	QueryPredicate limit(Pageable page);
	
	/**
	 * 使用偏移量分页
	 * @param offset 起始位置，偏移量。首条数据从0开始计数。
	 * @param size 查询行数
	 * @param count 是否查询符合的总行数
	 * @return 当前对象
	 */
	QueryPredicate limitOffset(int offset, int size, boolean count);

	/**
	 * 使用偏移量分页，默认查询总行数
	 * @param offset 起始位置，偏移量。首条数据从0开始计数。
	 * @param size 查询行数
	 * @return 当前对象
	 */
	QueryPredicate limitOffset(int offset, int size);
	
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
	QueryPredicate isNull(boolean accept, String attrName);

	@Override
	QueryPredicate notNull(String attrName);

	@Override
	QueryPredicate notNull(boolean accept, String attrName);

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

	@Override
	QueryPredicate regex(String attrName, Object value);

	@Override
	QueryPredicate regex(boolean accept, String attrName, Object value);
	
	@Override
	QueryPredicate undeleted();
	
	@Override
	QueryPredicate deleted();
	
	/**
	 * 去除重复
	 * @return 当前对象
	 */
	QueryPredicate distinct();
	
	/**
	 * 设置数据库锁模式
	 * @param lockModeType {@link LockModeType}锁模式
	 * 本框架下模式READ/WRITE/OPTIMISTIC/OPTIMISTIC_FORCE_INCREMENT都等同于NONE，即无锁，乐观锁是实体在声明{@link Version}之后自动使用的<br>
	 * PESSIMISTIC_WRITE等于PESSIMISTIC_FORCE_INCREMENT，即使用悲观写锁，如果有{@link Version}声明属性，则自增。<br>
	 * 如果数据库无读锁（共享锁）则PESSIMISTIC_READ跟PESSIMISTIC_WRITE功能一致
	 * @return 当前对象
	 */
	QueryPredicate lock(LockModeType lockModeType);
	
	/**
	 * 设置数据库锁，并且锁模式为悲观写锁{@link LockModeType#PESSIMISTIC_WRITE}
	 * @return 当前对象
	 */
	QueryPredicate lock();
	
	/**
	 * 设置全局联结模式
	 * @param joinMode 联结模式
	 * @return 当前对象
	 */
	QueryPredicate joinMode(JoinMode joinMode);
	
	/**
	 * 设置关联属性的实体联结模式
	 * @param propertyName 实体类关联实体（或实体集合）的属性名
	 * @param joinMode 联结模式
	 * @return 当前对象
	 */
	QueryPredicate joinMode(String propertyName, JoinMode joinMode);
	
	/**
	 * 设置关联属性的实体联结模式
	 * @param propertyName 实体类关联实体（或实体集合）的属性名
	 * @param joinMode 联结模式
	 * @param joinPredicate 联结附加条件。通过Predicates.predicate()创建一个Predicate
	 * @return 当前对象
	 */
	QueryPredicate joinMode(String propertyName, JoinMode joinMode, Predicate joinPredicate);
	
	/**
	 * 设置关联属性的实体联结模式
	 * @param propertyName 实体类关联实体（或实体集合）的属性名
	 * @param joinMode 联结模式
	 * @param joinPredicate 联结附加条件。
	 * @return 当前对象
	 */
	QueryPredicate joinMode(String propertyName, JoinMode joinMode, Consumer<Predicate> joinPredicate);
	
	/**
	 * 将Example样例条件将为Predicate动态条件<br>
	 * 继承example的匹配模式与范围查询条件
	 * @param example example必须为实体类的实例
	 * @return 当前对象
	 */
	QueryPredicate byExample(Object example);
	
	/**
	 * 全文搜索<br>
	 * 对应数据库可能需要安装相应插件、启用插件、建立全文索引才可用<br>
	 * 由于不同的数据库全文搜索的表达方式差别大，所以如果切换到其它类型数据库，可能要调整查询参数
	 * 
	 * @param attrNames 全文索引属性（列）
	 * @param keywordExpression 关键字表达式，不同的数据库的表达式不尽相同，请注意区分
	 * @return 当前对象
	 */
	QueryPredicate fulltext(Collection<String> attrNames, String keywordExpression);
	
	/**
	 * 全文搜索<br>
	 * 对应数据库可能需要安装相应插件、启用插件、建立全文索引才可用<br>
	 * 由于不同的数据库全文搜索的表达方式差别大，所以如果切换到其它类型数据库，可能要调整查询参数
	 * 
	 * @param attrNames 全文索引属性（列）
	 * @param keywordExpression 关键字表达式，不同的数据库的表达式不尽相同，请注意区分
	 * @param fulltextMode 全文索引模式
	 * @return 当前对象
	 */
	QueryPredicate fulltext(Collection<String> attrNames, String keywordExpression, FulltextMode fulltextMode);
	
	/**
	 * 增加exist子查询条件
	 * @param exists exists子查询
	 * @return 当前对象
	 */
	QueryPredicate exists(Exists<?> exists);
	
	/**
	 * 增加not exist子查询条件
	 * @param exists exists子查询
	 * @return 当前对象
	 */
	QueryPredicate notExists(Exists<?> exists);
}
