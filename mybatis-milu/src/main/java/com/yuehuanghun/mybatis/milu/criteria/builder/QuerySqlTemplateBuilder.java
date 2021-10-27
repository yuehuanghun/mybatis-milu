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
package com.yuehuanghun.mybatis.milu.criteria.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;

import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.criteria.QueryPredicate;
import com.yuehuanghun.mybatis.milu.criteria.QueryPredicateImpl;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.mapping.MiluMapperBuilderAssistant;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.PluralAttribute;
import com.yuehuanghun.mybatis.milu.tool.Segment;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Data;

public class QuerySqlTemplateBuilder extends SqlTemplateBuilder {
	private final QueryPredicate predicate;

	private final static Pattern ORDER_BY_PT = Pattern.compile("^\\s*ORDER BY.*$");
	private final static List<ResultFlag> ID_FLAG_LIST = Arrays.asList(ResultFlag.ID);

	public QuerySqlTemplateBuilder(Entity entity, MiluConfiguration configuration, QueryPredicate queryPredicate) {
		super(entity, configuration);
		this.predicate = queryPredicate;
	}

	public BuildResult build() {
		Set<String> selectAttrs = Collections.emptySet();
		Set<String> exselectAttrs = Collections.emptySet();
		if (QueryPredicateImpl.class.isInstance(predicate)) {
			selectAttrs = ((QueryPredicateImpl) predicate).getSelectAttrs();
			exselectAttrs = ((QueryPredicateImpl) predicate).getExselectAttrs();
		}

		if(selectAttrs.isEmpty()) {
			selectAttrs.add("*");
		}
		Map<String, List<Attribute>> selectEntityAttrMap = analyseAttrs(selectAttrs);
		Map<String, List<Attribute>> exselectEntityAttrMap = analyseAttrs(exselectAttrs);

		StringBuilder expressionBuilder = new StringBuilder(256);
		Set<String> properties = new HashSet<>();
		predicate.renderSqlTemplate(configuration, expressionBuilder, properties, 0);
		
		if(!selectEntityAttrMap.isEmpty()) {
			selectEntityAttrMap.forEach((attrName, attributes) -> {
				if(StringUtils.isNotBlank(attrName)) {
					properties.add(attrName + StringUtils.capitalize(attributes.get(0).getName())); //只需要一个即可，用于解析关联表
				}
			});
		}

		SqlBuildingHelper.analyseDomain(entity, properties, tableAliasDispacher, configuration, joinExpressMap,
				joinQueryColumnNap);

		StringBuilder sqlBuilder = new StringBuilder(1024).append(Segment.SCRIPT_LABEL);

		sqlBuilder.append(Segment.SELECT);
		Iterator<Entry<String, List<Attribute>>> it = selectEntityAttrMap.entrySet().iterator();
		boolean firstColumn = true;
		while(it.hasNext()) {
			Entry<String, List<Attribute>> entityEntry = it.next();
			String mainEntityAttrName = entityEntry.getKey();
			List<String> exselectAttrNameList = exselectEntityAttrMap.containsKey(mainEntityAttrName) ? exselectEntityAttrMap.get(mainEntityAttrName).stream().map(Attribute::getName).collect(Collectors.toList()) : Collections.emptyList();
			for(Attribute attr : entityEntry.getValue()) {
				if (!exselectAttrNameList.isEmpty() && exselectAttrNameList.contains(attr.getName())) {
					continue;
				}
				if (firstColumn) {
					firstColumn = false;
				} else {
					sqlBuilder.append(Segment.COMMA_B);
				}
				if (StringUtils.isBlank(mainEntityAttrName)) { // 没有关联查询时，不需要使用表别名
					if(!joinExpressMap.isEmpty()) {
						sqlBuilder.append(mainTableAlias).append(Segment.DOT);
					}
				} else {
					sqlBuilder.append(tableAliasDispacher.dispach(Segment.ATTR_ + mainEntityAttrName)).append(Segment.DOT); //表别名
				}
				SqlBuildingHelper.appendIdentifier(sqlBuilder, attr.getColumnName(), configuration);
				
				if(StringUtils.isNotBlank(mainEntityAttrName)) { //处理关联表字段别名
					String tableAlias = tableAliasDispacher.dispach(Segment.ATTR_ + mainEntityAttrName);
					String columnAlias = Segment.UNDER_LINE + tableAlias + Segment.UNDER_LINE + attr.getName();
					sqlBuilder.append(Segment.SPACE);
					SqlBuildingHelper.appendIdentifier(sqlBuilder, columnAlias, configuration);
				}
			}
		}

		sqlBuilder.append(Segment.FROM_B);
		buildTableSegment(sqlBuilder);

		String sqlTemplateTmp = expressionBuilder.toString();
		if (StringUtils.isNotBlank(sqlTemplateTmp)) {
			sqlTemplateTmp = renderConditionSql(sqlTemplateTmp, properties);

			if (!ORDER_BY_PT.matcher(sqlTemplateTmp).matches()) { // 如果没有查询条件时，则不用加WHERE
				sqlBuilder.append(Segment.WHERE_B);
			}
			sqlBuilder.append(sqlTemplateTmp);
		}

		sqlBuilder.append(Segment.SCRIPT_LABEL_END);
		
		//是否需要构造新的ResultMap，如果查询的字段只有主表的，就不需要
		List<ResultMapping> resultMappings = new ArrayList<>();
		if(selectEntityAttrMap.size() > 1 || (selectEntityAttrMap.size() == 1 && !selectEntityAttrMap.containsKey(StringUtils.EMPTY))) {
			MiluMapperBuilderAssistant assistant = new MiluMapperBuilderAssistant(configuration, null); //resource ignore
			
			selectEntityAttrMap.forEach((mainEntityAttrName, attributes) -> {
				if(StringUtils.isBlank(mainEntityAttrName)) {
					attributes.forEach(attribute -> {
						ResultMapping resultMapping = assistant.buildResultMapping(attribute.getOwner().getJavaType(), attribute.getName(), attribute.getColumnName(), attribute.getJavaType(), attribute.getJdbcType(), null, null, null, null, attribute.getTypeHandler(), attribute.isId() ? ID_FLAG_LIST : null);
						resultMappings.add(resultMapping);
					});
				} else {
					String tableAlias = tableAliasDispacher.dispach(Segment.ATTR_ + mainEntityAttrName);
					
					List<ResultMapping> refResultMappings = new ArrayList<>();
					attributes.forEach(attribute -> {
						String columnAlias = Segment.UNDER_LINE + tableAlias + Segment.UNDER_LINE + attribute.getName();
						ResultMapping resultMapping = assistant.buildResultMapping(attribute.getOwner().getJavaType(), attribute.getName(), columnAlias, attribute.getJavaType(), attribute.getJdbcType(), null, null, null, null, attribute.getTypeHandler(), attribute.isId() ? ID_FLAG_LIST : null);
						refResultMappings.add(resultMapping);
					});
					
					String resultMapId = entity.getJavaType().getName() + "#" + mainEntityAttrName + "$" + (refResultMappings.hashCode() + 31 * predicate.hashCode());
					Attribute attribute = entity.getAttribute(mainEntityAttrName);
					
					if(!configuration.hasResultMap(resultMapId)) {
						Class<?> javaType = attribute.getJavaType();
						if(attribute instanceof PluralAttribute) {
							javaType = ((PluralAttribute) attribute).getElementClass();
						}
						try {
							configuration.addResultMap(new ResultMap.Builder(configuration, resultMapId, javaType, refResultMappings, true).build());
						} catch (IllegalArgumentException e) {
							// 并发下的重复添加引起的异步，忽略
						}
					}
					
					ResultMapping resultMapping = assistant.buildResultMapping(attribute.getOwner().getJavaType(), attribute.getName(), null, attribute.getJavaType(), attribute.getJdbcType(), null, resultMapId, null, null, attribute.getTypeHandler(),  null);
					resultMappings.add(resultMapping);
				}
			});
		}

		return new BuildResult(sqlBuilder.toString(), resultMappings);
	}

