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
package com.yuehuanghun.mybatis.milu.generic.impl;

import java.util.Collection;

import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.generic.GenericCachingProviderSql;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.tool.Segment;

public class GenericUpdateByIdProviderSql extends GenericCachingProviderSql {

	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		SqlBuildingHelper.fill(params, false, context.getConfiguration());
		return super.provideSql(context, params);
	}

	@Override
	public String provideCachingSql(GenericProviderContext context, Object params) {
		Entity entity = context.getEntity();
		Collection<Attribute> attributes = entity.getAttributes();
		StringBuilder sqlBuilder = new StringBuilder(256).append(Segment.SCRIPT_LABEL);
		
		sqlBuilder.append(Segment.UPDATE).append(wrapIdentifier(entity.getTableName(), context));
		sqlBuilder.append(Segment.SET_LABEL);
		
		for(Attribute attr : attributes) {
			if(!attr.isUpdateable()) {
				continue;
			}
			if(attr.isVersion()) { //版本号 + 1，总是+1
				String colName = wrapIdentifier(attr.getColumnName(), context);
				sqlBuilder.append(colName).append(" = ").append(colName).append(" + 1, ");
			} else {
				if(attr.getUpdateMode() == Mode.ALL) {
					sqlBuilder.append(wrapIdentifier(attr.getColumnName(), context)).append(" = #{entity.").append(attr.toParameter()).append(Segment.RIGHT_BRACE);
				} else if(attr.getUpdateMode() == Mode.NOT_EMPTY && CharSequence.class.isAssignableFrom(attr.getJavaType())) {
					sqlBuilder.append(" <if test=\"entity.").append(attr.getName()).append(" != null and entity.").append(attr.getName()).append("!=''\">").append(wrapIdentifier(attr.getColumnName(), context)).append(" = #{entity.").append(attr.toParameter()).append("}, </if> ");
				} else {
					sqlBuilder.append(" <if test=\"entity.").append(attr.getName()).append(" != null\">").append(wrapIdentifier(attr.getColumnName(), context)).append(" = #{entity.").append(attr.toParameter()).append("}, </if> ");
				}
			}
			
		}
		sqlBuilder.append(Segment.SET_LABEL_END);
		
		Attribute idAttr = entity.getId();
		if(idAttr == null) {
			throw new SqlExpressionBuildingException("id属性不存在");
		}
		sqlBuilder.append(Segment.WHERE_B).append(wrapIdentifier(idAttr.getColumnName(), context)).append(" = #{entity.").append(idAttr.getName()).append(Segment.RIGHT_BRACE);
		
		if(entity.isHasVersion()) {
			Attribute version = entity.getVersion();
			sqlBuilder.append(" <if test=\"entity.").append(version.getName()).append(" != null\"> AND ").append(wrapIdentifier(version.getColumnName(), context)).append(" = #{entity.").append(version.getName()).append("} </if> ");
		}
		
		sqlBuilder.append(Segment.SCRIPT_LABEL_END);
		
		return sqlBuilder.toString();
	}

	@Override
	public String getMethodName() {
		return "updateById";
	}

}
