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
package com.yuehuanghun.mybatis.milu.generic;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.javassist.scopedpool.SoftValueHashMap;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;

import com.yuehuanghun.mybatis.milu.mapping.ResultMapHelper;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;

public class GenericProviderSqlSource implements SqlSource {
	private final Configuration configuration;
	private final LanguageDriver languageDriver;
	private final GenericProviderSql providerSql;
	private final GenericProviderContext providerContext;
	private final Map<String, SqlSource> sqlSourceCache = new SoftValueHashMap<>();

	public GenericProviderSqlSource(Configuration configuration, GenericProviderSql providerSql, Class<?> mapperType,
			Method mapperMethod, KeyGenerator keyGenerator, Entity entity) {
		this.configuration = configuration;
		this.languageDriver = configuration.getLanguageDriver(null);
		this.providerContext = new GenericProviderContext(mapperType, mapperMethod, configuration, keyGenerator, entity);
		this.providerSql = providerSql;
	}

	@Override
	public BoundSql getBoundSql(Object parameterObject) {
		SqlSource sqlSource = createSqlSource(parameterObject);
		return sqlSource.getBoundSql(parameterObject);
	}

	private SqlSource createSqlSource(Object parameterObject) {
		ResultMapHelper.clearResultType(); //清除动态的resultType
		try {
			String sql = providerSql.provideSql(providerContext, parameterObject);

			return sqlSourceCache.computeIfAbsent(sql, (key) -> {
				Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
				return languageDriver.createSqlSource(configuration, sql, parameterType);
			});
		} catch (Exception e) {
			throw new BuilderException(e);
		}
	}
}
