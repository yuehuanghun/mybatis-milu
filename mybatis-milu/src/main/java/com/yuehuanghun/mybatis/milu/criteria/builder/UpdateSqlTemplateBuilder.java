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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.criteria.UpdatePredicate;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.tool.Segment;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

public class UpdateSqlTemplateBuilder extends SqlTemplateBuilder {
	private final UpdatePredicate predicate;
	private List<String> nullableUpdateAttrNames = Collections.emptyList();

	public UpdateSqlTemplateBuilder(Entity entity, MiluConfiguration configuration, UpdatePredicate predicate) {
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
		
		for(Attribute attr : entity.getAttributes()) {
			if(!attr.isUpdateable()) {
				continue;
			}
			if(attr.isVersion()) { //版本号 + 1，总是+1
				sqlBuilder.append(SqlBuildingHelper.wrapIdentifier(attr.getColumnName(), configuration)).append(" = #{entity.").append(attr.toParameter()).append("} + 1, ");
			} else {
				if(predicate.getUpdateMode() == Mode.ALL || nullableUpdateAttrNames.contains(attr.getName())) {
					sqlBuilder.append(SqlBuildingHelper.wrapIdentifier(attr.getColumnName(), configuration)).append(" = #{entity.").append(attr.toParameter()).append("}, ");
				} else if(predicate.getUpdateMode() == Mode.NOT_EMPTY && CharSequence.class.isAssignableFrom(attr.getJavaType())) {
					sqlBuilder.append(" <if test=\"entity.").append(attr.getName()).append(" != null and entity.").append(attr.getName()).append("!=''\">").append(SqlBuildingHelper.wrapIdentifier(attr.getColumnName(), configuration)).append(" = #{entity.").append(attr.getName()).append("}, </if> ");
				} else {
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

	public UpdateSqlTemplateBuilder setNullableUpdateAttrNames(List<String> nullableUpdateAttrNames) {
		this.nullableUpdateAttrNames = nullableUpdateAttrNames;
		return this;
	}
}
