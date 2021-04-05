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
import java.util.Map;

import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.data.Sort;
import com.yuehuanghun.mybatis.milu.data.Sort.Order;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.generic.GenericCachingProviderSql;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.tool.Segment;

public class GenericFindByExampleAndSortProviderSql extends GenericCachingProviderSql {
	
	@SuppressWarnings("unchecked")
	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		StringBuilder mapKeyBuilder = new StringBuilder(64).append(context.getMapperType().getName());
		Sort sort = (Sort)((Map<String, Object>)params).get("sort");
		if(sort != null) {
			for(Order order : sort) {
				mapKeyBuilder.append("-").append(order.getProperty()).append("-").append(order.getDirection());
			}
		}
		return cache.computeIfAbsent(mapKeyBuilder.toString(), (key) -> {return provideCachingSql(context, params);});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String provideCachingSql(GenericProviderContext context, Object params) {
		Entity entity = context.getEntity();
		Collection<Attribute> attributes = entity.getAttributes();
		StringBuilder sqlBuilder = new StringBuilder(256).append(Segment.SCRIPT_LABEL);
		
		sqlBuilder.append(Segment.SELECT);
		
		boolean first = true;
		
		StringBuilder condition = new StringBuilder(Segment.WHERE_LABEL);
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
			if(attr.getConditionMode() == Mode.ALL) {
				condition.append(Segment.AND_B)
				    .append(wrapIdentifier(attr.getColumnName(), context))
				    .append(SqlBuildingHelper.matchExpression(attr, context.getConfiguration()));
			} else if(attr.getUpdateMode() == Mode.NOT_EMPTY && CharSequence.class.isAssignableFrom(attr.getJavaType())) {
				condition.append(" <if test=\"example.")
				    .append(attr.getName()).append(" != null and example.")
				    .append(attr.getName()).append(" != ''\"> AND ")
				    .append(wrapIdentifier(attr.getColumnName(), context))
				    .append(SqlBuildingHelper.matchExpression(attr, context.getConfiguration()))
				    .append("</if> ");
			} else {
				condition.append(" <if test=\"example.")
				    .append(attr.getName()).append(" != null\"> AND ")
				    .append(wrapIdentifier(attr.getColumnName(), context))
				    .append(SqlBuildingHelper.matchExpression(attr, context.getConfiguration()))
				    .append("</if> ");
			}
		}
		condition.append(Segment.WHERE_LABEL_END);
		sqlBuilder.append(Segment.FROM_B).append(wrapIdentifier(entity.getTableName(), context));
		sqlBuilder.append(condition);
		
		Sort sort = (Sort)((Map<String, Object>)params).get("sort");
		if(sort != null) {
			sqlBuilder.append(Segment.ORDER_BY);
			first = true;
			for(Order order : sort) {
				if(!first) {
					sqlBuilder.append(Segment.COMMA_B);
				} else {
					first = false;
				}
				
				Attribute orderAttr = entity.getAttribute(order.getProperty());
				sqlBuilder.append(wrapIdentifier(orderAttr.getColumnName(), context)).append(Segment.SPACE).append(order.getDirection().name());
			}
		}
		
		sqlBuilder.append(Segment.SCRIPT_LABEL_END);
		
		return sqlBuilder.toString();
	}

	@Override
	public String getMethodName() {
		return "findByExampleAndSort";
	}

}
