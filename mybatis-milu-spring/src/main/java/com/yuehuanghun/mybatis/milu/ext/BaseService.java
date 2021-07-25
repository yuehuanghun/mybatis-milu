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

package com.yuehuanghun.mybatis.milu.ext;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.ibatis.annotations.Param;

import com.yuehuanghun.mybatis.milu.BaseMapper;
import com.yuehuanghun.mybatis.milu.criteria.LambdaPredicate;
import com.yuehuanghun.mybatis.milu.criteria.LambdaQueryPredicate;
import com.yuehuanghun.mybatis.milu.criteria.LambdaStatisticPredicate;
import com.yuehuanghun.mybatis.milu.criteria.Predicate;
import com.yuehuanghun.mybatis.milu.criteria.QueryPredicate;
import com.yuehuanghun.mybatis.milu.criteria.StatisticPredicate;
import com.yuehuanghun.mybatis.milu.data.Sort;
import com.yuehuanghun.mybatis.milu.tool.Constants;

/**
 * 将BaseMaper的功能以Service的形式提供
 * @author yuehuanghun
 *
 * @param <T> 实体类
 * @param <ID> 实体类的ID类型
 */
public interface BaseService<T, ID extends Serializable,  M extends BaseMapper<T, ID>> {

	M getDomainMapper();

	/**
	 * 通过ID查询唯一，如果实体类未声明ID，则不可使用此方法
	 * @param id 主键
	 * @return 唯一行
	 */
	default Optional<T> getById(ID id){
		return getDomainMapper().findById(id);
	}

	/**
	 * 通过ID集合查询一组数据，如果实体类未声明ID，则不可使用此方法
	 * @param ids 主键集合
	 * @return 列表
	 */
	default List<T> getByIds(Collection<ID> ids){
		return getDomainMapper().findByIds(ids);
	}

	/**
	 * 查询表所有数据
	 * @return 列表
	 */
	default List<T> getAll(){
		return getDomainMapper().findAll();
	}

	/**
	 * 查询表所有数据，并指定排序方式
	 * @param sort 排序
	 * @return 列表
	 */
	default List<T> getAllAndSort(Sort sort) {
		return getDomainMapper().findAllAndSort(sort);
	}

	/**
	 * 使用实体类作为查询参数，非null值才会参与查询，关联表属性无效
	 * @param example 条件
	 * @return 列表
	 */
	default List<T> getByExample(T example) {
		return getDomainMapper().findByExample(example);
	}

	/**
	 * 使用实体类作为查询参数，并指定排序方式，非null值才会参与查询，关联表属性无效
	 * @param example 条件
	 * @param sort 排序
	 * @return 列表
	 */
	default List<T> getByExampleAndSort(T example, Sort sort) {
		return getDomainMapper().findByExampleAndSort(example, sort);
	}

	/**
	 * 新增一条数据
	 * @param entity 新增实体对象
	 * @return 影响行数
	 */
	default int add(T entity) {
		return getDomainMapper().insert(entity);
	}

	/**
	 * 批量新增，使用单SQL新增方式，支持批量新增的数据库才能使用此方法<br>
	 * 目前支持数据库：MySql、MariaDB、PostgreDB
	 * @param entityList 新增实体对象列表
	 * @return 影响行数
	 */
	default int batchAdd(Collection<T> entityList) {
		return getDomainMapper().batchInsert(entityList);
	}

	/**
	 * 通过ID更新，默认非null值被更新，可以通过@AttributeOptions注解的updateMode设定更新的模式<br>
	 * @param entity 更新对象
	 * @return 影响行数
	 */
	default int updateById(T entity) {
		return getDomainMapper().updateById(entity);
	}

	/**
	 * 通过ID删除单条数据
	 * @param id 主键
	 * @return 影响行数
	 */
	default int deleteById(ID id) {
		return getDomainMapper().deleteById(id);
	}

	/**
	 * 通过ID集合删除批量数据
	 * @param ids 主键集合
	 * @return 影响行数
	 */
	default int deleteByIds(Collection<ID> ids) {
		return getDomainMapper().deleteByIds(ids);
	}

	/**
	 * 动态条件计算行数
	 * @param predicate 条件
	 * @return 影响行数
	 */
	default int countByExample(T example) {
		return getDomainMapper().countByExample(example);
	}
	
	/**
	 * 动态条件计算行数
	 * @param predicate 条件
	 * @return 影响行数
	 */
	default int countByLambdaCriteria(@Param(Constants.CRITERIA)Consumer<LambdaPredicate<T>> predicate) {
		return getDomainMapper().countByLambdaCriteria(predicate);
	}

	/**
	 * 动态条件查询<br>
	 * @param predicate 条件
	 * @return 列表
	 */
	default List<T> getByCriteria(QueryPredicate predicate) {
		return getDomainMapper().findByCriteria(predicate);
	}

	/**
	 * 动态条件查询<br>
	 * @param predicate 条件
	 * @return 列表
	 */
	default List<T> getByCriteria(Consumer<QueryPredicate> predicate) {
		return getDomainMapper().findByCriteria(predicate);
	}
	
