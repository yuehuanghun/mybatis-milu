/*
 * Copyright 2020-current the original author or authors.
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

package com.yuehuanghun.mybatis.milu.criteria.ext.postgre.conds;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.yuehuanghun.mybatis.milu.criteria.Condition;
import com.yuehuanghun.mybatis.milu.criteria.ext.CompareMode;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.tool.Assert;
import com.yuehuanghun.mybatis.milu.tool.Segment;

/**
 * PostgreSQL的JSON字段元素值比较
 */
public class PostgreJsonCompare implements Condition {

	private final String attrName;
	
	private final String jsonKey;
	
	private final Object jsonVal;
	
	private final CompareMode compareMode;
	
	private final PostgreJdbcType jdbcType;

	/**
	 * 
	 * @param attrName json属性
	 * @param jsonKey json的键，仅第一层，如name。如需要深度路径查询，使用PostgreJsonPathCompare
	 * @param jsonVal 搜索包含值
	 */
	public PostgreJsonCompare(String attrName, String jsonKey, Object jsonVal, CompareMode compareMode) {
		Assert.notBlank(attrName, "attrName不能为空");
		Assert.notBlank(jsonKey, "jsonKey不能为空");
		Assert.notNull(jsonVal, "jsonVal不能为空");
		Assert.notNull(compareMode, "compareMode不能为空");
		
		this.attrName = attrName;
		this.jsonKey = jsonKey;
		this.compareMode = compareMode;
		if(jsonVal != null) {
			this.jdbcType = PostgreJdbcType.forJavaType(jsonVal.getClass());
			this.jsonVal = this.jdbcType == PostgreJdbcType.TEXT ? jsonVal.toString() : jsonVal;
		} else {
			this.jdbcType = PostgreJdbcType.TEXT;
			this.jsonVal = null;
		}
	}

	@Override
	public int renderSqlTemplate(GenericProviderContext context, StringBuilder expressionBuilder, Set<String> columns,
			int paramIndex) {
		SqlBuildingHelper.getAttribute(context.getConfiguration(), context.getEntity(), attrName, true);
		columns.add(attrName);
		String key = attrName + "_" + paramIndex;
		paramIndex ++;
		expressionBuilder.append(Segment.SPACE);
		if(this.jdbcType != PostgreJdbcType.TEXT) {
			expressionBuilder.append("CAST (");
		}
		expressionBuilder.append(Segment.DOLLAR).append(attrName).append(Segment.DOLLAR).append(" -&gt;&gt; ")
		  .append(Segment.SIGLE_QUOT).append(jsonKey).append(Segment.SIGLE_QUOT);
		if(this.jdbcType != PostgreJdbcType.TEXT) {
			expressionBuilder.append(" AS ").append(this.jdbcType).append(")");
		}
		expressionBuilder.append(Segment.SPACE).append(compareMode.getExp()).append(Segment.SPACE)
		  .append(Segment.HASH_LEFT_BRACE).append(key).append(Segment.RIGHT_BRACE);
		return paramIndex;
	}

	@Override
	public int renderParams(GenericProviderContext context, Map<String, Object> params, int paramIndex) {
		String key = attrName + "_" + paramIndex;
		params.put(key, jsonVal);
		paramIndex ++;
		return paramIndex;
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 31 * result + attrName.hashCode();
		result = 31 * result + jsonKey.hashCode();
		result = 31 * result + compareMode.hashCode();
		result = 31 * result + jdbcType.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object that) {
		if(!this.getClass().isInstance(that)) {
			return false;
		}
		return Objects.equals(this.attrName, ((PostgreJsonCompare)that).attrName)
				&& Objects.equals(this.jsonKey, ((PostgreJsonCompare)that).jsonKey)
				&& Objects.equals(this.compareMode, ((PostgreJsonCompare)that).compareMode)
				&& Objects.equals(this.jdbcType, ((PostgreJsonCompare)that).jdbcType);
	}
}