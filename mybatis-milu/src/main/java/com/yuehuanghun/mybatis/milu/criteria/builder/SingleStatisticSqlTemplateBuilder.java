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

import java.util.HashSet;
import java.util.Set;

import com.yuehuanghun.mybatis.milu.criteria.Predicate;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.tool.Segment;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

public class SingleStatisticSqlTemplateBuilder extends SqlTemplateBuilder {
	private final Predicate predicate;
	private final String maxAttr;
	private final String functionName;

	public SingleStatisticSqlTemplateBuilder(GenericProviderContext context, String functionName, String maxAttr, Predicate predicate) {
		super(context);
		this.functionName = functionName;
		this.maxAttr = maxAttr;
		this.predicate = predicate;
	}

	@Override
	public String build() {
		Attribute attribute = context.getEntity().getAttribute(maxAttr);
		if(attribute == null) {
			throw new SqlExpressionBuildingException(String.format("实体类：%s中找不到属性：%s", context.getEntity().getJavaType().getName(), maxAttr));
		}
		
		String functionExpressionTemplate = context.getConfiguration().getDialect().getFunctionExpression(functionName);
		
		StringBuilder expressionBuilder = new StringBuilder(256);
		Set<String> properties = new HashSet<>();
		predicate.renderSqlTemplate(context, expressionBuilder, properties, 0);

		SqlBuildingHelper.analyseDomain(entity, properties, getTableAliasDispacher(), configuration, joinExpressMap, joinQueryColumnNap);

		StringBuilder sqlBuilder = new StringBuilder(1024).append(Segment.SCRIPT_LABEL);
		sqlBuilder.append(Segment.SELECT).append(String.format(functionExpressionTemplate, SqlBuildingHelper.wrapIdentifier(attribute.getColumnName(), configuration))).append(Segment.SPACE).append(SqlBuildingHelper.wrapIdentifier("result", configuration)).append(Segment.FROM_B);
		buildTableSegment(sqlBuilder);
		
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
