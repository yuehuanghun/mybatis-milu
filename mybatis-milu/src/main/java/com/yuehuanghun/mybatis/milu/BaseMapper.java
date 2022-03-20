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
package com.yuehuanghun.mybatis.milu;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.ibatis.annotations.Param;

import com.yuehuanghun.mybatis.milu.criteria.LambdaPredicate;
import com.yuehuanghun.mybatis.milu.criteria.LambdaQueryPredicate;
import com.yuehuanghun.mybatis.milu.criteria.LambdaStatisticPredicate;
import com.yuehuanghun.mybatis.milu.criteria.LambdaUpdatePredicate;
import com.yuehuanghun.mybatis.milu.criteria.Predicate;
import com.yuehuanghun.mybatis.milu.criteria.QueryPredicate;
import com.yuehuanghun.mybatis.milu.criteria.StatisticPredicate;
import com.yuehuanghun.mybatis.milu.criteria.UpdatePredicate;
import com.yuehuanghun.mybatis.milu.criteria.lambda.SerializableFunction;
import com.yuehuanghun.mybatis.milu.data.Sort;
import com.yuehuanghun.mybatis.milu.pagehelper.Pageable;
import com.yuehuanghun.mybatis.milu.tool.Constants;

/**
 * 通用查询方法接口<br>
 * 勿在继承接口上覆盖或重载BaseMapper中的方法，以免发现不必要的错误
 * @author yuehuanghun
 *
 * @param <T> 实体类
 * @param <ID> 主键类
 */
public interface BaseMapper<T, ID extends Serializable> {

	/**
	 * 通过ID查询唯一，如果实体类未声明ID，则不可使用此方法
	 * @param id 主键
	 * @return 唯一行
	 */
	Optional<T> findById(@Param(Constants.ID) ID id);
	
	/**
	 * 通过ID集合查询一组数据，如果实体类未声明ID，则不可使用此方法
	 * @param ids 主键集合
	 * @return 列表
	 */
	List<T> findByIds(@Param(Constants.IDS) Collection<ID> ids);
	
	/**
	 * 查询表所有数据
	 * @return 列表
	 */
	List<T> findAll();
	
	/**
	 * 查询表所有数据，并指定排序方式
	 * @param sort 排序
	 * @return 列表
	 */
	List<T> findAllAndSort(@Param(Constants.SORT) Sort sort);
	
	/**
	 * 使用实体类作为查询参数，非null值才会参与查询，关联表属性无效
	 * @param example 条件
	 * @return 列表
	 */
	List<T> findByExample(@Param(Constants.EXAMPLE) T example);
	
	/**
	 * 使用实体类作为查询参数，非null值才会参与查询，关联表属性无效<br>
	 * 结果集必须是0或1条数据，否则会报错。
	 * @param example 条件
	 * @param pageable 分页参数，可null
	 * @return 列表
	 */
	T findUniqueByExample(@Param(Constants.EXAMPLE) T example, @Param(Constants.PAGE) Pageable pageable);

	/**
	 * 使用实体类作为查询参数，非null值才会参与查询，关联表属性无效
	 * @param example 条件
	 * @param pageable 分页参数，可null
	 * @return 列表
	 */
	List<T> findByExample(@Param(Constants.EXAMPLE) T example, @Param(Constants.PAGE) Pageable pageable);
	
	/**
	 * 使用实体类作为查询参数，并指定排序方式，非null值才会参与查询，关联表属性无效
	 * @param example 条件
	 * @param sort 排序，可null
	 * @return 列表
	 */
	List<T> findByExampleAndSort(@Param(Constants.EXAMPLE) T example, @Param(Constants.SORT) Sort sort);
	
	/**
	 * 使用实体类作为查询参数，并指定排序方式，非null值才会参与查询，关联表属性无效
	 * @param example 条件
	 * @param sort 排序，可null，可null
	 * @param pageable 分页参数
	 * @return 列表
	 */
	List<T> findByExampleAndSort(@Param(Constants.EXAMPLE) T example, @Param(Constants.SORT) Sort sort, @Param(Constants.PAGE) Pageable pageable);
	
