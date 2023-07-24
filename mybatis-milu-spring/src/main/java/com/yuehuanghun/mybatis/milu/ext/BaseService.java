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

package com.yuehuanghun.mybatis.milu.ext;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.yuehuanghun.mybatis.milu.BaseMapper;
import com.yuehuanghun.mybatis.milu.annotation.EntityOptions;
import com.yuehuanghun.mybatis.milu.criteria.LambdaPredicate;
import com.yuehuanghun.mybatis.milu.criteria.LambdaQueryPredicate;
import com.yuehuanghun.mybatis.milu.criteria.LambdaStatisticPredicate;
import com.yuehuanghun.mybatis.milu.criteria.LambdaUpdatePredicate;
import com.yuehuanghun.mybatis.milu.criteria.Patch;
import com.yuehuanghun.mybatis.milu.criteria.Predicate;
import com.yuehuanghun.mybatis.milu.criteria.Predicates;
import com.yuehuanghun.mybatis.milu.criteria.QueryPredicate;
import com.yuehuanghun.mybatis.milu.criteria.StatisticPredicate;
import com.yuehuanghun.mybatis.milu.criteria.UpdatePredicate;
import com.yuehuanghun.mybatis.milu.criteria.lambda.SerializableFunction;
import com.yuehuanghun.mybatis.milu.data.Sort;
import com.yuehuanghun.mybatis.milu.pagehelper.Pageable;

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
	 * 查询表所有数据<br>
	 * 从1.13.0开始，自动过滤逻辑删除数据
	 * @return 列表
	 */
	default List<T> getAll(){
		return getDomainMapper().findAll();
	}

	/**
	 * 查询表所有数据，并指定排序方式<br>
	 * 从1.13.0开始，自动过滤逻辑删除数据
	 * @param sort 排序
	 * @return 列表
	 */
	default List<T> getAll(Sort sort) {
		return getDomainMapper().findAll(sort);
	}

	/**
	 * 查询表所有数据，并指定排序方式
	 * @param sort 排序
	 * @return 列表
	 * @deprecated 使用getAll(Sort sort)方法替代
	 */
	@Deprecated
	default List<T> getAllAndSort(Sort sort) {
		return getDomainMapper().findAllAndSort(sort);
	}

	/**
	 * 使用实体类作为查询参数，非null值才会参与查询<br>
	 * 查询关联表（引用属性）使用{@link EntityOptions}注解对实体类声明<br>
	 * 当有实体逻辑删除属性时，将自动添加查询未删除数据的条件，可通用{@link EntityOptions#filterLogicDeletedData()}设置关闭
	 * @param example 条件
	 * @return 列表
	 */
	default List<T> getByExample(T example) {
		return getDomainMapper().findByExample(example);
	}

	/**
	 * 使用实体类作为查询参数，非null值才会参与查询<br>
	 * 查询关联表（引用属性）使用{@link EntityOptions}注解对实体类声明<br>
	 * 当有实体逻辑删除属性时，将自动添加查询未删除数据的条件，可通用{@link EntityOptions#filterLogicDeletedData()}设置关闭
	 * @param example 条件
	 * @param pageable 分页参数，可null
	 * @return 列表
	 */
	default List<T> getByExample(T example, Pageable pageable){
		return getDomainMapper().findByExample(example, pageable);
	}

	/**
	 * 使用实体类作为查询参数，并指定排序方式，非null值才会参与查询<br>
	 * 查询关联表（引用属性）使用{@link EntityOptions}注解对实体类声明<br>
	 * 从1.12.0开始，当有实体逻辑删除属性时，将自动添加查询未删除数据的条件，可通用{@link EntityOptions#filterLogicDeletedData()}设置关闭
	 * @param example 条件
	 * @param sort 排序，可null
	 * @return 列表
	 */
	default List<T> getByExample(T example, Sort sort) {
		return getDomainMapper().findByExample(example, sort);
	}
	
	/**
	 * 使用实体类作为查询参数，并指定排序方式，非null值才会参与查询<br>
	 * 查询关联表（引用属性）使用{@link EntityOptions}注解对实体类声明<br>
	 * 从1.12.0开始，当有实体逻辑删除属性时，将自动添加查询未删除数据的条件，可通用{@link EntityOptions#filterLogicDeletedData()}设置关闭
	 * @param example 条件
	 * @param sort 排序，可null
	 * @param pageable 分页参数，可null
	 * @return 列表
	 */
	default List<T> getByExample(T example, Sort sort, Pageable pageable) {
		return getDomainMapper().findByExample(example, sort, pageable);
	}
	
	/**
	 * 使用实体类作为查询参数，并指定排序方式，非null值才会参与查询<br>
	 * 查询关联表（引用属性）使用{@link EntityOptions}注解对实体类声明<br>
	 * 从1.12.0开始，当有实体逻辑删除属性时，将自动添加查询未删除数据的条件，可通用{@link EntityOptions#filterLogicDeletedData()}设置关闭
	 * @param example 条件
	 * @param sort 排序，可null
	 * @param pageable 分页参数，可null
	 * @param group 查询引用属性分组，可null，见@EntityOptions注解
	 * @return 列表
	 */
	default List<T> getByExample(T example, Sort sort, Pageable pageable, String group) {
		return getDomainMapper().findByExample(example, sort, pageable, group);
	}

	/**
	 * 使用实体类作为查询参数，非null值才会参与查询<br>
	 * 查询关联表（引用属性）使用{@link EntityOptions}注解对实体类声明<br>
	 * 从1.12.0开始，当有实体逻辑删除属性时，将自动添加查询未删除数据的条件，可通用{@link EntityOptions#filterLogicDeletedData()}设置关闭
	 * 结果集必须是0或1条数据，否则会报错。
	 * @param example 条件
	 * @param pageable 分页参数，可null
	 * @return 列表
	 */
	default T getUniqueByExample(T example, Pageable pageable){
		return getDomainMapper().findUniqueByExample(example, pageable);
	}
	
	/**
	 * 使用实体类作为查询参数，并指定排序方式，非null值才会参与查询<br>
	 * 查询关联表（引用属性）使用{@link EntityOptions}注解对实体类声明<br>
	 * 结果集必须是0或1条数据，否则会报错。<br>
	 * 查询关联表（引用属性）使用{@link EntityOptions}注解对实体类声明<br>
	 * 从1.12.0开始，当有实体逻辑删除属性时，将自动添加查询未删除数据的条件，可通用{@link EntityOptions#filterLogicDeletedData()}设置关闭
	 * @param example 条件
	 * @param sort 排序，可null
	 * @param pageable 分页参数，可null
	 * @return 列表
	 */
	default T getUniqueByExample(T example, Sort sort, Pageable pageable) {
		return getDomainMapper().findUniqueByExample(example, sort, pageable);
	}
	
	/**
	 * 使用实体类作为查询参数，并指定排序方式，非null值才会参与查询<br>
	 * 查询关联表（引用属性）使用{@link EntityOptions}注解对实体类声明<br>
	 * 结果集必须是0或1条数据，否则会报错。<br>
	 * 查询关联表（引用属性）使用{@link EntityOptions}注解对实体类声明<br>
	 * 从1.12.0开始，当有实体逻辑删除属性时，将自动添加查询未删除数据的条件，可通用{@link EntityOptions#filterLogicDeletedData()}设置关闭
	 * @param example 条件
	 * @param sort 排序，可null
	 * @param pageable 分页参数，可null
	 * @param group 查询引用属性分组，可null，见@EntityOptions注解
	 * @return 列表
	 */
	default T getUniqueByExample(T example, Sort sort, Pageable pageable, String group) {
		return getDomainMapper().findUniqueByExample(example, sort, pageable, group);
	}

	/**
	 * 使用实体类作为查询参数，并指定排序方式，非null值才会参与查询，关联表属性无效
	 * @param example 条件
	 * @param sort 排序，可null
	 * @return 列表
	 * @deprecated 使用getByExample(T example, Sort sort)替代
	 */
	@Deprecated
	default List<T> getByExampleAndSort(T example, Sort sort) {
		return getDomainMapper().findByExampleAndSort(example, sort);
	}
	
	/**
	 * 使用实体类作为查询参数，并指定排序方式，非null值才会参与查询，关联表属性无效
	 * @param example 条件
	 * @param sort 排序，可null
	 * @param pageable 分页参数，可null
	 * @return 列表
	 * @deprecated 使用getByExample(T example, Sort sort, Pageable pageable)进行替代
	 */
	@Deprecated
	default List<T> getByExampleAndSort(T example, Sort sort, Pageable pageable) {
		return getDomainMapper().findByExampleAndSort(example, sort, pageable);
	}
	
	/**
	 * 使用实体类作为查询参数，并指定排序方式，非null值才会参与查询，关联表属性无效<br>
	 * 结果集必须是0或1条数据，否则会报错。
	 * @param example 条件
	 * @param sort 排序，可null
	 * @param pageable 分页参数，可null
	 * @return 列表
	 * @deprecated 使用getUniqueByExample(T example, Sort sort, Pageable pageable)进行替代
	 */
	@Deprecated
	default T getUniqueByExampleAndSort(T example, Sort sort, Pageable pageable) {
		return getDomainMapper().findUniqueByExampleAndSort(example, sort, pageable);
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
	 * 批量保存（新增）<br>
	 * batchAdd是单sql模式，而这个是使用mybatis的批处理模式<br>
	 * @param entityList 新增实体对象列表
	 */
	void batchSave(Collection<T> entityList);
	
	/**
	 * 通过主键批量更新
	 * @param entityList 被更新的实体对象列表
	 */
	void batchUpdateById(Collection<T> entityList);
	
	/**
	 * 批量动态条件更新<br>
	 * <br>示例: <br>
	 * Collection&lt;Pair&lt;SomeEntity, Consumer&lt;LambdaUpdatePredicate&lt;SomeEntity&gt;&gt;&gt;&gt; entityAndPredicateList = entityList.stream().map(item -&gt; { <br>
	 *&nbsp;&nbsp;Pair&lt;SomeEntity, Consumer&lt;LambdaUpdatePredicate&lt;SomeEntity&gt;&gt;&gt; pair = Pair.of(item, p -&gt; p.eq(SomeEntity::getId, item.getId()));<br>
	 *&nbsp;&nbsp;return pair;<br>
	 *	}).collect(Collectors.toList());<br><br>
	 *	service.batchUpdateByLambdaCriteria(entityAndPredicateList);<br>
	 * <br><br>
	 * @param entityAndPredicateList 被更新的内容与条件集合
	 */
	void batchUpdateByLambdaCriteria(Collection<Pair<T, Consumer<LambdaUpdatePredicate<T>>>> entityAndPredicateList);

	/**
	 * 通过主键更新，默认非null值被更新，可以通过@AttributeOptions注解的updateMode设定更新的模式<br>
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
	 * 动态条件计算行数<br>
	 * 从1.13.0开始，自动过滤逻辑删除数据
	 * @param example 条件
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
	default int countByLambdaCriteria(Consumer<LambdaPredicate<T>> predicate) {
		return getDomainMapper().countByLambdaCriteria(predicate);
	}

	/**
	 * 动态条件查询<br>
	 * @param predicate 条件，可通过{@link Predicates#queryPredicate()}创建
	 * @return 列表
	 */
	default List<T> getByCriteria(QueryPredicate predicate) {
		return getDomainMapper().findByCriteria(predicate);
	}

	/**
	 * 动态条件查询唯一数据<br>
	 * 结果集必须是0或1条数据，否则会报错。建议使用.limit(1)进行结果集行数限制。
	 * @param predicate 条件，可通过{@link Predicates#queryPredicate()}创建
	 * @return 列表
	 */
	default T getUniqueByCriteria(QueryPredicate predicate) {
		return getDomainMapper().findUniqueByCriteria(predicate);
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
	 * 动态条件查询，多组条件查询结果联合<br>
	 * 如果指定查询列，则各组条件中的查询列需一致并对齐<br>
	 * 可用于OR查询转UNION (ALL)<br>
	 * 不适宜用于分页查询
	 * @param predicates 条件组，组数&gt;=2才有意义
	 * @return 列表
	 */
	default List<T> getByCriteriaUnion(Predicate... predicates) {
		return getDomainMapper().findByCriteriaUnion(predicates);
	}

	/**
	 * 动态条件查询，多组条件查询结果联合<br>
	 * 如果指定查询列，则各组条件中的查询列需一致并对齐<br>
	 * 可用于OR查询转UNION (ALL)<br>
	 * 不适宜用于分页查询
	 * @param predicates 条件组，组数&gt;=2才有意义
	 * @return 列表
	 */
	@SuppressWarnings("unchecked")
	default List<T> getByCriteriaUnion(Consumer<Predicate>... predicates) {
		return getDomainMapper().findByCriteriaUnion(predicates);
	}

	/**
	 * 动态条件查询，多组条件查询结果联合<br>
	 * 如果指定查询列，则各组条件中的查询列需一致并对齐<br>
	 * 可用于OR查询转UNION (ALL)<br>
	 * 不适宜用于分页查询
	 * @param selectAttrNames 指定查询的属性
	 * @param predicates 条件组，组数&gt;=2才有意义
	 * @return 列表
	 */
	default List<T> getByCriteriaUnion(String[] selectAttrNames, Predicate... predicates) {
		return getDomainMapper().findByCriteriaUnion(selectAttrNames, predicates);
	}

	/**
	 * 动态条件查询，多组条件查询结果联合<br>
	 * 如果指定查询列，则各组条件中的查询列需一致并对齐<br>
	 * 可用于OR查询转UNION (ALL)<br>
	 * 不适宜用于分页查询
	 * @param selectAttrNames 指定查询的属性
	 * @param predicates 条件组，组数&gt;=2才有意义
	 * @return 列表
	 */
	@SuppressWarnings("unchecked")
	default List<T> getByCriteriaUnion(String[] selectAttrNames, Consumer<Predicate>... predicates) {
		return getDomainMapper().findByCriteriaUnion(selectAttrNames, predicates);
	}
	

	/**
	 * 动态条件查询唯一数据<br>
	 * 结果集必须是0或1条数据，否则会报错。建议使用.limit(1)进行结果集行数限制。
	 * @param predicate 条件
	 * @return 列表
	 */
	default T getUniqueByCriteria(Consumer<QueryPredicate> predicate) {
		return getDomainMapper().findUniqueByCriteria(predicate);
	}
	
	/**
	 * 动态条件查询<br>
	 * @param <E> 元素
	 * @param predicate 条件
	 * @param resultType 结果类
	 * @return 列表
	 */
	default <E> List<E> getByCriteria(Consumer<QueryPredicate> predicate, Class<E> resultType) {
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
	 * 动态条件查询，多组条件查询结果联合<br>
	 * 如果指定查询列，则各组条件中的查询列需一致并对齐<br>
	 * 可用于OR查询转UNION (ALL)<br>
	 * 不适宜用于分页查询
	 * @param predicates 条件组，组数&gt;=2才有意义
	 * @return 列表
	 */
	@SuppressWarnings("unchecked")
	default List<T> getByLambdaCriteriaUnion(Consumer<LambdaPredicate<T>>... predicates) {
		return getDomainMapper().findByLambdaCriteriaUnion(predicates);
	}

	/**
	 * 动态条件查询，多组条件查询结果联合<br>
	 * 如果指定查询列，则各组条件中的查询列需一致并对齐<br>
	 * 可用于OR查询转UNION (ALL)<br>
	 * 不适宜用于分页查询
	 * @param selectAttrNames 指定查询的属性
	 * @param predicates 条件组，组数&gt;=2才有意义
	 * @return 列表
	 */
	@SuppressWarnings("unchecked")
	default List<T> getByLambdaCriteriaUnion(String[] selectAttrNames, Consumer<LambdaPredicate<T>>... predicates) {
		return getDomainMapper().findByLambdaCriteriaUnion(selectAttrNames, predicates);
	}
	

	/**
	 * lambda表达式动态条件查询唯一数据<br>
	 *  结果集必须是0或1条数据，否则会报错。建议使用.limit(1)进行结果集行数限制。
	 * @param predicate 条件
	 * @return 列表
	 */
	default T getUniqueByLambdaCriteria(Consumer<LambdaQueryPredicate<T>> predicate) {
		return getDomainMapper().findUniqueByLambdaCriteria(predicate);
	}
	
	/**
	 * 动态条件查询<br>
	 * @param <E> 元素
	 * @param predicate 条件
	 * @param resultType 结果类
	 * @return 列表
	 */
	default <E> List<E> getByLambdaCriteria(Consumer<LambdaQueryPredicate<T>> predicate, Class<E> resultType){
		return getDomainMapper().findByLambdaCriteria(predicate, resultType);
	}
	
	/**
	 * 动态条件更新<br>
	 * 支持关联表条件，但需要确定数据库是否支持关联条件查询进行更新<br>
	 * @param entity 默认非null值被更新，可以通过@AttributeOptions注解的updateMode设定更新的模式
	 * @param predicate 动态条件，可通过{@link Predicates#updatePredicate()}创建
	 * @return 影响行数
	 */
	default int updateByCriteria(T entity, UpdatePredicate predicate) {
		return getDomainMapper().updateByCriteria(entity, predicate);
	}
	
	/**
	 * 动态条件更新<br>
	 * 支持关联表条件，但需要确定数据库是否支持关联条件查询进行更新<br>
	 * @param entity 默认非null值被更新，可以通过@AttributeOptions注解的updateMode设定更新的模式
	 * @param predicate Where条件
	 * @return 影响行数
	 */
	default int updateByCriteria(T entity, Consumer<UpdatePredicate> predicate) {
		return getDomainMapper().updateByCriteria(entity, predicate);
	}
	
	/**
	 * 动态条件更新<br>
	 * @param entity 默认非null值被更新，可以通过@AttributeOptions注解的updateMode设定更新的模式
	 * @param predicate Where条件
	 * @return 影响行数
	 */
	default int updateByLambdaCriteria(T entity, Consumer<LambdaUpdatePredicate<T>> predicate) {
		return getDomainMapper().updateByLambdaCriteria(entity, predicate);
	}
	
	/**
	 * 动态条件删除<br>
	 * @param predicate 条件，可通过{@link Predicates#predicate()}创建
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
	default <E> List<E> statisticByCriteria(Consumer<StatisticPredicate> predicate, Class<E> resultType){
		return getDomainMapper().statisticByCriteria(predicate, resultType);
	}
	
	/**
	 * 动态统计数据
	 * @param predicate 条件
	 * @return 统计数据列表
	 */
	default List<Map<String, Object>> statisticByLambdaCriteria(Consumer<LambdaStatisticPredicate<T>> predicate) {
		return getDomainMapper().statisticByLambdaCriteria(predicate);
	}
	
	/**
	 * 动态统计数据
	 * @param <E> 每行数据的接收类
	 * @param predicate 条件
	 * @param resultType 结果类
	 * @return 统计数据列表
	 */
	default <E> List<E> statisticByLambdaCriteria(Consumer<LambdaStatisticPredicate<T>> predicate, Class<E> resultType) {
		return getDomainMapper().statisticByLambdaCriteria(predicate, resultType);
	}

	/**
	 * 单属性更新<br>
	 * 除了指定属性外，@Version及@AttributeOptions中声明为更新时自动填充的属性也会被更新<br>
     * 指定更新属性为@Version属性是无意义的<br>
     * 指定更新属性如果为@AttributeOptions声明为更新时自动填充的属性，则以指定值为准
	 * @param attrName 属性名，本实体中的属性，不能为关联实体的属性。注意不要直接填字段名。
	 * @param value 更新值，可以为null值
	 * @param id 主键
	 * @return 影响行数
	 */
	default int updateAttrById(String attrName, Object value, ID id) {
		return getDomainMapper().updateAttrById(attrName, value, id);
	}

	/**
	 * 单属性更新<br>
	 * 除了指定属性外，@Version及@AttributeOptions中声明为更新时自动填充的属性也会被更新<br>
     * 指定更新属性为@Version属性是无意义的<br>
     * 指定更新属性如果为@AttributeOptions声明为更新时自动填充的属性，则以指定值为准
	 * @param attrNameGetter 实体类属性名getter函数式。
	 * @param value 更新值，可以为null值
	 * @param id 主键
	 * @return 影响行数
	 */
	default int updateAttrById(SerializableFunction<T, ?> attrNameGetter, Object value, ID id) {
		return getDomainMapper().updateAttrById(attrNameGetter, value, id);
	}
	

	/**
	 * 单属性更新<br>
	 * 除了指定属性外，@Version及@AttributeOptions中声明为更新时自动填充的属性也会被更新<br>
	 * 指定更新属性为@Version属性是无意义的<br>
	 * 指定更新属性如果为@AttributeOptions声明为更新时自动填充的属性，则以指定值为准
	 * 
	 * @param attrName  实体类属性名。
	 * @param value     更新值，可以为null值
	 * @param predicate 条件，可通过{@link Predicates#updatePredicate()}创建
	 * @return 影响行数
	 */
	default int updateAttrByCriteria(String attrName, Object value, UpdatePredicate predicate) {
		return getDomainMapper().updateAttrByCriteria(attrName, value, predicate);
	}
	
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
	default int updateAttrByCriteria(String attrName, Object value, Consumer<UpdatePredicate> predicate) {
		return getDomainMapper().updateAttrByCriteria(attrName, value, predicate);
	}
	
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
	default int updateAttrByLambdaCriteria(SerializableFunction<T, ?> attrNameGetter, Object value, Consumer<LambdaUpdatePredicate<T>> predicate) {
		return getDomainMapper().updateAttrByLambdaCriteria(attrNameGetter, value, predicate);
	}
	

	/**
	 * 逻辑删除<br>
	 * 如果实体有乐观锁，依然会使用<br>
	 * 如果有有实体属性在更新时自动填充，依然会生效
	 * @param id 主键
	 * @return 受影响的行数
	 */
	default int logicDeleteById(ID id) {
		return getDomainMapper().logicDeleteById(id);
	}
	
	/**
	 * 逻辑删除<br>
	 * 如果实体有乐观锁，依然会使用<br>
	 * 如果有有实体属性在更新时自动填充，依然会生效
	 * @param ids 主键集合，不可为空
	 * @return 受影响的行数
	 */
	default int logicDeleteByIds(Collection<ID> ids) {
		return getDomainMapper().logicDeleteByIds(ids);
	}
	
	/**
	 * 动态条件逻辑删除
	 * @param predicate 条件
	 * @return 影响行数
	 */
	default int logicDeleteByCriteria(Consumer<UpdatePredicate> predicate) {
		return getDomainMapper().logicDeleteByCriteria(predicate);
	}
	
	/**
	 * 动态条件逻辑删除
	 * @param predicate 条件
	 * @return 影响行数
	 */
	default int logicDeleteByLambdaCriteria(Consumer<LambdaUpdatePredicate<T>> predicate) {
		return getDomainMapper().logicDeleteByLambdaCriteria(predicate);
	}
	
	/**
	 * 恢复已被逻辑删除的状态<br>
	 * 如果有有实体属性在更新时自动填充，依然会生效
	 * @param id 主键
	 * @return 受影响的行数
	 */
	default int resumeLogicDeletedById(ID id) {
		return getDomainMapper().resumeLogicDeletedById(id);
	}
	
	/**
	 * 恢复已被逻辑删除的状态<br>
	 * 如果有有实体属性在更新时自动填充，依然会生效
	 * @param ids 主键集合，不可为空
	 * @return 受影响的行数
	 */
	default int resumeLogicDeletedByIds(Collection<ID> ids) {
		return resumeLogicDeletedByIds(ids);
	}
	
	/**
	 * 动态条件恢复已逻辑删除的状态
	 * @param predicate 条件
	 * @return 影响行数
	 */
	default int resumeLogicDeletedByCriteria(Consumer<UpdatePredicate> predicate) {
		return resumeLogicDeletedByCriteria(predicate);
	}
	
	/**
	 * 动态条件恢复已逻辑删除的状态
	 * @param predicate 条件
	 * @return 影响行数
	 */
	default int resumeLogicDeletedByLambdaCriteria(Consumer<LambdaUpdatePredicate<T>> predicate) {
		return resumeLogicDeletedByLambdaCriteria(predicate);
	}
	
	/**
	 * 动态条件查询字段最大值，如果匹配行数为0，则返回值为null
	 * @param <R> 返回值类型
	 * @param attrNameGetter 被统计的实体属性getter函数式，例如User::getAge
	 * @param predicate 动态查询条件
	 * @return 最大值
	 */
	default <R> R maxByLambdaCriteria(SerializableFunction<T, R> attrNameGetter, Consumer<LambdaPredicate<T>> predicate) {
		return getDomainMapper().maxByLambdaCriteria(attrNameGetter, predicate);
	}
	
	/**
	 * 动态条件查询字段最小值，如果匹配行数为0，则返回值为null
	 * @param <R> 返回值类型
	 * @param attrNameGetter 被统计的实体属性getter函数式，例如User::getAge
	 * @param predicate 动态查询条件
	 * @return 最小值
	 */
	default <R> R minByLambdaCriteria(SerializableFunction<T, R> attrNameGetter, Consumer<LambdaPredicate<T>> predicate) {
		return getDomainMapper().minByLambdaCriteria(attrNameGetter, predicate);
	}
	
	/**
	 * 动态条件查询字段值总和，如果匹配行数为0，则返回值为null
	 * @param <R> 返回值类型
	 * @param attrNameGetter 被统计的实体属性getter函数式，例如User::getAge
	 * @param predicate 动态查询条件
	 * @return 值总和
	 */
	default <R> R sumByLambdaCriteria(SerializableFunction<T, R> attrNameGetter, Consumer<LambdaPredicate<T>> predicate) {
		return getDomainMapper().sumByLambdaCriteria(attrNameGetter, predicate);
	}

	/**
	 * 补丁式更新，以id为条件
	 * @param patch 需要被更新的属性及其值，所有指定属性都会被更新，不管值是否为null。
	 * @param id 主键值，值不能为空
	 * @return 影响行数
	 */
	default int updatePatchById(Patch patch, ID id) {
		return getDomainMapper().updatePatchById(patch, id);
	}

	/**
	 * 补丁式更新，以id为条件
	 * @param patch 需要被更新的属性及其值，所有指定属性都会被更新，不管值是否为null。
	 * @param ids 主键值集合，值不能为空
	 * @return 影响行数
	 */
	default int updatePatchByIds(Patch patch, Collection<ID> ids) {
		return getDomainMapper().updatePatchByIds(patch, ids);
	}
	
	/**
	 * 补丁式更新，使用动态条件
	 * @param patch 需要被更新的属性及其值，所有指定属性都会被更新，不管值是否为null。
	 * @param predicate 动态条件
	 * @return
	 */
	default int updatePatchByCriteria(Patch patch, Predicate predicate) {
		return getDomainMapper().updatePatchByCriteria(patch, predicate);
	}

	/**
	 * 补丁式更新，使用动态条件
	 * @param patch 需要被更新的属性及其值，所有指定属性都会被更新，不管值是否为null。
	 * @param predicate 动态条件
	 * @return
	 */
	default int updatePatchByCriteria(Patch patch, Consumer<Predicate> predicate) {
		return getDomainMapper().updatePatchByCriteria(patch, predicate);
	}

	/**
	 * 补丁式更新，使用动态条件
	 * @param patch 需要被更新的属性及其值，所有指定属性都会被更新，不管值是否为null。
	 * @param predicate 动态条件
	 * @return
	 */
	default int updatePatchByLambdaCriteria(Patch patch, Consumer<LambdaPredicate<T>> predicate) {
		return getDomainMapper().updatePatchByLambdaCriteria(patch, predicate);
	}
}
