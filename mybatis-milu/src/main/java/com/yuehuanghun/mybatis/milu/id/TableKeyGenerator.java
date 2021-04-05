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
package com.yuehuanghun.mybatis.milu.id;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;

public class TableKeyGenerator implements KeyGenerator {
	
	private MappedStatement selectStatement;
	
	private MappedStatement updateStatement;
	
	private int capacity;
	
	private KeyManager keyManager = new KeyManager();

	public TableKeyGenerator(MappedStatement selectStatement, MappedStatement updateStatement, int capacity) {
		this.selectStatement = selectStatement;
		this.updateStatement = updateStatement;
		this.capacity = capacity <= 0 ? 1 : capacity;
	}

	@Override
	public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
		final Configuration configuration = ms.getConfiguration();
		final MetaObject metaParam = configuration.newMetaObject(parameter);

		setValue(metaParam, selectStatement.getKeyProperties()[0], keyManager.getNextKey());
	}

	@Override
	public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
		// 不处理
	}
	
	private void setValue(MetaObject metaParam, String property, Object value) {
	    if (metaParam.hasSetter(property)) {
	      metaParam.setValue(property, value);
	    } else {
	      throw new ExecutorException("No setter found for the keyProperty '" + property + "' in " + metaParam.getOriginalObject().getClass().getName() + ".");
	    }
	}

	private class KeyManager {
		private AtomicLong curKey = new AtomicLong();
		private AtomicLong maxKey = new AtomicLong();
		private ReentrantLock rLock = new ReentrantLock();
		
		public Long getNextKey() {
			Long nextKey = doGetNextKey();
			while(nextKey == null) {
				fetchNextBatchKey();
				nextKey = doGetNextKey();
			}
			return nextKey;
		}
		
		private Long doGetNextKey() {
			if(curKey.get() < maxKey.get()) {
				long key = curKey.incrementAndGet();
				if(key > maxKey.get()) {
					return null;
				}
				
				return key;
			}
			return null;
		}
		
		private boolean fetchNextBatchKey() {
			if(rLock.tryLock()) {
				try {
					Configuration configuration = selectStatement.getConfiguration();
					Environment environment = configuration.getEnvironment();
					Transaction transaction = environment.getTransactionFactory().newTransaction(environment.getDataSource(), TransactionIsolationLevel.READ_COMMITTED, true);
					Executor selectExecutor = configuration.newExecutor(transaction, ExecutorType.SIMPLE);
					List<Object> values = selectExecutor.query(selectStatement, null, RowBounds.DEFAULT,
							Executor.NO_RESULT_HANDLER);
					if(values.size() == 0) {
						throw new ExecutorException("获取表序列数据为空");
					}
					Object value = values.get(0);
					
					Long oldValue = (Long) value;
					Long newValue = oldValue.longValue() + capacity;
					Map<String, Object> parameter = new HashMap<>();
					parameter.put("oldValue", oldValue);
					parameter.put("newValue", newValue);
					
					Executor updateExecutor = updateStatement.getConfiguration().newExecutor(transaction, ExecutorType.SIMPLE);
					int effect = updateExecutor.update(updateStatement, parameter);
					
					if(effect > 0) {
						maxKey.set(newValue);
						curKey.set(oldValue);
						transaction.commit();
						return true;
					}
					return false;
				} catch (SQLException e) {
					throw new RuntimeException(e);
				} finally {
					rLock.unlock();
				}
			}
			return true; //获取锁失败，当作拉取批量key成功
		}
	}
}
