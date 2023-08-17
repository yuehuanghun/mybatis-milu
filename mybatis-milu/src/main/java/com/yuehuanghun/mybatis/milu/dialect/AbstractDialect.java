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
package com.yuehuanghun.mybatis.milu.dialect;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import com.yuehuanghun.mybatis.milu.criteria.FulltextMode;
import com.yuehuanghun.mybatis.milu.data.Part.Type;
import com.yuehuanghun.mybatis.milu.data.Sort.NullHandling;
import com.yuehuanghun.mybatis.milu.exception.OrmBuildingException;
import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.tool.Assert;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

public abstract class AbstractDialect implements Dialect {
	private static Log log = LogFactory.getLog(AbstractDialect.class);
	protected final Map<Type, String> partTypeExpressionMap = new HashMap<>();
	protected final Map<String, String> functionMap = new HashMap<>();
	protected final Map<Class<?>, Integer> javaType2JdbcTypeMap = new HashMap<>();
	
	protected AbstractDialect() {
		initPartTypeExpression();
		initFunctionMap();
	}
	
	protected void initPartTypeExpression() {
		partTypeExpressionMap.put(Type.BETWEEN, " BETWEEN %s AND %s");
		partTypeExpressionMap.put(Type.IS_NOT_NULL, " IS NOT NULL");
		partTypeExpressionMap.put(Type.IS_NULL, " IS NULL");
		partTypeExpressionMap.put(Type.LESS_THAN, " &lt; %s");
		partTypeExpressionMap.put(Type.LESS_THAN_EQUAL, " &lt;= %s");
		partTypeExpressionMap.put(Type.GREATER_THAN, " &gt; %s");
		partTypeExpressionMap.put(Type.GREATER_THAN_EQUAL, " &gt;= %s");
		partTypeExpressionMap.put(Type.BEFORE, " &lt; %s");
		partTypeExpressionMap.put(Type.AFTER, " &gt; %s");
		partTypeExpressionMap.put(Type.NOT_LIKE, " NOT LIKE %s");
		partTypeExpressionMap.put(Type.LIKE, " LIKE %s ");
		partTypeExpressionMap.put(Type.STARTING_WITH, " LIKE CONCAT(%s,'%%')");
		partTypeExpressionMap.put(Type.ENDING_WITH, " LIKE CONCAT('%%', %s)");
		partTypeExpressionMap.put(Type.NOT_CONTAINING, " NOT LIKE CONCAT('%%', %s, '%%')");
		partTypeExpressionMap.put(Type.CONTAINING, " LIKE CONCAT('%%', %s, '%%')");
		partTypeExpressionMap.put(Type.NOT_IN, " NOT IN (<foreach collection=\"%s\" item=\"item\" separator=\",\">#{item}</foreach>)");
		partTypeExpressionMap.put(Type.IN, " IN (<foreach collection=\"%s\" item=\"item\" separator=\",\">#{item}</foreach>)");
		partTypeExpressionMap.put(Type.REGEX, " REGEXP %s");
		partTypeExpressionMap.put(Type.TRUE, " = TRUE");
		partTypeExpressionMap.put(Type.FALSE, " = FALSE");
		partTypeExpressionMap.put(Type.SIMPLE_PROPERTY, " = %s");
		partTypeExpressionMap.put(Type.NEGATING_SIMPLE_PROPERTY, " &lt;&gt; %s");
	}
	
	protected void initFunctionMap() {
		functionMap.put(SUM, "SUM(%s)");
		functionMap.put(COUNT, "COUNT(%s)");
		functionMap.put(MIN, "MIN(%s)");
		functionMap.put(MAX, "MAX(%s)");
		functionMap.put(AVG, "AVG(%s)");
	}
	
