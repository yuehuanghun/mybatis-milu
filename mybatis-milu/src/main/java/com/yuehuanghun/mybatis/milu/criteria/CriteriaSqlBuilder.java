/*
 * Copyright 2020-2021 the original author or authors.
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
package com.yuehuanghun.mybatis.milu.criteria;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper.TableAliasDispacher;
import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.tool.Segment;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

import lombok.Setter;
import lombok.experimental.Accessors;

public class CriteriaSqlBuilder {
	
	private final Entity entity;
	private final String queryConditionExpression;	
	private final Set<String> queryProperties;	
	private final MiluConfiguration configuration;
	
	private TableAliasDispacher tableAliasDispacher = new TableAliasDispacher();
	private Map<String, String> joinExpressMap = new HashMap<>();
	private Map<String, String> joinQueryColumnNap = new HashMap<>();
	private String mainTableAlias;
	@Setter
	@Accessors(chain = true)
	private CriteriaType criteriaType = CriteriaType.SELECT;	
	@Setter
	@Accessors(chain = true)
	private Set<String> selectAttrs = Collections.emptySet();
	@Setter
	@Accessors(chain = true)
	private Set<String> exselectAttrs = Collections.emptySet();
	
	private final static Pattern ORDER_BY_PT = Pattern.compile("^\\s*ORDER BY.*$");

	private CriteriaSqlBuilder(Class<?> domainClazz, String queryConditionExpression, Set<String> queryProperties, MiluConfiguration configuration) {
		this.entity = configuration.getMetaModel().getEntity(domainClazz);
		this.queryConditionExpression = queryConditionExpression;
		this.queryProperties = queryProperties;
		this.configuration = configuration;
		
		this.mainTableAlias = tableAliasDispacher.dispach(Segment.TABLE_ + entity.getTableName());
	}
	
	public static CriteriaSqlBuilder instance(Class<?> domainClazz, String queryConditionExpression, Set<String> queryProperties, MiluConfiguration configuration) {
		return new CriteriaSqlBuilder(domainClazz, queryConditionExpression, queryProperties, configuration);
	}
	 
	public String build() {
		SqlBuildingHelper.analyseDomain(entity, queryProperties, tableAliasDispacher, configuration, joinExpressMap, joinQueryColumnNap);
		
		StringBuilder sqlBuilder = new StringBuilder(1024).append(Segment.SCRIPT_LABEL);
		
		switch (criteriaType) {
		case SELECT:
			buildSelectSegment(sqlBuilder);
			break;

		case UPDATE:
			buildUpdateSegment(sqlBuilder);
			break;
		case DELETE:
			buildDeleteSegment(sqlBuilder);
			break;
		case COUNT:
			buildCountSegment(sqlBuilder);
			break;
		}		
		
		if(StringUtils.isNotBlank(queryConditionExpression)) {
			String condition = queryConditionExpression;
			for(String property : queryProperties) {
				if(joinQueryColumnNap.containsKey(property)) {
					condition = condition.replace(SqlBuildingHelper.columnHolder(property), joinQueryColumnNap.get(property));
				} else {
					String column = Segment.EMPTY;
					if(!joinExpressMap.isEmpty()) {
						column = mainTableAlias + Segment.DOT;
					}
					Attribute attr = entity.getAttribute(property);
					column += SqlBuildingHelper.wrapIdentifier(attr.getColumnName(), configuration);
					condition = condition.replace(SqlBuildingHelper.columnHolder(property), column);
				}	
			}
			
			if(!ORDER_BY_PT.matcher(condition).matches()) {
				sqlBuilder.append(Segment.WHERE_B);
			}
			sqlBuilder.append(condition);
		}
		
		sqlBuilder.append(Segment.SCRIPT_LABEL_END);
		
		return sqlBuilder.toString();
	}
	
	private void buildSelectSegment(StringBuilder sqlBuilder) {
		assertAttrExists(selectAttrs);
		assertAttrExists(exselectAttrs);
		
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
	}
	
	private void buildTableSegment(StringBuilder sqlBuilder) {
		
		SqlBuildingHelper.appendIdentifier(sqlBuilder, entity.getTableName(), configuration); //表名
		if(!joinExpressMap.isEmpty()) { //没有关联查询时，不需要使用表别名
			sqlBuilder.append(Segment.SPACE).append(mainTableAlias);

            for(String joinExpress : joinExpressMap.values()) {
            	sqlBuilder.append(joinExpress);
            }
		}
	}
	
	private void buildUpdateSegment(StringBuilder sqlBuilder) {
		sqlBuilder.append(Segment.UPDATE);
		buildTableSegment(sqlBuilder);
		
		sqlBuilder.append(Segment.SET_LABEL);
		
		for(Attribute attr : entity.getAttributes()) {
			if(!attr.isUpdateable()) {
				continue;
			}
			if(attr.isVersion()) { //版本号 + 1
				sqlBuilder.append(" <if test=\"entity.").append(attr.getName()).append(" != null\">").append(SqlBuildingHelper.wrapIdentifier(attr.getColumnName(), configuration)).append(" = #{entity.").append(attr.getName()).append("} + 1, </if> ");
			} else {
				if(attr.getUpdateMode() == Mode.ALL) {
					sqlBuilder.append(SqlBuildingHelper.wrapIdentifier(attr.getColumnName(), configuration)).append(" = #{entity.").append(attr.getName()).append("}");
				} else if(attr.getUpdateMode() == Mode.NOT_EMPTY && CharSequence.class.isAssignableFrom(attr.getJavaType())) {
					sqlBuilder.append(" <if test=\"entity.").append(attr.getName()).append(" != null and entity.").append(attr.getName()).append("!=''\">").append(SqlBuildingHelper.wrapIdentifier(attr.getColumnName(), configuration)).append(" = #{entity.").append(attr.getName()).append("}, </if> ");
				} else {
					sqlBuilder.append(" <if test=\"entity.").append(attr.getName()).append(" != null\">").append(SqlBuildingHelper.wrapIdentifier(attr.getColumnName(), configuration)).append(" = #{entity.").append(attr.getName()).append("}, </if> ");
				}
			}
			
		}
		sqlBuilder.append(Segment.SET_LABEL_END);
	}
	
	private void buildDeleteSegment(StringBuilder sqlBuilder) {
		sqlBuilder.append(Segment.DELETE_FROM);
		buildTableSegment(sqlBuilder);		
	}
	
	private void buildCountSegment(StringBuilder sqlBuilder) {
		sqlBuilder.append(Segment.SELECT_COUNT).append(Segment.FROM_B);;
		buildTableSegment(sqlBuilder);		
	}
	
	private void assertAttrExists(Set<String> attrs) {
		if(attrs == null || attrs.isEmpty()) {
			return;
		}
		
		for(String attr : attrs) {
			if(!entity.hasAttribute(attr)) {
				throw new SqlExpressionBuildingException(String.format("实体类%s中不存在属性：%s", entity.getJavaType().getName(), attr));
			}
		}
	}
	
	public enum CriteriaType {
		SELECT, UPDATE, DELETE, COUNT
	}
}