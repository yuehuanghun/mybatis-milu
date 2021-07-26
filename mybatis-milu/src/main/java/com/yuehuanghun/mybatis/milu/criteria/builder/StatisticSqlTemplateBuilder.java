package com.yuehuanghun.mybatis.milu.criteria.builder;

import java.util.HashSet;
import java.util.Set;

import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.criteria.StatisticPredicate;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.tool.Constants;
import com.yuehuanghun.mybatis.milu.tool.Segment;

public class StatisticSqlTemplateBuilder extends SqlTemplateBuilder {
	private final StatisticPredicate predicate;

	public StatisticSqlTemplateBuilder(Entity entity, MiluConfiguration configuration, StatisticPredicate predicate) {
		super(entity, configuration);
		this.predicate = predicate;
	}

	@Override
	public String build() {
		StringBuilder expressionBuilder = new StringBuilder(256);
		Set<String> properties = new HashSet<>();
		predicate.renderSqlTemplate(configuration, expressionBuilder, properties, 0);

		SqlBuildingHelper.analyseDomain(entity, properties, tableAliasDispacher, configuration, joinExpressMap, joinQueryColumnNap);

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
