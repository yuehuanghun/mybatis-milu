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
import java.util.Map;

import com.github.pagehelper.PageHelper;
import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.generic.AbstractGenericExampleProviderSql;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.RangeCondition;
import com.yuehuanghun.mybatis.milu.pagehelper.Pageable;
import com.yuehuanghun.mybatis.milu.tool.Constants;
import com.yuehuanghun.mybatis.milu.tool.Segment;

public class GenericFindByExampleProviderSql extends AbstractGenericExampleProviderSql {

	@SuppressWarnings("rawtypes")
	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		String sql = super.provideSql(context, params);

		Map paramMap = ((Map)params);
		boolean hasPaging = false;
		if(paramMap.containsKey(Constants.PAGE)) {
			Pageable page = (Pageable) paramMap.get(Constants.PAGE);
			if(page != null) {
				PageHelper.startPage(page.getPageNum(), page.getPageSize(), page.isCount());
				hasPaging = true;
			}
		}
		
		if(!hasPaging && paramMap.get(Constants.EXAMPLE) instanceof Pageable) {
			Pageable page = (Pageable)  paramMap.get(Constants.EXAMPLE);
			if(page != null && page.getPageNum() > 0 && page.getPageSize() > 0) {
				PageHelper.startPage(page.getPageNum(), page.getPageSize(), page.isCount());
			}
		}
		
		return sql;
	}

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
			} else if(attr.getConditionMode() == Mode.NOT_EMPTY && CharSequence.class.isAssignableFrom(attr.getJavaType())) {
				condition.append(Segment.IF_TEST_EXAMPLE)
				    .append(attr.getName()).append(Segment.NOT_EQUAL_NULL_AND_EXAMPLE)
				    .append(attr.getName()).append(Segment.NOT_EMPTY_NULL_CLOSING).append(Segment.AND_B)
				    .append(wrapIdentifier(attr.getColumnName(), context))
				    .append(SqlBuildingHelper.matchExpression(attr, context.getConfiguration()))
				    .append(Segment.IF_LABEL_END);
			} else {
				condition.append(Segment.IF_TEST_EXAMPLE)
				    .append(attr.getName()).append(Segment.NOT_EQUAL_NULL_CLOSING).append(Segment.AND_B)
				    .append(wrapIdentifier(attr.getColumnName(), context))
				    .append(SqlBuildingHelper.matchExpression(attr, context.getConfiguration()))
				    .append(Segment.IF_LABEL_END);
			}
			
			for(RangeCondition range : attr.getRangeList()) {
				condition.append(Segment.IF_TEST_EXAMPLE_NOT_BLANK)
				    .append(range.getKeyName()).append(Segment.RIGHT_BRACKET_CLOSING).append(Segment.AND_B)
				    .append(wrapIdentifier(attr.getColumnName(), context))
				    .append(SqlBuildingHelper.matchExpression(range.getType(), range.getKeyName(), attr, context.getConfiguration()))
				    .append(Segment.IF_LABEL_END);
			}
		}
		condition.append(Segment.WHERE_LABEL_END);
		sqlBuilder.append(Segment.FROM_B).append(wrapTableName(entity, context));
		sqlBuilder.append(condition).append(Segment.SCRIPT_LABEL_END);

		return sqlBuilder.toString();
	}

	@Override
	public String getMethodName() {
		return "findByExample";
	}

}
