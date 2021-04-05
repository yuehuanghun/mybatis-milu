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

import java.io.Serializable;
import java.sql.Statement;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;

import com.yuehuanghun.mybatis.milu.MiluConfiguration;

public class AssignKeyGenerator implements KeyGenerator{
	private final String identifierGeneratorName;
	private final String keyProperty;
	private final IdGenerateContext idGenerateContext;
	
	public AssignKeyGenerator(String identifierGeneratorName,MiluConfiguration configuration, Class<?> entityClass, String keyProperty) {
		this.identifierGeneratorName = identifierGeneratorName;
		this.keyProperty = keyProperty;
		idGenerateContext  = new IdGenerateContext(configuration, entityClass);
	}

	@Override
	public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
		final MiluConfiguration configuration = (MiluConfiguration) ms.getConfiguration();
		final MetaObject metaParam = ms.getConfiguration().newMetaObject(parameter);
		
		if(!configuration.hasIdentifierGenerator(identifierGeneratorName)) {
			throw new ExecutorException(String.format("不存在名为%s的ID构造器", identifierGeneratorName));
		}
		
		IdentifierGenerator identifierGenerator = configuration.getIdentifierGenerator(identifierGeneratorName);
		Serializable identifierValue = identifierGenerator.generate(idGenerateContext);
		setValue(metaParam, keyProperty, identifierValue);
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
