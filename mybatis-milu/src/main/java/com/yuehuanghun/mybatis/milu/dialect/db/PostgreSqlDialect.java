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

import javax.persistence.LockModeType;

import com.yuehuanghun.mybatis.milu.data.Part.Type;
import com.yuehuanghun.mybatis.milu.dialect.AbstractDialect;

/**
 * PostgreSql
 * @author yuehuanghun
 *
 */
public class PostgreSqlDialect extends AbstractDialect {
	public static final PostgreSqlDialect instance = new PostgreSqlDialect();

	@Override
	public String getTopLimitSql(String sql, int topRows) {
		return sql + " LIMIT " + topRows;
	}

	@Override
	public String getLockSql(String sql, LockModeType lockModeType) {
		if(LockModeType.PESSIMISTIC_READ == lockModeType) {
			return sql + " FOR SHARE";
		}
		return super.getLockSql(sql, lockModeType);
	}

	@Override
	protected void initPartTypeExpression() {
		super.initPartTypeExpression();
		partTypeExpressionMap.put(Type.REGEX, " ~ %s");
	}
}
