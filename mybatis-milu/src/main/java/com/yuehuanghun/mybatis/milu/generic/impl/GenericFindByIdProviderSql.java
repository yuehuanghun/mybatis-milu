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

import com.yuehuanghun.mybatis.milu.generic.GenericCachingProviderSql;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.tool.Segment;

public class GenericFindByIdProviderSql extends GenericCachingProviderSql {

	@Override
	public String provideCachingSql(GenericProviderContext context, Object params) {
		Entity entity = context.getEntity();
		Collection<Attribute> attributes = entity.getAttributes();
		StringBuilder sqlBuilder = new StringBuilder(512);
		
		sqlBuilder.append(Segment.SELECT);
		
		Attribute idAttr = entity.getId();
		if(idAttr == null) {
			throw new RuntimeException("id属性不存在");
		}
		boolean first = true;
		
		for(Attribute attr : attributes) {
			if(!attr.isSelectable()) {
				continue;
			}
			
			if(!first) {
				sqlBuilder.append(Segment.COMMA_B);
			} else {
				first = false;
			}
			
			sqlBuilder.append(wrapIdentifier(attr.getColumnName(), context));
		}
		
		
		sqlBuilder.append(Segment.FROM_B).append(wrapIdentifier(entity.getTableName(), context));
		sqlBuilder.append(Segment.WHERE_B).append(wrapIdentifier(idAttr.getColumnName(), context)).append(" = #{id}");
		
		return sqlBuilder.toString();
	}

	@Override
	public String getMethodName() {
		return "findById";
	}

}
