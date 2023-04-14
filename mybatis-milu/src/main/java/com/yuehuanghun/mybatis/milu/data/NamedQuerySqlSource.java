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
package com.yuehuanghun.mybatis.milu.data;

import java.util.Map;

import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

import com.github.pagehelper.PageHelper;
import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.pagehelper.Pageable;

public class NamedQuerySqlSource implements SqlSource {
	private final SqlSource delegate;
	private final Entity entity;
	private final MiluConfiguration configuration;
	
	public NamedQuerySqlSource(MiluConfiguration configuration, String script, Class<?> parameterType, Entity entity) {
		delegate = createSqlSource(configuration, script, parameterType);
		this.entity = entity;
		this.configuration = configuration;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public BoundSql getBoundSql(Object parameterObject) {
		if(parameterObject instanceof Map) {
			Map paramMap = (Map) parameterObject;
			for(Object obj : paramMap.values()) {
				if(obj instanceof Pageable) {
					Pageable page = (Pageable) obj;
					PageHelper.startPage(page.getPageNum(), page.getPageSize(), page.isCount());
					break;
				}
			}
		}
		
		SqlBuildingHelper.convertLocalPageOrder(entity, configuration); //转换排序中的属性为列名
		
		try {
			return delegate.getBoundSql(parameterObject);
		} catch (Exception e) {
			PageHelper.clearPage(); //防止异常导致ThreadLocal中的Page未被使用并清除
			throw e;
		}
	}
	
	private SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
		try {
			return configuration.getLanguageDriver(XMLLanguageDriver.class).createSqlSource(configuration, script, parameterType);
		} catch (Exception e) {
			throw new BuilderException(e);
		}
	}
}
