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
package com.yuehuanghun.mybatis.milu.generic.impl;

import java.util.Collection;

import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.generic.GenericCachingProviderSql;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.tool.Segment;

public class GenericInsertProviderSql extends GenericCachingProviderSql {

	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		SqlBuildingHelper.fill(params, true, context.getConfiguration());
		return super.provideSql(context, params);
	}

	@Override
	public String provideCachingSql(GenericProviderContext context, Object params) {
		Entity entity = context.getEntity();
		Collection<Attribute> attributes = entity.getAttributes();
		StringBuilder sqlBuilder = new StringBuilder(256).append(Segment.SCRIPT_LABEL);
		
		sqlBuilder.append("INSERT INTO ").append(wrapTableName(entity, context));
		sqlBuilder.append(" (<trim suffixOverrides=\",\">");
		
		StringBuilder values = new StringBuilder(" VALUES(<trim suffixOverrides=\",\">");
		for(Attribute attr : attributes) {
			if(!attr.isInsertable()) {
				continue;
			}
			sqlBuilder.append(" <if test=\"").append(attr.getName()).append(" != null\">").append(wrapIdentifier(attr.getColumnName(), context)).append(",</if> ");
			values.append(" <if test=\"").append(attr.getName()).append(" != null\">").append("#{").append(attr.toParameter()).append("},</if> ");
		}
		sqlBuilder.append(" </trim>) ");
		values.append("</trim>) ");
		sqlBuilder.append(values);
		sqlBuilder.append(Segment.SCRIPT_LABEL_END);
		
		return sqlBuilder.toString();
	}

	@Override
	public String getMethodName() {
		return "insert";
	}

}
