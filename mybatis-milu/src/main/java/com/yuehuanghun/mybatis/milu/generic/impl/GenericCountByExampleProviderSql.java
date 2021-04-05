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
package com.yuehuanghun.mybatis.milu.generic.impl;

import java.util.Collection;

import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.generic.GenericCachingProviderSql;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.tool.Segment;

public class GenericCountByExampleProviderSql extends GenericCachingProviderSql {

	@Override
	public String provideCachingSql(GenericProviderContext context, Object params) {
		Entity entity = context.getEntity();
		
		Collection<Attribute> attributes = entity.getAttributes();
		StringBuilder sqlBuilder = new StringBuilder(1024).append(Segment.SCRIPT_LABEL);
		
		sqlBuilder.append("SELECT COUNT(*) FROM ").append(wrapIdentifier(entity.getTableName(), context));
		
		sqlBuilder.append(Segment.WHERE_LABEL);
		for(Attribute attr : attributes) {
			if(!attr.isConditionable()) {
				continue;
			}
			
			if(attr.getConditionMode() == Mode.ALL) {
				sqlBuilder.append(Segment.AND_B)
				    .append(wrapIdentifier(attr.getColumnName(), context))
				    .append(SqlBuildingHelper.matchExpression(attr, context.getConfiguration()));
			} else if(attr.getUpdateMode() == Mode.NOT_EMPTY && CharSequence.class.isAssignableFrom(attr.getJavaType())) {
				sqlBuilder.append(" <if test=\"example.")
				    .append(attr.getName()).append(" != null and example.")
				    .append(attr.getName()).append(" != ''\"> AND ")
				    .append(wrapIdentifier(attr.getColumnName(), context))
				    .append(SqlBuildingHelper.matchExpression(attr, context.getConfiguration()))
				    .append("</if> ");
			} else {
				sqlBuilder.append(" <if test=\"example.")
				    .append(attr.getName()).append(" != null\"> AND ")
				    .append(wrapIdentifier(attr.getColumnName(), context))
				    .append(SqlBuildingHelper.matchExpression(attr, context.getConfiguration()))
				    .append("</if> ");
			}
		}
		sqlBuilder.append(Segment.WHERE_LABEL_END).append(Segment.SCRIPT_LABEL_END);
		
		return sqlBuilder.toString();
	}

	@Override
	public String getMethodName() {
		return "countByExample";
	}

}