	private Map<String, List<Attribute>> analyseAttrs(Set<String> attrs) {
		Map<String, List<Attribute>> entityAttrsMap = new HashMap<>();
		for (String attr : attrs) {
			//主体
			if ("*".equals(attr)) {
				List<Attribute> list = entityAttrsMap.computeIfAbsent(StringUtils.EMPTY, key -> { //空表示主体
					return new ArrayList<>();
				});
				Iterator<Attribute> it = entity.getAttributes().iterator();
				while (it.hasNext()) {
					Attribute attribute = it.next();
					if (attribute.isSelectable()) {
						list.add(attribute);
					}
				}
				continue;
			}
			//外键/关联实体
			if(attr.contains("*")) {
				String refAttr = attr.substring(0, attr.indexOf("*"));
				Attribute attribute = entity.getAttribute(refAttr);
				if(attribute == null) {
					throw new SqlExpressionBuildingException(String.format("表达式%s找不到相关实体属性：%s", attr, refAttr));
				}
				if(!attribute.isReference()) {
					throw new SqlExpressionBuildingException(String.format("表达式%s中%s不是关联（外键）属性", attr, refAttr));
				}
				Entity refEntity = configuration.getMetaModel().getEntity(attribute.getEntityClass());
				if(refEntity == null) {
					throw new SqlExpressionBuildingException(String.format("关联（外键）属性%s无对应实体类信息", attribute.getEntityClass().getName()));
				}
				
				List<Attribute> list = entityAttrsMap.computeIfAbsent(refAttr, key -> {
					return new ArrayList<>();
				});
				Iterator<Attribute> it = refEntity.getAttributes().iterator();
				while (it.hasNext()) {
					Attribute curAttr = it.next();
					if (curAttr.isSelectable()) {
						list.add(curAttr);
					}
				}
				continue;
			}
			
			//主体的属性
			if(entity.hasAttribute(attr)) {
				entityAttrsMap.computeIfAbsent(StringUtils.EMPTY, key -> {
					return new ArrayList<>();
				}).add(entity.getAttribute(attr));
				continue;
			}
			
			//外键实体的属性
			Map<String, String> possiblePropertyMap = SqlBuildingHelper.possibleProperty(attr);
			Iterator<Entry<String, String>> possiblePropertyIt = possiblePropertyMap.entrySet().iterator();
			boolean match = false;
			while(possiblePropertyIt.hasNext()) {
				Entry<String, String> possibleProperty = possiblePropertyIt.next();
				if(!entity.hasAttribute(possibleProperty.getKey())) {
					continue;
				}
				
				Attribute refAttribute = entity.getAttribute(possibleProperty.getKey());
				Entity refAttrEntity = configuration.getMetaModel().getEntity(refAttribute.getEntityClass());
				if(!refAttrEntity.hasAttribute(possibleProperty.getValue())) {
					continue;
				}
				
				List<Attribute> list = entityAttrsMap.computeIfAbsent(possibleProperty.getKey(), key -> {
					return new ArrayList<>();
				});
				list.add(refAttrEntity.getAttribute(possibleProperty.getValue()));
				match = true;
				break;
			}
			
			if(!match) {
				throw new SqlExpressionBuildingException(String.format("未知的属性：%s", attr));
			}
		}
		
		return entityAttrsMap;
	}
	
	@Data
	@AllArgsConstructor
	public static class BuildResult {
		
		private String sqlTemplate;
		
		private List<ResultMapping> resultMappings;
	}
}
