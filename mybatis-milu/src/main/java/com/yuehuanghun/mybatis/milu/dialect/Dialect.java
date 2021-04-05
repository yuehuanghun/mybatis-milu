/*
 * Copyright 2020-2021 the original author or authors.
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

import com.yuehuanghun.mybatis.milu.data.Part.Type;

/**
 * 目前仅应用于NamingQuery方法中topN条件的分页查询<br>
 * 实现参考Mybatis_PageHelper（https://gitee.com/free/Mybatis_PageHelper）
 * @author yuehuanghun
 *
 */
public interface Dialect {
  
	/**
	 * 获取第一页N条数据
	 * @param sql 原sql
	 * @param topRows 条件数
	 * @return
	 */
	public String getTopLimitSql(String sql, int topRows);
	
	/**
	 * 获取partType的SQL表达式
	 * @param partType
	 * @return
	 */
	String getPartTypeExpression(Type partType);

}