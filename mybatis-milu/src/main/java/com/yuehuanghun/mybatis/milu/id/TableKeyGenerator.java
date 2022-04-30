/*
 * Copyright 2020-2022 the original author or authors.
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

	private final ReentrantLock rLock = new ReentrantLock();
	
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
		private volatile long maxKey = 0;
		private static final long MAX_WAIT_TIMEOUT_MILLS = 30000L;
		
		public Long getNextKey() {
			Long nextKey = doGetNextKey();
			if(nextKey != null) {
				return nextKey;
			}
			long startTime = System.currentTimeMillis();
			while(true) {
				fetchNextBatchKey();
				nextKey = doGetNextKey();
				if(nextKey != null) {
					return nextKey;
				}
				if(System.currentTimeMillis() - startTime > MAX_WAIT_TIMEOUT_MILLS) {
					throw new RuntimeException("获取主键超时");
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					//
				}
			}
		}
		
		private Long doGetNextKey() {
			if(curKey.get() < maxKey) {
				long key = curKey.incrementAndGet();
				if(key > maxKey) {
					return null;
				}
				
				return key;
			}
			return null;
		}
		
		private void fetchNextBatchKey() {
			if(rLock.tryLock()) {
				Executor selectExecutor = null;
				Executor updateExecutor = null;
				try {
					if(curKey.get() < maxKey) {
						return;
					}
					Configuration configuration = selectStatement.getConfiguration();
					Environment environment = configuration.getEnvironment();
					Transaction transaction = environment.getTransactionFactory().newTransaction(environment.getDataSource(), TransactionIsolationLevel.READ_COMMITTED, false);
					selectExecutor = configuration.newExecutor(transaction, ExecutorType.SIMPLE);
					updateExecutor = configuration.newExecutor(transaction, ExecutorType.SIMPLE);
					
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
					
					int effect = updateExecutor.update(updateStatement, parameter);
					
					if(effect > 0) {
						curKey.set(oldValue.longValue());
						maxKey = newValue.longValue();
						selectExecutor.commit(false);
						updateExecutor.commit(true);
						return;
					}
					return;
				} catch (SQLException e) {
					try {
						updateExecutor.rollback(true);
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					throw new ExecutorException(e);
				} finally {
					rLock.unlock();
					if(selectExecutor != null) {
						selectExecutor.close(false);
					}
					if(updateExecutor != null) {
						updateExecutor.close(false);
					}
				}
			}
			return;
		}
	}
}
