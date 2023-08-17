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
package com.yuehuanghun.mybatis.milu.dialect.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.yuehuanghun.mybatis.milu.criteria.FulltextMode;
import com.yuehuanghun.mybatis.milu.data.Part.Type;
import com.yuehuanghun.mybatis.milu.data.Sort.NullHandling;
import com.yuehuanghun.mybatis.milu.dialect.AbstractDialect;
import com.yuehuanghun.mybatis.milu.exception.OrmBuildingException;
import com.yuehuanghun.mybatis.milu.exception.OrmRuntimeException;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

/**
 * sqlserver 任意版本
 * @author yuehuanghun
 *
 */
public class SqlServerDialect extends AbstractDialect {
	public static final SqlServerDialect instance = new SqlServerDialect();

	@Override
	public String getTopLimitSql(String sql, int topRows) {
		return sql.replaceFirst("^(?i)\\s*SELECT", "SELECT TOP " + topRows);
	}

	@Override
	public String getPartTypeExpression(Type partType) {
		if(partType == Type.REGEX) {
			throw new OrmRuntimeException("SQLSERVER 未支持正则表达式查询");
		}
		return super.getPartTypeExpression(partType);
	}

	@Override
	protected String getCatalog(DataSource dataSource, String catalog, String schema) {
		if(StringUtils.isNotBlank(catalog)) {
			return catalog;
		}
		try (Connection conn = dataSource.getConnection()) {
			return queryForString("SELECT Name FROM Master..SysDataBases WHERE DbId=(SELECT Dbid FROM Master..SysProcesses WHERE Spid = @@spid)", conn);
		} catch (SQLException e) {
			throw new OrmBuildingException(e);
		}
	}
	
	@Override
	public String nullValueSort(String sortExpression, String columnName, NullHandling nullHandling) {
		return super.standardNullValueSort(sortExpression, columnName, nullHandling);
	}

	@Override
	public String getFulltextExpression(FulltextMode fulltextMode) {
		if(fulltextMode == FulltextMode.SQLSERVER_FREETEXT) {
			return "FREETEXT ((${columns}), ${keyword})";
		}
		return "CONTAINS ((${columns}), ${keyword})";
	}
}
