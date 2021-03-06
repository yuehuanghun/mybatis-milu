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
package com.yuehuanghun.mybatis.milu.dialect;

import javax.persistence.LockModeType;

import com.yuehuanghun.mybatis.milu.data.Part.Type;
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
	
	String getFunctionExpression(String function);
}