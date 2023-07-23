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
package com.yuehuanghun.mybatis.milu.criteria.builder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.criteria.Patch;
import com.yuehuanghun.mybatis.milu.criteria.Predicate;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.tool.Segment;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

public class UpdatePatchSqlTemplateBuilder extends SqlTemplateBuilder {
	private final Predicate predicate;
	private final Patch patch;

	public UpdatePatchSqlTemplateBuilder(GenericProviderContext context, Patch patch, Predicate predicate) {
		super(context);
		this.predicate = predicate;
		this.patch = patch;
	}

	@Override
	public String build() {
		Entity entity = context.getEntity();
		MiluConfiguration configuration = context.getConfiguration();
		
		Set<String> patchAttrNames = patch.getAttrNames();

		patchAttrNames.forEach(attrName -> {
			if(!entity.hasAttribute(attrName)) {
				throw new SqlExpressionBuildingException(String.format("实体类%s中未包含属性%s", entity.getJavaType().getName(), attrName));
			}
		});

		StringBuilder expressionBuilder = new StringBuilder(256);
		Set<String> properties = new HashSet<>();
		predicate.renderSqlTemplate(context, expressionBuilder, properties, 0);

		SqlBuildingHelper.analyseDomain(entity, properties, tableAliasDispacher, configuration, joinExpressMap, joinQueryColumnNap);
		
		
		Collection<Attribute> attributes = entity.getAttributes();
		StringBuilder sqlBuilder = new StringBuilder(256).append(Segment.SCRIPT_LABEL);
		
		sqlBuilder.append(Segment.UPDATE).append(SqlBuildingHelper.wrapTableName(entity, configuration));
		sqlBuilder.append(Segment.SET_LABEL);
		
		for(Attribute attr : attributes) {
			if(!attr.isUpdateable()) {
				continue;
			}
			
			if(attr.isVersion()) { //版本号 + 1，总是+1
				String colName = SqlBuildingHelper.wrapIdentifier(attr.getColumnName(), configuration);
				sqlBuilder.append(colName).append(" = ").append(colName).append(" + 1, ");
				continue;
			}
			
			if(!patchAttrNames.contains(attr.getName()) || attr.isId()) {
				continue;
			}
			
			sqlBuilder.append(SqlBuildingHelper.wrapIdentifier(attr.getColumnName(), configuration)).append(" = #{entity.").append(attr.toParameter()).append("}, ");
			
		}
		sqlBuilder.append(Segment.SET_LABEL_END);
		
		Attribute idAttr = entity.getId();
		if(idAttr == null) {
			throw new SqlExpressionBuildingException("id属性不存在");
		}
		
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