	/**
	 * 由于多个jdbc type可以映射同一个java type，所以这里设置并不能绝对准确。<br>
	 * 如果出现问题，应当手动设置@AttributeOptions(jdbcType=?)<br>
	 * 这里的数据是getTableColumnDataType获取数据失败后才会使用
	 * @see #getTableColumnJdbcType(String, String, String, DataSource)
	 */
	protected void initJavaTypeToJdbcType() {
		javaType2JdbcTypeMap.put(BigInteger.class, Types.NUMERIC);
		javaType2JdbcTypeMap.put(Long.class, Types.BIGINT);
		javaType2JdbcTypeMap.put(long.class, Types.BIGINT);
		javaType2JdbcTypeMap.put(Integer.class, Types.INTEGER);
		javaType2JdbcTypeMap.put(int.class, Types.INTEGER);
		javaType2JdbcTypeMap.put(Short.class, Types.SMALLINT);
		javaType2JdbcTypeMap.put(short.class, Types.SMALLINT);
		javaType2JdbcTypeMap.put(Byte.class, Types.TINYINT);
		javaType2JdbcTypeMap.put(byte.class, Types.TINYINT);
		javaType2JdbcTypeMap.put(Double.class, Types.DOUBLE);
		javaType2JdbcTypeMap.put(double.class, Types.DOUBLE);
		javaType2JdbcTypeMap.put(Float.class, Types.FLOAT);
		javaType2JdbcTypeMap.put(float.class, Types.FLOAT);
		javaType2JdbcTypeMap.put(String.class, Types.VARCHAR);
		javaType2JdbcTypeMap.put(Boolean.class, Types.BIT);
		javaType2JdbcTypeMap.put(LocalDateTime.class, Types.TIMESTAMP);
		javaType2JdbcTypeMap.put(LocalDate.class, Types.DATE);
		javaType2JdbcTypeMap.put(LocalTime.class, Types.TIME);
		javaType2JdbcTypeMap.put(Date.class, Types.TIMESTAMP);
		javaType2JdbcTypeMap.put(Timestamp.class, Types.TIMESTAMP);
		javaType2JdbcTypeMap.put(java.sql.Date.class, Types.DATE);
		javaType2JdbcTypeMap.put(BigDecimal.class, Types.NUMERIC);
		javaType2JdbcTypeMap.put(byte[].class, Types.VARBINARY);
	}
	
	public Map<Class<?>, Integer> getJavaTypeToJdbcTypeMap() {
		return javaType2JdbcTypeMap;
	}

	public String getPartTypeExpression(Type partType) {
		String expression = partTypeExpressionMap.get(partType);
		Assert.notBlank(expression, "未支持的表达式类型");
		return expression;
	}

	@Override
	public String getFunctionExpression(String function) {
		return functionMap.get(function);
	}
	
	protected String getCatalog(DataSource dataSource, String catalog, String schema) {
		return catalog;
	}
	
	protected String getSchema(DataSource dataSource, String catalog, String schema) {
		return schema;
	}
	
	protected String queryForString(String sql, Connection conn) {
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if(rs.next()) {
				return rs.getString(1);
			}
			
			return null;
		} catch (Exception e) {
			throw new OrmBuildingException(e);
		} finally {
			if(st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					// ignore
				}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					// ignore
				}
			}
		}
	}

	@Override
	public Map<String, Integer> getTableColumnJdbcType(String catalog, String schema, String tableName, DataSource dataSource) {
		Map<String, Integer> map = new HashMap<>();
		try (Connection conn = dataSource.getConnection()) {
			DatabaseMetaData dbMetaData = conn.getMetaData();
			ResultSet columnRs = dbMetaData.getColumns(getCatalog(dataSource, catalog, schema), getSchema(dataSource, catalog, schema), tableName, null);
            
			while(columnRs.next()) {
				String columnName = columnRs.getString("COLUMN_NAME");
				int dataType = columnRs.getInt("DATA_TYPE");
				map.put(columnName.toLowerCase(), dataType); // 转小写
			}
		} catch (Exception e) {
			log.warn(e.getMessage());
			log.warn(String.format("获取表【%s】的列元数据失败，请确认当前数据库连接用户是否拥有相应权限。", tableName));
			e.printStackTrace();
		}
		return map;
	}

	@Override
	public String nullValueSort(String sortExpression, String columnName, NullHandling nullHandling) {
		if(nullHandling != NullHandling.NATIVE) {
			log.warn("当前数据库未实现支持空值排序");
		}
		return sortExpression;
	}
	
	// 标准空值排序
	protected String standardNullValueSort(String sortExpression, String columnName, NullHandling nullHandling) {
		if(nullHandling == NullHandling.NATIVE) {
			return sortExpression;
		}
		if(nullHandling == NullHandling.NULLS_FIRST) {
			return sortExpression + " NULLS FIRST";
		}
		if(nullHandling == NullHandling.NULLS_LAST) {
			return sortExpression + " NULLS LAST";
		}
		return sortExpression;
	}

	@Override
	public String getFulltextExpression(FulltextMode fulltextMode) {
		throw new SqlExpressionBuildingException("当前数据库未支持全文搜索");
	}

	@Override
	public String getFullTextModeExpression(FulltextMode fulltextMode) {
		return StringUtils.EMPTY;
	}
}
