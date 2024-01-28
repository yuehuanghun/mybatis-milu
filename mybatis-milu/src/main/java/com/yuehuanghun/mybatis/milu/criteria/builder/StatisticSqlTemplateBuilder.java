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

import com.yuehuanghun.mybatis.milu.criteria.StatisticPredicate;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.tool.Constants;
import com.yuehuanghun.mybatis.milu.tool.Segment;

public class StatisticSqlTemplateBuilder extends SqlTemplateBuilder {
	private final StatisticPredicate predicate;

	public StatisticSqlTemplateBuilder(GenericProviderContext context, StatisticPredicate predicate) {
		super(context);
		this.predicate = predicate;
	}

	@Override
	public String build() {
		StringBuilder expressionBuilder = new StringBuilder(256);
		Set<String> properties = new HashSet<>();
		predicate.renderSqlTemplate(context, expressionBuilder, properties, 0);

		SqlBuildingHelper.analyseDomain(entity, properties, getTableAliasDispacher(), configuration, joinExpressMap, joinQueryColumnNap);

		StringBuilder sqlBuilder = new StringBuilder(1024).append(Segment.SCRIPT_LABEL);
		
		String sqlTemplateTmp = renderConditionSql(expressionBuilder.toString(), properties);
		
		StringBuilder tableSegment = new StringBuilder();
		buildTableSegment(tableSegment);
		sqlTemplateTmp = sqlTemplateTmp.replace(Constants.TABLE_HOLDER, tableSegment.toString());
		
		sqlBuilder.append(sqlTemplateTmp);
		
		sqlBuilder.append(Segment.SCRIPT_LABEL_END);
		return sqlBuilder.toString();
	}

}
