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
package com.yuehuanghun.mybatis.milu.id.tablekey;

import javax.persistence.TableGenerator;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

import com.yuehuanghun.mybatis.milu.tool.StringUtils;

public class TableKeySelectSqlSource implements SqlSource{
	private final Configuration configuration;
	private final TableGenerator tableGenerator;
	private final BoundSql boundSql;

	public TableKeySelectSqlSource(Configuration configuration, TableGenerator tableGenerator) {
		super();
		this.configuration = configuration;
		this.tableGenerator = tableGenerator;
		this.boundSql = initBoundSql();
	}
	
	private BoundSql initBoundSql() {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT ").append(tableGenerator.valueColumnName()).append(" FROM ");
		if(StringUtils.isNotBlank(tableGenerator.schema())) {
			sqlBuilder.append(tableGenerator.schema()).append(".");
		}
		sqlBuilder.append(tableGenerator.table());
		if(StringUtils.isNotBlank(tableGenerator.pkColumnName()) && StringUtils.isNotBlank(tableGenerator.pkColumnValue())) {
			sqlBuilder.append(" WHERE ").append(tableGenerator.pkColumnName()).append(" = '").append(tableGenerator.pkColumnValue()).append("'");
		}
		return configuration.getLanguageDriver(XMLLanguageDriver.class).createSqlSource(configuration, sqlBuilder.toString(), Object.class).getBoundSql(null);
	}

	@Override
	public BoundSql getBoundSql(Object parameterObject) {
		return boundSql;
	}

}
