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
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

import com.yuehuanghun.mybatis.milu.BaseMapper;
import com.yuehuanghun.mybatis.milu.criteria.LambdaUpdatePredicate;
import com.yuehuanghun.mybatis.milu.ext.Supports.MapperMethod;

public class BaseServiceImpl<T, ID extends Serializable, M extends BaseMapper<T, ID>> implements BaseService<T, ID, M> {

	@Autowired
	protected M domainMapper;
	
	@Override
	public M getDomainMapper() {
		return domainMapper;
	}

	@Override
	@Transactional
	public void batchSave(Collection<T> entityList) {
		batchOps(entityList, "insert", (tool, entity) -> {
			tool.getKey().insert(tool.getValue().getCommand().getName(), tool.getValue().getMethod().convertArgsToSqlCommandParam(new Object[] {entity}));
		});
	}

	@Override
	@Transactional
	public void batchUpdateById(Collection<T> entityList) {
		batchOps(entityList, "updateById", (tool, entity) -> {
			tool.getKey().update(tool.getValue().getCommand().getName(), tool.getValue().getMethod().convertArgsToSqlCommandParam(new Object[] {entity}));
		});
	}

	@Override
	@Transactional
	public void batchUpdateByLambdaCriteria(
			Collection<Pair<T, Consumer<LambdaUpdatePredicate<T>>>> entityAndPredicateList) {
		batchOps(entityAndPredicateList, "updateByLambdaCriteria", (tool, pair) -> {
			tool.getKey().update(tool.getValue().getCommand().getName(), tool.getValue().getMethod().convertArgsToSqlCommandParam(new Object[] {pair.getKey(), pair.getValue()}));
		});
	}
	
	private <E> void batchOps(Collection<E> coll, String baseMethodName, BiConsumer<Pair<SqlSession, MapperMethod>, E> consumer) {
		if(CollectionUtils.isEmpty(coll)) {
			return;
		}
		
		SqlSessionFactory sqlSessionFactory = Supports.getSqlSessionFactory(domainMapper);
		SqlSession batchSqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
		MapperMethod mapperMethod = Supports.getMapperMethod(domainMapper, baseMethodName);
		int count = 0;
		int max = getBatchFlushSize();
		Pair<SqlSession, MapperMethod> tool = Pair.of(batchSqlSession, mapperMethod);
		
		try {
			for(E ele : coll) {
				consumer.accept(tool, ele);
				count++;
				if(count >= max) {
					batchSqlSession.flushStatements();
					count = 0;
				}
			}
		} finally {
			batchSqlSession.flushStatements();
			
			if(!TransactionSynchronizationManager.isActualTransactionActive()) {
				batchSqlSession.commit();
			}
			
			batchSqlSession.close();
		}
	}

	/**
	 * 批量处理中提交缓冲区的阈值
	 * @return 阈值
	 */
	protected int getBatchFlushSize() {
		return 500;
	}
	
}