	/**
	 * 使用实体类作为查询参数，并指定排序方式，非null值才会参与查询，关联表属性无效<br>
	 * 结果集必须是0或1条数据，否则会报错。
	 * @param example 条件
	 * @param sort 排序，可null
	 * @param pageable 分页参数，可null
	 * @return 列表
	 */
	T findUniqueByExampleAndSort(@Param(Constants.EXAMPLE) T example, @Param(Constants.SORT) Sort sort, @Param(Constants.PAGE) Pageable pageable);
	
	/**
	 * 插入一条数据
	 * @param entity 新增实体对象
	 * @return 影响行数
	 */
	int insert(T entity);
	
	/**
	 * 批量插入，使用单SQL插入方式，支持批量插入的数据库才能使用此方法<br>
	 * 目前支持数据库：MySql、MariaDB、PostgreDB、Oracle、SqlServer，或兼容前所列数据库语法的数据库
	 * @param entityList 新增实体对象列表
	 * @return 影响行数
	 */
	int batchInsert(@Param(Constants.ENTITY_LIST) Collection<T> entityList);
	
	/**
	 * 通过ID更新，默认非null值被更新，可以通过@AttributeOptions注解的updateMode设定更新的模式<br>
	 * 如果有Version字段（被@Version注解的实体类属性），只有Version字段非null时才有效，如果由于版本不匹配不被更新，不会抛出异常，请自行判断影响行数（即返回值 ）
	 * @param entity 更新对象
	 * @return 影响行数
	 */
	int updateById(@Param(Constants.ENTITY) T entity);
	
	/**
	 * 通过ID删除单条数据
	 * @param id 主键
	 * @return 影响行数
	 */
	int deleteById(@Param(Constants.ID) ID id);
	
	/**
	 * 通过ID集合删除批量数据
	 * @param ids 主键集合
	 * @return 影响行数
	 */
	int deleteByIds(@Param(Constants.IDS) Collection<ID> ids);

	/**
	 * 以实体类为参数进行统计行数
	 * @param example 条件
	 * @return 影响行数
	 */
	int countByExample(@Param(Constants.EXAMPLE) T example);

	/**
	 * 动态条件计算行数
	 * @param predicate 条件
	 * @return 影响行数
	 */
	int countByCriteria(@Param(Constants.CRITERIA) Predicate predicate);
	
	/**
	 * 动态条件计算行数
	 * @param predicate 条件
	 * @return 影响行数
	 */
	int countByCriteria(@Param(Constants.CRITERIA) Consumer<Predicate> predicate);
	
	/**
	 * 动态条件计算行数
	 * @param predicate 条件
	 * @return 影响行数
	 */
	int countByLambdaCriteria(@Param(Constants.CRITERIA) Consumer<LambdaPredicate<T>> predicate);
	
	/**
	 * 动态条件查询<br>
	 * @param predicate 条件
	 * @return 列表
	 */
	List<T> findByCriteria(@Param(Constants.CRITERIA) QueryPredicate predicate);
	
	/**
	 * 动态条件查询唯一数据<br>
	 * 结果集必须是0或1条数据，否则会报错。建议使用.limit(1)进行结果集行数限制。
	 * @param predicate 条件
	 * @return 列表
	 */
	T findUniqueByCriteria(@Param(Constants.CRITERIA) QueryPredicate predicate);
	
	/**
	 * 动态条件查询<br>
	 * @param predicate 条件
	 * @return 列表
	 */
	List<T> findByCriteria(@Param(Constants.CRITERIA) Consumer<QueryPredicate> predicate);
	
	/**
	 * 动态条件查询唯一数据<br>
	 * 结果集必须是0或1条数据，否则会报错。建议使用.limit(1)进行结果集行数限制。
	 * @param predicate 条件
	 * @return 列表
	 */
	T findUniqueByCriteria(@Param(Constants.CRITERIA) Consumer<QueryPredicate> predicate);
	
	/**
	 * 动态条件查询<br>
	 * @param <E> 元素
	 * @param predicate 条件
	 * @param resultType 结果类
	 * @return 列表
	 */
	<E> List<E> findByCriteria(@Param(Constants.CRITERIA) Consumer<QueryPredicate> predicate, @Param(Constants.RESULT_TYPE) Class<E> resultType);
	
