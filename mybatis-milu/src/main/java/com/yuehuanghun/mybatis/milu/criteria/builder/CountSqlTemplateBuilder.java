package com.yuehuanghun.mybatis.milu.criteria.builder;

import java.util.HashSet;
import java.util.Set;

import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.criteria.Predicate;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.tool.Segment;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

public class CountSqlTemplateBuilder extends SqlTemplateBuilder {
	private final Predicate predicate;

	public CountSqlTemplateBuilder(Entity entity, MiluConfiguration configuration, Predicate predicate) {
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
		sqlBuilder.append(Segment.SELECT_COUNT).append(Segment.FROM_B);
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
