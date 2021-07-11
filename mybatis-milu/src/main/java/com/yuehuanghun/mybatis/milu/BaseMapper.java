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
import com.yuehuanghun.mybatis.milu.criteria.Predicate;
import com.yuehuanghun.mybatis.milu.criteria.QueryPredicate;
import com.yuehuanghun.mybatis.milu.criteria.StatisticPredicate;
import com.yuehuanghun.mybatis.milu.data.Sort;

/**
 * 通用查询方法接口<br>
 * 勿在继续接口上覆盖或重载BaseMapper中的方法，以免发现不必要的错误
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
	Optional<T> findById(@Param("id")ID id);
	
	/**
	 * 通过ID集合查询一组数据，如果实体类未声明ID，则不可使用此方法
	 * @param ids 主键集合
	 * @return 列表
	 */
	List<T> findByIds(@Param("ids")Collection<ID> ids);
	
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
	List<T> findAllAndSort(@Param("sort")Sort sort);
	
	/**
	 * 使用实体类作为查询参数，非null值才会参与查询，关联表属性无效
	 * @param example 条件
	 * @return 列表
	 */
	List<T> findByExample(@Param("example")T example);
	
	/**
	 * 使用实体类作为查询参数，并指定排序方式，非null值才会参与查询，关联表属性无效
	 * @param example 条件
	 * @param sort 排序
	 * @return 列表
	 */
	List<T> findByExampleAndSort(@Param("example")T example, @Param("sort")Sort sort);
	
	/**
	 * 插入一条数据
	 * @param entity 新增实体对象
	 * @return 影响行数
	 */
	int insert(T entity);
	
	/**
	 * 批量插入，使用单SQL插入方式，支持批量插入的数据库才能使用此方法<br>
	 * 目前支持数据库：MySql、MariaDB、PostgreDB
	 * @param entityList 新增实体对象列表
	 * @return 影响行数
	 */
	int batchInsert(@Param("entityList")Collection<T> entityList);
	
	/**
	 * 通过ID更新，非null值被更新<br>
	 * 如果有Version字段（被@Version注解的实体类属性），只有Version字段非null时才有效，如果由于版本不匹配不被更新，不会抛出异常，请自行判断影响行数（即返回值 ）
	 * @param entity 更新对象
	 * @return 影响行数
	 */
	int updateById(@Param("entity")T entity);
	
	/**
	 * 通过ID删除单条数据
	 * @param id 主键
	 * @return 影响行数
	 */
	int deleteById(@Param("id")ID id);
	
	/**
	 * 通过ID集合删除批量数据
	 * @param ids 主键集合
	 * @return 影响行数
	 */
	int deleteByIds(@Param("ids")Collection<ID> ids);

	/**
	 * 以实体类为参数进行统计行数
	 * @param example 条件
	 * @return 影响行数
	 */
	int countByExample(@Param("example")T example);

	/**
	 * 动态条件计算行数
	 * @param predicate 条件
	 * @return 影响行数
	 */
	int countByCriteria(@Param("criteria")Predicate predicate);
	
	/**
	 * 动态条件计算行数
	 * @param predicate 条件
	 * @return 影响行数
	 */
	int countByCriteria(@Param("criteria")Consumer<Predicate> predicate);
	
	/**
	 * 动态条件计算行数
	 * @param predicate 条件
	 * @return 影响行数
	 */
	int countByLambdaCriteria(@Param("criteria")Consumer<LambdaPredicate<T>> predicate);
	
	/**
	 * 动态条件查询<br>
	 * @param predicate 条件
	 * @return 列表
	 */
	List<T> findByCriteria(@Param("criteria")QueryPredicate predicate);
	
	/**
	 * 动态条件查询<br>
	 * @param predicate 条件
	 * @return 列表
	 */
	List<T> findByCriteria(@Param("criteria")Consumer<QueryPredicate> predicate);
	
	/**
	 * lambda表达式动态条件查询
	 * @param predicate 条件
	 * @return 列表
	 */
	List<T> findByLambdaCriteria(@Param("criteria")Consumer<LambdaQueryPredicate<T>> predicate);
	
	/**
	 * 动态条件更新<br>
	 * 支持关联表条件，但需要确定数据库是否支持关联条件查询进行更新<br>
	 * @param entity 只更新实体对象中的非Null属性
	 * @param predicate Where条件
	 * @return 影响行数
	 */
	int updateByCriteria(@Param("entity")T entity, @Param("criteria")Predicate predicate);
	
	/**
	 * 动态条件更新<br>
	 * 支持关联表条件，但需要确定数据库是否支持关联条件查询进行更新<br>
	 * @param entity 只更新实体对象中的非Null属性
	 * @param predicate Where条件
	 * @return 影响行数
	 */
	int updateByCriteria(@Param("entity")T entity, @Param("criteria")Consumer<Predicate> predicate);
	
	/**
	 * 动态条件更新<br>
	 * @param entity 只更新实体对象中的非Null属性
	 * @param predicate Where条件
	 * @return 影响行数
	 */
	int updateByLambdaCriteria(@Param("entity")T entity, @Param("criteria")Consumer<LambdaPredicate<T>> predicate);
	
	/**
	 * 动态条件删除<br>
	 * @param predicate 条件
	 * @return 影响行数
	 */
	int deleteByCriteria(@Param("criteria")Predicate predicate);
	
	/**
	 * 动态条件删除<br>
	 * @param predicate 条件
	 * @return 影响行数
	 */
	int deleteByCriteria(@Param("criteria")Consumer<Predicate> predicate);
	
	/**
	 * 动态条件删除<br>
	 * @param predicate 条件
	 * @return 影响行数
	 */
	int deleteByLambdaCriteria(@Param("criteria")Consumer<LambdaPredicate<T>> predicate);
	
	/**
	 * 动态统计数据
	 * @param predicate 条件
	 * @return 统计数据列表
	 */
	List<Map<String, Object>> statisticByCriteria(@Param("criteria") StatisticPredicate predicate);
	
	/**
	 * 动态统计数据
	 * @param predicate 条件
	 * @return 统计数据列表
	 */
	List<Map<String, Object>> statisticByCriteria(@Param("criteria") Consumer<StatisticPredicate> predicate);
	
	/**
	 * 动态统计数据
	 * @param <E> 每行数据的接收类
	 * @param predicate 条件
	 * @param resultType 结果类
	 * @return 统计数据列表
	 */
	<E> List<E> statisticByCriteria(@Param("criteria") Consumer<StatisticPredicate> predicate, @Param("resultType") Class<E> resultType);
	
	/**
	 * 动态统计数据
	 * @param predicate 条件
	 * @return 统计数据列表
	 */
	List<Map<String, Object>> statisticByLambdaCriteria(@Param("criteria") Consumer<LambdaStatisticPredicate<T>> predicate);
	
	/**
	 * 动态统计数据
	 * @param <E> 每行数据的接收类
	 * @param predicate 条件
	 * @param resultType 结果类
	 * @return 统计数据列表
	 */
	<E> List<E> statisticByLambdaCriteria(@Param("criteria") Consumer<LambdaStatisticPredicate<T>> predicate, @Param("resultType") Class<E> resultType);
	
	/**
	 * 刷新执行查询到数据库
	 */
	void flush();
}