	/**
	 * lambda表达式动态条件查询
	 * @param predicate 条件
	 * @return 列表
	 */
	List<T> findByLambdaCriteria(@Param(Constants.CRITERIA) Consumer<LambdaQueryPredicate<T>> predicate);
	
	/**
	 * lambda表达式动态条件查询唯一数据<br>
	 * 结果集必须是0或1条数据，否则会报错。建议使用.limit(1)进行结果集行数限制。
	 * @param predicate 条件
	 * @return 列表
	 */
	T findUniqueByLambdaCriteria(@Param(Constants.CRITERIA) Consumer<LambdaQueryPredicate<T>> predicate);

	/**
	 * 动态条件查询<br>
	 * @param <E> 元素
	 * @param predicate 条件
	 * @param resultType 结果类
	 * @return 列表
	 */
	<E> List<E> findByLambdaCriteria(@Param(Constants.CRITERIA) Consumer<LambdaQueryPredicate<T>> predicate, @Param(Constants.RESULT_TYPE) Class<E> resultType);
	
	/**
	 * 动态条件更新<br>
	 * 支持关联表条件，但需要确定数据库是否支持关联条件查询进行更新<br>
	 * @param entity 默认非null值被更新，可以通过predicate.updateMode(Mode mode)设定更新的模式
	 * @param predicate 查询条件
	 * @return 影响行数
	 */
	int updateByCriteria(@Param(Constants.ENTITY) T entity, @Param(Constants.CRITERIA) UpdatePredicate predicate);
	
	/**
	 * 动态条件更新<br>
	 * 支持关联表条件，但需要确定数据库是否支持关联条件查询进行更新<br>
	 * @param entity 默认非null值被更新，可以通过predicate.updateMode(Mode mode)设定更新的模式
	 * @param predicate 查询条件
	 * @return 影响行数
	 */
	int updateByCriteria(@Param(Constants.ENTITY) T entity, @Param(Constants.CRITERIA) Consumer<UpdatePredicate> predicate);
	
	/**
	 * 动态条件更新<br>
	 * @param entity 默认非null值被更新，可以通过predicate.updateMode(Mode mode)设定更新的模式
	 * @param predicate 查询条件
	 * @return 影响行数
	 */
	int updateByLambdaCriteria(@Param(Constants.ENTITY) T entity, @Param(Constants.CRITERIA) Consumer<LambdaUpdatePredicate<T>> predicate);
	
	/**
	 * 动态条件删除<br>
	 * @param predicate 条件
	 * @return 影响行数
	 */
	int deleteByCriteria(@Param(Constants.CRITERIA) Predicate predicate);
	
	/**
	 * 动态条件删除<br>
	 * @param predicate 条件
	 * @return 影响行数
	 */
	int deleteByCriteria(@Param(Constants.CRITERIA) Consumer<Predicate> predicate);
	
	/**
	 * 动态条件删除<br>
	 * @param predicate 条件
	 * @return 影响行数
	 */
	int deleteByLambdaCriteria(@Param(Constants.CRITERIA) Consumer<LambdaPredicate<T>> predicate);
	
	/**
	 * 动态统计数据
	 * @param predicate 条件
	 * @return 统计数据列表
	 */
	List<Map<String, Object>> statisticByCriteria(@Param(Constants.CRITERIA) StatisticPredicate predicate);
	
	/**
	 * 动态统计数据
	 * @param predicate 条件
	 * @return 统计数据列表
	 */
	List<Map<String, Object>> statisticByCriteria(@Param(Constants.CRITERIA) Consumer<StatisticPredicate> predicate);
	
	/**
	 * 动态统计数据
	 * @param <E> 每行数据的接收类
	 * @param predicate 条件
	 * @param resultType 结果类
	 * @return 统计数据列表
	 */
	<E> List<E> statisticByCriteria(@Param(Constants.CRITERIA) Consumer<StatisticPredicate> predicate, @Param(Constants.RESULT_TYPE) Class<E> resultType);
	
	/**
	 * 动态统计数据
	 * @param predicate 条件
	 * @return 统计数据列表
	 */
	List<Map<String, Object>> statisticByLambdaCriteria(@Param(Constants.CRITERIA) Consumer<LambdaStatisticPredicate<T>> predicate);
	
