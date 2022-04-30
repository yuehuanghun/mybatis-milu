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
package com.yuehuanghun.mybatis.milu.dialect.db;

import com.yuehuanghun.mybatis.milu.data.Part.Type;
import com.yuehuanghun.mybatis.milu.dialect.AbstractDialect;
import com.yuehuanghun.mybatis.milu.tool.Constants;

/**
 * ORACLE
 * @author yuehuanghun
 *
 */
public class OracleDialect extends AbstractDialect {
	public static final OracleDialect instance = new OracleDialect();
	
	@Override
	public String getTopLimitSql(String sql, int topRows) {
		if(sql.contains("ORDER BY")) { //自动生成的标准SQL允许如此判断
			StringBuilder sqlBuilder = new StringBuilder(sql.length() + 60);
	        sqlBuilder.append(" SELECT * FROM (")
	            .append(sql)
	            .append(") _TMP_TABLE WHERE ROWNUM &lt;= ")
	            .append(topRows);
	        return sqlBuilder.toString();
		}
		return sql.contains("WHERE") ? sql + " AND ROWNUM &lt;= " + topRows : sql + " WHERE " + topRows;
	}

	@Override
	protected void initPartTypeExpression() {
		super.initPartTypeExpression();

		partTypeExpressionMap.put(Type.STARTING_WITH, " LIKE %s || '%%' ");
		partTypeExpressionMap.put(Type.ENDING_WITH, " LIKE '%%' || %s ");
		partTypeExpressionMap.put(Type.NOT_CONTAINING, " NOT LIKE '%%' || %s || '%%' ");
		partTypeExpressionMap.put(Type.CONTAINING, " LIKE '%%' || %s || '%%' ");
		partTypeExpressionMap.put(Type.REGEX, " REGEXP_LIKE(" + Constants.COLUMN_HOLDER + ", %s )");
	}
}
