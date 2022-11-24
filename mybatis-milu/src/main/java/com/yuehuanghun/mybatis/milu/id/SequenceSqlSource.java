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

import javax.persistence.SequenceGenerator;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;

import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;

public class SequenceSqlSource implements SqlSource {
	private final SequenceGenerator sequenceGenerator;
	private final MiluConfiguration configuration;
	
	public SequenceSqlSource(MiluConfiguration configuration, SequenceGenerator sequenceGenerator) {
		this.configuration = configuration;
		this.sequenceGenerator = sequenceGenerator;
	}

	@Override
	public BoundSql getBoundSql(Object parameterObject) {
		
		StringBuilder sqlBuilder = new StringBuilder("SELECT ");
		sqlBuilder.append(sequenceGenerator.sequenceName()).append(".nextval FROM ");
		SqlBuildingHelper.appendSchema(sqlBuilder, sequenceGenerator.schema(), configuration);
		sqlBuilder.append("dual");
		
		return configuration.getLanguageDriver(XMLLanguageDriver.class).createSqlSource(configuration, sqlBuilder.toString(), Object.class).getBoundSql(parameterObject);
	}

}
