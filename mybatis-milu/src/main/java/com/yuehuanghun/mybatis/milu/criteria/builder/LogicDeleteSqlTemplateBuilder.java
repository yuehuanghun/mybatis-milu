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
package com.yuehuanghun.mybatis.milu.criteria.builder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.criteria.UpdatePredicate;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.LogicDeleteAttribute;
import com.yuehuanghun.mybatis.milu.tool.Segment;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

public class LogicDeleteSqlTemplateBuilder extends SqlTemplateBuilder {
	private final UpdatePredicate predicate;

	public LogicDeleteSqlTemplateBuilder(Entity entity, MiluConfiguration configuration, UpdatePredicate predicate) {
		super(entity, configuration);
		this.predicate = predicate;
	}

	@Override
	public String build() {
		StringBuilder expressionBuilder = new StringBuilder(256);
		Set<String> properties = new HashSet<>();
		predicate.renderSqlTemplate(configuration, expressionBuilder, properties, 0);

		SqlBuildingHelper.analyseDomain(entity, properties, tableAliasDispacher, configuration, joinExpressMap, joinQueryColumnNap);

		StringBuilder sqlBuilder = new StringBuilder(512).append(Segment.SCRIPT_LABEL);
		sqlBuilder.append(Segment.UPDATE);
		buildTableSegment(sqlBuilder);
		
		sqlBuilder.append(Segment.SET_LABEL);
		
		List<String> logicDeleteAttributeNames = entity.getLogicDeleteAttributes().stream().map(LogicDeleteAttribute::getName).collect(Collectors.toList());
		
		for(Attribute attr : entity.getAttributes()) {
			if(!attr.isUpdateable()) {
				continue;
			}
			if(attr.isVersion()) { //????????? + 1?????????+1
				String colName = SqlBuildingHelper.wrapIdentifier(attr.getColumnName(), configuration);
				sqlBuilder.append(colName).append(" = ").append(colName).append(" + 1, ");
			} else {
				if(logicDeleteAttributeNames.contains(attr.getName())) { //?????????????????????????????????
					sqlBuilder.append(SqlBuildingHelper.wrapIdentifier(attr.getColumnName(), configuration)).append(" = #{entity.").append(attr.toParameter()).append("}, ");
				} else { //?????????Null??????
					sqlBuilder.append(" <if test=\"entity.").append(attr.getName()).append(" != null\">").append(SqlBuildingHelper.wrapIdentifier(attr.getColumnName(), configuration)).append(" = #{entity.").append(attr.toParameter()).append("}, </if> ");
				}
			}
		}
		sqlBuilder.append(Segment.SET_LABEL_END);
		
		String sqlTemplateTmp = expressionBuilder.toString();
		if(StringUtils.isNotBlank(sqlTemplateTmp)) {
			sqlBuilder.append(Segment.WHERE_B);
			sqlTemplateTmp = renderConditionSql(sqlTemplateTmp, properties);
			sqlBuilder.append(sqlTemplateTmp);
		}
		
		sqlBuilder.append(Segment.SCRIPT_LABEL_END);
		return sqlBuilder.toString();
	}
}
