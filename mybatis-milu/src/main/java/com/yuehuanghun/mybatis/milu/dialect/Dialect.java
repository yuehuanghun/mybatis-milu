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

import java.util.Map;

import javax.persistence.LockModeType;
import javax.sql.DataSource;

import com.yuehuanghun.mybatis.milu.criteria.FulltextMode;
import com.yuehuanghun.mybatis.milu.data.Part.Type;
import com.yuehuanghun.mybatis.milu.data.Sort.NullHandling;
import com.yuehuanghun.mybatis.milu.tool.Segment;

/**
 * 目前仅应用于NamingQuery方法中topN条件的分页查询<br>
 * 实现参考Mybatis_PageHelper（https://gitee.com/free/Mybatis_PageHelper）
 * @author yuehuanghun
 *
 */
public interface Dialect {
	String SUM = "sum";
	String COUNT = "count";
	String MIN = "min";
	String MAX = "max";
	String AVG = "avg";
  
	/**
	 * 获取第一页N条数据
	 * @param sql 原sql
	 * @param topRows 条件数
	 * @return 添加分页后SQL表达式
	 */
	String getTopLimitSql(String sql, int topRows);
	
	/**
	 * 获取partType的SQL表达式
	 * @param partType 类型
	 * @return 表达式
	 */
	String getPartTypeExpression(Type partType);

	/**
	 * 为sql添加锁
	 * @param sql 原始sql
	 * @param lockModeType 锁模式
	 * @return 完成后的sql
	 */
	default String getLockSql(String sql, LockModeType lockModeType) {
		if(LockModeType.PESSIMISTIC_WRITE == lockModeType || LockModeType.PESSIMISTIC_READ == lockModeType || LockModeType.PESSIMISTIC_FORCE_INCREMENT == lockModeType) {
			return sql + Segment.FOR_UPDATE;
		}
		return sql;
	}
	
	/**
	 * 函数查询表达式
	 * @param function 函数名
	 * @return 表达式
	 */
	String getFunctionExpression(String function);
	
	/**
	 * 获取表的字段的jdbc类型
	 * @param catalog catalog
	 * @param schema schema
	 * @param tableName 表名
	 * @param dataSource 数据源
	 * @return 映射
	 */
	Map<String, Integer> getTableColumnJdbcType(String catalog, String schema, String tableName, DataSource dataSource);

	/**
	 * java类到jdbc类型的缺省映射，如果通过DatabaseMetaData获取实体类属性的列的jdbc type失败，则缺省使用这里的配置数据
	 * 
	 * #getTableColumnDataType(String, String, String, DataSource)
	 * @return 映射
	 */
	Map<Class<?>, Integer> getJavaTypeToJdbcTypeMap();
	
	/**
	 * 空值排序
	 * @param sortExpression 未加空值排序前的排序表达式
	 * @param columnName 列名
	 * @param nullHandling 空值排序枚举
	 * @return 添加空值排序后的排序
	 */
	String nullValueSort(String sortExpression, String columnName, NullHandling nullHandling);
	
	String getFulltextExpression(FulltextMode fulltextMode);
	
	String getFullTextModeExpression(FulltextMode fulltextMode);
}