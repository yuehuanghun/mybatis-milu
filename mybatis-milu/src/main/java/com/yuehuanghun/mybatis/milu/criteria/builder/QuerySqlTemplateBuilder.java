package com.yuehuanghun.mybatis.milu.criteria.builder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.criteria.QueryPredicate;
import com.yuehuanghun.mybatis.milu.criteria.QueryPredicateImpl;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.tool.Segment;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

public class QuerySqlTemplateBuilder extends SqlTemplateBuilder {
	private final QueryPredicate predicate;
	
	private final static Pattern ORDER_BY_PT = Pattern.compile("^\\s*ORDER BY.*$");
	
	public QuerySqlTemplateBuilder(Entity entity, MiluConfiguration configuration, QueryPredicate queryPredicate) {
		super(entity, configuration);
		this.predicate = queryPredicate;
	}	

	public String build() {
		Set<String> selectAttrs = Collections.emptySet();
		Set<String> exselectAttrs = Collections.emptySet();
		if(QueryPredicateImpl.class.isInstance(predicate)) {
			selectAttrs = ((QueryPredicateImpl)predicate).getSelectAttrs();
			exselectAttrs = ((QueryPredicateImpl)predicate).getExselectAttrs();
		}
		
		assertAttrExists(selectAttrs);
		assertAttrExists(exselectAttrs);
		
		StringBuilder expressionBuilder = new StringBuilder(256);
		Set<String> properties = new HashSet<>();
		predicate.renderSqlTemplate(configuration, expressionBuilder, properties, 0);

		SqlBuildingHelper.analyseDomain(entity, properties, tableAliasDispacher, configuration, joinExpressMap, joinQueryColumnNap);
		
		StringBuilder sqlBuilder = new StringBuilder(1024).append(Segment.SCRIPT_LABEL);
		
		sqlBuilder.append(Segment.SELECT);
		Iterator<Attribute> it = entity.getAttributes().iterator();
		boolean first = true;
		while(it.hasNext()) {
			Attribute attr = it.next();
			if(attr.isSelectable()) {
				if(!selectAttrs.isEmpty()) {
					if(!selectAttrs.contains(attr.getName())) {
						continue;
					}
				} else if(!exselectAttrs.isEmpty() && exselectAttrs.contains(attr.getName())) {
					continue;
				}
				if(first) {
					first = false;
				} else {
					sqlBuilder.append(Segment.COMMA_B);
				}						
				if(!joinExpressMap.isEmpty()) { //没有关联查询时，不需要使用表别名
					sqlBuilder.append(mainTableAlias).append(Segment.DOT);
				}
				SqlBuildingHelper.appendIdentifier(sqlBuilder, attr.getColumnName(), configuration);
			}
		}

		sqlBuilder.append(Segment.FROM_B);
		buildTableSegment(sqlBuilder);
		
		String sqlTemplateTmp = expressionBuilder.toString();
		if(StringUtils.isNotBlank(sqlTemplateTmp)) {
			sqlTemplateTmp = renderConditionSql(sqlTemplateTmp, properties);
			
			if(!ORDER_BY_PT.matcher(sqlTemplateTmp).matches()) { //如果没有查询条件时，则不用加WHERE
				sqlBuilder.append(Segment.WHERE_B);
			}
			sqlBuilder.append(sqlTemplateTmp);
		}
		
		sqlBuilder.append(Segment.SCRIPT_LABEL_END);
		
		return sqlBuilder.toString();
	}

}