	/**
	 * 动态条件查询<br>
	 * @param <E> 元素
	 * @param predicate 条件
	 * @param resultType 结果类
	 * @return 列表
	 */
	default <E> List<E> getByCriteria(@Param(Constants.CRITERIA)Consumer<QueryPredicate> predicate, @Param(Constants.RESULT_TYPE) Class<E> resultType) {
		return getDomainMapper().findByCriteria(predicate, resultType);
	}

	/**
	 * lambda表达式动态条件查询
	 * @param predicate 条件
	 * @return 列表
	 */
	default List<T> getByLambdaCriteria(Consumer<LambdaQueryPredicate<T>> predicate) {
		return getDomainMapper().findByLambdaCriteria(predicate);
	}
	
	/**
	 * 动态条件查询<br>
	 * @param <E> 元素
	 * @param predicate 条件
	 * @param resultType 结果类
	 * @return 列表
	 */
	default <E> List<E> getByLambdaCriteria(@Param(Constants.CRITERIA)Consumer<LambdaQueryPredicate<T>> predicate, @Param(Constants.RESULT_TYPE) Class<E> resultType){
		return getDomainMapper().findByLambdaCriteria(predicate, resultType);
	}
	
	/**
	 * 动态条件更新<br>
	 * 支持关联表条件，但需要确定数据库是否支持关联条件查询进行更新<br>
	 * @param entity 默认非null值被更新，可以通过@AttributeOptions注解的updateMode设定更新的模式
	 * @param predicate Where条件
	 * @return 影响行数
	 */
	default int updateByCriteria(T entity, Predicate predicate) {
		return getDomainMapper().updateByCriteria(entity, predicate);
	}
	
	/**
	 * 动态条件更新<br>
	 * 支持关联表条件，但需要确定数据库是否支持关联条件查询进行更新<br>
	 * @param entity 默认非null值被更新，可以通过@AttributeOptions注解的updateMode设定更新的模式
	 * @param predicate Where条件
	 * @return 影响行数
	 */
	default int updateByCriteria(T entity, Consumer<Predicate> predicate) {
		return getDomainMapper().updateByCriteria(entity, predicate);
	}
	
	/**
	 * 动态条件更新<br>
	 * @param entity 默认非null值被更新，可以通过@AttributeOptions注解的updateMode设定更新的模式
	 * @param predicate Where条件
	 * @return 影响行数
	 */
	default int updateByLambdaCriteria(T entity, Consumer<LambdaPredicate<T>> predicate) {
		return getDomainMapper().updateByLambdaCriteria(entity, predicate);
	}
	
	/**
	 * 动态条件删除<br>
	 * @param predicate 条件
	 * @return 影响行数
	 */
	default int deleteByCriteria(Predicate predicate) {
		return getDomainMapper().deleteByCriteria(predicate);
	}
	
	/**
	 * 动态条件删除<br>
	 * @param predicate 条件
	 * @return 影响行数
	 */
	default int deleteByCriteria(Consumer<Predicate> predicate) {
		return getDomainMapper().deleteByCriteria(predicate);
	}
	
	/**
	 * 动态条件删除<br>
	 * @param predicate 条件
	 * @return 影响行数
	 */
	default int deleteByLambdaCriteria(Consumer<LambdaPredicate<T>> predicate) {
		return getDomainMapper().deleteByLambdaCriteria(predicate);
	}
	
	/**
	 * 动态统计数据
	 * @param predicate 条件
	 * @return 统计数据列表
	 */
	default List<Map<String, Object>> statisticByCriteria(Consumer<StatisticPredicate> predicate) {
		return getDomainMapper().statisticByCriteria(predicate);
	}
	
	/**
	 * 动态统计数据
	 * @param <E> 每行数据的接收类
	 * @param predicate 条件
	 * @param resultType 结果类
	 * @return 统计数据列表
	 */
	default <E> List<E> statisticByCriteria(@Param(Constants.CRITERIA) Consumer<StatisticPredicate> predicate, @Param(Constants.RESULT_TYPE) Class<E> resultType){
		return getDomainMapper().statisticByCriteria(predicate, resultType);
	}
	
	/**
	 * 动态统计数据
	 * @param predicate 条件
	 * @return 统计数据列表
	 */
	default List<Map<String, Object>> statisticByLambdaCriteria(@Param(Constants.CRITERIA) Consumer<LambdaStatisticPredicate<T>> predicate) {
		return getDomainMapper().statisticByLambdaCriteria(predicate);
	}
	
	/**
	 * 动态统计数据
	 * @param <E> 每行数据的接收类
	 * @param predicate 条件
	 * @param resultType 结果类
	 * @return 统计数据列表
	 */
	default <E> List<E> statisticByLambdaCriteria(@Param(Constants.CRITERIA) Consumer<LambdaStatisticPredicate<T>> predicate, @Param(Constants.RESULT_TYPE) Class<E> resultType) {
		return getDomainMapper().statisticByLambdaCriteria(predicate, resultType);
	}
	
}
