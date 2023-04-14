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

import com.yuehuanghun.mybatis.milu.dialect.AbstractDialect;

/**
 * sqlserver &gt;= 2012版本
 * @author yuehuanghun
 *
 */
public class SqlServer2012Dialect extends AbstractDialect {
	public static final SqlServer2012Dialect instance = new SqlServer2012Dialect();

	@Override
	public String getTopLimitSql(String sql, int topRows) {
		return sql + " FETCH NEXT " + topRows + " ROWS ONLY";
	}

}
