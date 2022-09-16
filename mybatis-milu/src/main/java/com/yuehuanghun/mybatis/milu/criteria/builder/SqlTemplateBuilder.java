package com.yuehuanghun.mybatis.milu.criteria.builder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper.TableAliasDispacher;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.tool.Segment;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

public abstract class SqlTemplateBuilder {

	protected final Entity entity;
	protected final MiluConfiguration configuration;

	protected TableAliasDispacher tableAliasDispacher = new TableAliasDispacher();
	protected Map<String, String> joinExpressMap = new HashMap<>();
	protected Map<String, String> joinQueryColumnNap = new HashMap<>();
	protected String mainTableAlias;
	
	public SqlTemplateBuilder(Entity entity, MiluConfiguration configuration) {
		super();
		this.entity = entity;
		this.configuration = configuration;
		this.mainTableAlias = tableAliasDispacher.dispach(Segment.TABLE_ + entity.getTableName());
	}
	
	protected void buildTableSegment(StringBuilder sqlBuilder) {	
		SqlBuildingHelper.appendSchema(sqlBuilder, entity.getSchema(), configuration);
		SqlBuildingHelper.appendIdentifier(sqlBuilder, entity.getTableName(), configuration); //表名
		if(!joinExpressMap.isEmpty()) { //没有关联查询时，不需要使用表别名
			sqlBuilder.append(Segment.SPACE).append(mainTableAlias);

            for(String joinExpress : joinExpressMap.values()) {
            	sqlBuilder.append(joinExpress);
            }
		}
	}
	
	protected String renderConditionSql(String sqlTemplate, Set<String> properties) {
		String sqlTemplateTmp = sqlTemplate;
		if(StringUtils.isNotBlank(sqlTemplateTmp)) {
			for(String property : properties) {
				if(joinQueryColumnNap.containsKey(property)) {
					sqlTemplateTmp = sqlTemplateTmp.replace(SqlBuildingHelper.columnHolder(property), joinQueryColumnNap.get(property));
				} else {
					String column = Segment.EMPTY;
					if(!joinExpressMap.isEmpty()) {
						column = mainTableAlias + Segment.DOT;
					}
					Attribute attr = entity.getAttribute(property);
					column += SqlBuildingHelper.wrapIdentifier(attr.getColumnName(), configuration);
					sqlTemplateTmp = sqlTemplateTmp.replace(SqlBuildingHelper.columnHolder(property), column);
				}	
			}
		}
		return sqlTemplateTmp;
	}

	abstract Object build();
}
