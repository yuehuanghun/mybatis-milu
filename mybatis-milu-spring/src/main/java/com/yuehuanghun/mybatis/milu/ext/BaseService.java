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
import java.util.Optional;
import java.util.function.Consumer;

import com.yuehuanghun.mybatis.milu.BaseMapper;
import com.yuehuanghun.mybatis.milu.criteria.LambdaPredicate;
import com.yuehuanghun.mybatis.milu.criteria.LambdaQueryPredicate;
import com.yuehuanghun.mybatis.milu.criteria.Predicate;
import com.yuehuanghun.mybatis.milu.criteria.QueryPredicate;
import com.yuehuanghun.mybatis.milu.data.Sort;

/**
 * 将BaseMaper的功能以Service的形式提供
 * @author yuehuanghun
 *
 * @param <T> 实体类
 * @param <ID> 实体类的ID类型
 */
public interface BaseService<T, ID extends Serializable,  M extends BaseMapper<T, ID>> {

	M getDomainMapper();

	default Optional<T> getById(ID id){
		return getDomainMapper().findById(id);
	}
	
	default List<T> getByIds(Collection<ID> ids){
		return getDomainMapper().findByIds(ids);
	}
	
	default List<T> getAll(){
		return getDomainMapper().findAll();
	}
	
	default List<T> getAllAndSort(Sort sort) {
		return getDomainMapper().findAllAndSort(sort);
	}
	
	default List<T> getByExample(T example) {
		return getDomainMapper().findByExample(example);
	}
	
	default List<T> getByExampleAndSort(T example, Sort sort) {
		return getDomainMapper().findByExampleAndSort(example, sort);
	}
	
	default int add(T entity) {
		return getDomainMapper().insert(entity);
	}
	
	default int batchAdd(Collection<T> entityList) {
		return getDomainMapper().batchInsert(entityList);
	}
	
	default int updateById(T entity) {
		return getDomainMapper().updateById(entity);
	}
	
	default int deleteById(ID id) {
		return getDomainMapper().deleteById(id);
	}
	
	default int deleteByIds(Collection<ID> ids) {
		return getDomainMapper().deleteByIds(ids);
	}
	
	default int countByExample(T example) {
		return getDomainMapper().countByExample(example);
	}
	
	default List<T> getByCriteria(QueryPredicate predicate) {
		return getDomainMapper().findByCriteria(predicate);
	}
	
	default List<T> getByCriteria(Consumer<QueryPredicate> predicate) {
		return getDomainMapper().findByCriteria(predicate);
	}
	
	default List<T> getByLambdaCriteria(Consumer<LambdaQueryPredicate<T>> predicate) {
		return getDomainMapper().findByLambdaCriteria(predicate);
	}
	
	default int updateByCriteria(T entity, Predicate predicate) {
		return getDomainMapper().updateByCriteria(entity, predicate);
	}
	
	default int updateByCriteria(T entity, Consumer<Predicate> predicate) {
		return getDomainMapper().updateByCriteria(entity, predicate);
	}
	
	default int updateByLambdaCriteria(T entity, Consumer<LambdaPredicate<T>> predicate) {
		return getDomainMapper().updateByLambdaCriteria(entity, predicate);
	}
	
	default int deleteByCriteria(Predicate predicate) {
		return getDomainMapper().deleteByCriteria(predicate);
	}
	
	default int deleteByCriteria(Consumer<Predicate> predicate) {
		return getDomainMapper().deleteByCriteria(predicate);
	}
	
	default int deleteByLambdaCriteria(Consumer<LambdaPredicate<T>> predicate) {
		return getDomainMapper().deleteByLambdaCriteria(predicate);
	}
}