	/**
	 * 动态统计数据
	 * @param <E> 每行数据的接收类
	 * @param predicate 条件
	 * @param resultType 结果类
	 * @return 统计数据列表
	 */
	<E> List<E> statisticByLambdaCriteria(@Param(Constants.CRITERIA) Consumer<LambdaStatisticPredicate<T>> predicate, @Param(Constants.RESULT_TYPE) Class<E> resultType);
	
	/**
	 * 单属性更新<br>
	 * 除了指定属性外，@Version及@AttributeOptions中声明为更新时自动填充的属性也会被更新<br>
	 * 指定更新属性为@Version属性是无意义的<br>
	 * 指定更新属性如果为@AttributeOptions声明为更新时自动填充的属性，则以指定值为准
	 * 
	 * @param attrName 属性名，本实体中的属性，不能为关联实体的属性。注意不要直接填字段名。
	 * @param value    更新值，可以为null值
	 * @param id       主键
	 * @return 影响行数
	 */
	int updateAttrById(@Param(Constants.ATTR_NAME) String attrName, @Param(Constants.VALUE) Object value, @Param(Constants.ID) ID id);

	/**
	 * 单属性更新<br>
	 * 除了指定属性外，@Version及@AttributeOptions中声明为更新时自动填充的属性也会被更新<br>
	 * 指定更新属性为@Version属性是无意义的<br>
	 * 指定更新属性如果为@AttributeOptions声明为更新时自动填充的属性，则以指定值为准
	 * 
	 * @param attrNameGetter 实体类属性名getter函数式。
	 * @param value          更新值，可以为null值
	 * @param id             主键
	 * @return 影响行数
	 */
	int updateAttrById(@Param(Constants.ATTR_NAME) SerializableFunction<T, ?> attrNameGetter, @Param(Constants.VALUE) Object value, @Param(Constants.ID) ID id);
	
	/**
	 * 单属性更新<br>
	 * 除了指定属性外，@Version及@AttributeOptions中声明为更新时自动填充的属性也会被更新<br>
	 * 指定更新属性为@Version属性是无意义的<br>
	 * 指定更新属性如果为@AttributeOptions声明为更新时自动填充的属性，则以指定值为准
	 * 
	 * @param attrName  实体类属性名。
	 * @param value     更新值，可以为null值
	 * @param predicate 条件
	 * @return 影响行数
	 */
	int updateAttrByCriteria(@Param(Constants.ATTR_NAME) String attrName, @Param(Constants.VALUE) Object value, @Param(Constants.CRITERIA) UpdatePredicate predicate);
	
	/**
	 * 单属性更新<br>
	 * 除了指定属性外，@Version及@AttributeOptions中声明为更新时自动填充的属性也会被更新<br>
	 * 指定更新属性为@Version属性是无意义的<br>
	 * 指定更新属性如果为@AttributeOptions声明为更新时自动填充的属性，则以指定值为准
	 * 
	 * @param attrName  实体类属性名。
	 * @param value     更新值，可以为null值
	 * @param predicate 条件
	 * @return 影响行数
	 */
	int updateAttrByCriteria(@Param(Constants.ATTR_NAME) String attrName, @Param(Constants.VALUE) Object value, @Param(Constants.CRITERIA) Consumer<UpdatePredicate> predicate);
	
	/**
	 * 单属性更新<br>
	 * 除了指定属性外，@Version及@AttributeOptions中声明为更新时自动填充的属性也会被更新<br>
	 * 指定更新属性为@Version属性是无意义的<br>
	 * 指定更新属性如果为@AttributeOptions声明为更新时自动填充的属性，则以指定值为准
	 * 
	 * @param attrNameGetter 实体类属性名getter函数式。
	 * @param value          更新值，可以为null值
	 * @param predicate      条件
	 * @return 影响行数
	 */
	int updateAttrByLambdaCriteria(@Param(Constants.ATTR_NAME) SerializableFunction<T, ?> attrNameGetter, @Param(Constants.VALUE) Object value, @Param(Constants.CRITERIA) Consumer<LambdaUpdatePredicate<T>> predicate);
	
	/**
	 * 刷新执行查询到数据库
	 */
	void flush();
}
