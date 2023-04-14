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

import com.yuehuanghun.mybatis.milu.generic.GenericCachingProviderSql;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.IdAttribute;
import com.yuehuanghun.mybatis.milu.tool.Segment;

public class GenericDeleteByIdProviderSql extends GenericCachingProviderSql {

	@Override
	public String provideCachingSql(GenericProviderContext context, Object params) {
		Entity entity = context.getEntity();
		StringBuilder sqlBuilder = new StringBuilder(64);
		
		sqlBuilder.append(Segment.DELETE_FROM).append(wrapTableName(entity, context));
		
		IdAttribute idAttr = entity.getId();
		
		if(idAttr == null) {
			throw new RuntimeException("id属性不存在");
		}
		
		sqlBuilder.append(Segment.WHERE_B).append(wrapIdentifier(idAttr.getColumnName(), context)).append(" = #{id}");
		
		return sqlBuilder.toString();
	}

	@Override
	public String getMethodName() {
		return "deleteById";
	}

}
