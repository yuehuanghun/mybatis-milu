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

import com.yuehuanghun.mybatis.milu.data.Sort.NullHandling;
import com.yuehuanghun.mybatis.milu.dialect.AbstractDialect;

/**
 * db2
 * @author yuehuanghun
 *
 */
public class Db2Dialect extends AbstractDialect {
	public static final Db2Dialect instance = new Db2Dialect();

	@Override
	public String getTopLimitSql(String sql, int topRows) {
		return String.format(TEMPLATE, sql, topRows);
	}

	private final String TEMPLATE = "SELECT _TMP_TABLE.*,ROWNUMBER() OVER() AS _ROW_ID FROM (%s) AS _TMP_TABLE WHERE _ROW_ID &lt;= %s";

	@Override
	public String nullValueSort(String sortExpression, String columnName, NullHandling nullHandling) {
		return super.standardNullValueSort(sortExpression, columnName, nullHandling);
	}
		
}
