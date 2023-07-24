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

import javax.persistence.LockModeType;
import javax.sql.DataSource;

import com.yuehuanghun.mybatis.milu.criteria.FulltextMode;
import com.yuehuanghun.mybatis.milu.data.Sort.NullHandling;
import com.yuehuanghun.mybatis.milu.dialect.AbstractDialect;
import com.yuehuanghun.mybatis.milu.exception.OrmBuildingException;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

/**
 * MYSQL 数据库
 * @author yuehuanghun
 *
 */
public class MysqlDialect extends AbstractDialect {
	public static final MysqlDialect instance = new MysqlDialect();

	@Override
	public String getTopLimitSql(String sql, int topRows) {
		return sql + " LIMIT " + topRows;
	}

	@Override
	public String getLockSql(String sql, LockModeType lockModeType) {
		if(LockModeType.PESSIMISTIC_READ == lockModeType) {
			return sql + " LOCK IN SHARE MODE";
		}
		return super.getLockSql(sql, lockModeType);
	}

	@Override
	protected String getCatalog(DataSource dataSource) {
		try (Connection conn = dataSource.getConnection()) {
			return queryForString("SELECT database()", conn);
		} catch (SQLException e) {
			throw new OrmBuildingException(e);
		}
	}

	@Override
	public String nullValueSort(String sortExpression, String columnName, NullHandling nullHandling) {
		if(nullHandling == NullHandling.NATIVE) {
			return sortExpression;
		}

		if(nullHandling == NullHandling.NULLS_FIRST) {
			return " IF(ISNULL(" + columnName + "), 0, 1) ASC, " + sortExpression;
		}

		if(nullHandling == NullHandling.NULLS_LAST) {
			return " IF(ISNULL(" + columnName + "), 1, 0) ASC, " + sortExpression;
		}
		return sortExpression;
	}

	@Override
	public String getFulltextExpression(FulltextMode fulltextMode) {
		return "MATCH(${columns}) AGAINST(${keyword}) ${mode}";
	}

	@Override
	public String getFullTextModeExpression(FulltextMode fulltextMode) {
		if(fulltextMode == FulltextMode.MYSQL_BOOLEAN) {
			return " IN BOOLEAN MODE";
		}
		if(fulltextMode == FulltextMode.MYSQL_NATURAL_LANG) {
			return " IN NATURAL LANGUAGE MODE";
		}
		return StringUtils.EMPTY;
	}
}
