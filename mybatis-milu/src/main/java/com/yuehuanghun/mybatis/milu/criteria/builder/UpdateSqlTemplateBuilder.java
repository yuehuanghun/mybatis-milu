package com.yuehuanghun.mybatis.milu.criteria.builder;

import java.util.HashSet;
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

		StringBuilder sqlBuilder = new StringBuilder(1024).append(Segment.SCRIPT_LABEL);
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
				if(predicate.getUpdateMode() == Mode.ALL) {
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

}
