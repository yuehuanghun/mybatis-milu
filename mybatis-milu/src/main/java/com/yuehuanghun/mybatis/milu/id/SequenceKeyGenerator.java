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

import java.sql.Statement;
import java.util.List;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.RowBounds;

public class SequenceKeyGenerator implements KeyGenerator {
	private final MappedStatement keyStatement;

	public SequenceKeyGenerator(MappedStatement keyStatement) {
		this.keyStatement = keyStatement;
	}

	@Override
	public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
		try {
			if (parameter != null && keyStatement != null && keyStatement.getKeyProperties() != null) {
				String[] keyProperties = keyStatement.getKeyProperties();
				final Configuration configuration = ms.getConfiguration();
				final MetaObject metaParam = configuration.newMetaObject(parameter);
				Executor keyExecutor = configuration.newExecutor(executor.getTransaction(), ExecutorType.SIMPLE);
				List<Object> values = keyExecutor.query(keyStatement, null, RowBounds.DEFAULT,
						Executor.NO_RESULT_HANDLER);
				if (values.size() == 0) {
					throw new ExecutorException("SelectKey returned no data.");
				} else if (values.size() > 1) {
					throw new ExecutorException("SelectKey returned more than one value.");
				} else {
					if (keyProperties.length != 1) {
						throw new ExecutorException("keyProperties not only one value.");
					} else {
						setValue(metaParam, keyProperties[0], values.get(0));
					}
				}
			}
		} catch (ExecutorException e) {
			throw e;
		} catch (Exception e) {
			throw new ExecutorException("Error selecting key or setting result to parameter object. Cause: " + e, e);
		}
	}

	@Override
	public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
		// do nothing
	}

	private void setValue(MetaObject metaParam, String property, Object value) {
	    if (metaParam.hasSetter(property)) {
	      metaParam.setValue(property, value);
	    } else {
	      throw new ExecutorException("No setter found for the keyProperty '" + property + "' in " + metaParam.getOriginalObject().getClass().getName() + ".");
	    }
	}
}
