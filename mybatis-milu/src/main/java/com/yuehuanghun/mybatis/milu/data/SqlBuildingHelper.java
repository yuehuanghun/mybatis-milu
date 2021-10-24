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
package com.yuehuanghun.mybatis.milu.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.filler.Filler;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.PluralAttribute;
import com.yuehuanghun.mybatis.milu.metamodel.ref.ManyToManyReference;
import com.yuehuanghun.mybatis.milu.metamodel.ref.ManyToManyReference.JoinTable;
import com.yuehuanghun.mybatis.milu.metamodel.ref.Reference;
import com.yuehuanghun.mybatis.milu.tool.Segment;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

public class SqlBuildingHelper {
	public static void analyseDomain(Entity entity, Collection<String> properties, TableAliasDispacher tableAliasDispacher, MiluConfiguration configuration, Map<String, String> joinExpressMap, Map<String, String> joinQueryColumnNap) {
		String mainTableAlias = tableAliasDispacher.dispach(Segment.TABLE_ + entity.getTableName());
		for(String property : properties) {
			if(entity.hasAttribute(property)) {
				Attribute attribute = entity.getAttribute(property);
				if(!attribute.isSelectable()) {
					throw new SqlExpressionBuildingException(String.format("未知的查询属性：%s", property));
				} else {
					continue;
				}
			}
			//关联表
			boolean hasAttr = false;
			Map<String, String> possiblePropertyMap = possibleProperty(property);
			Iterator<String> propIt = possiblePropertyMap.keySet().iterator();
			while(propIt.hasNext()) {
				String prop = propIt.next();
				if(entity.hasAttribute(prop)){
					Attribute attribute = entity.getAttribute(prop);
					
					if(attribute.isAssociation() || attribute.isCollection()) {
						Class<?> refClass;
						if(PluralAttribute.class.isInstance(attribute)) {
							refClass = ((PluralAttribute) attribute).getElementClass();
						} else {
							refClass = attribute.getJavaType();
						}
						
						Entity refEntity = configuration.getMetaModel().getEntity(refClass);
						
						if(!refEntity.hasAttribute(possiblePropertyMap.get(prop))) {
							throw new SqlExpressionBuildingException(String.format("未知的查询属性：%s", property));
						}
						
						Reference reference = entity.getReference(prop);

						String inverseTableAlias = tableAliasDispacher.dispach(Segment.ATTR_ + reference.getAttributeName());
						
						if(reference instanceof ManyToManyReference) {
							joinExpressMap.computeIfAbsent(reference.getInverseTableName(), key -> {
								ManyToManyReference m2mRef = (ManyToManyReference) reference;
								JoinTable joinTable = m2mRef.getJoinTable();
								
								StringBuilder joinExpressBuilder = new StringBuilder();
								String joinTableAlias = tableAliasDispacher.dispach(Segment.TABLE_ + joinTable.getTableName());
								
								joinExpressBuilder.append(Segment.INNER_JOIN_B);
								appendIdentifier(joinExpressBuilder, joinTable.getTableName(), configuration);
								joinExpressBuilder.append(Segment.SPACE).append(joinTableAlias)
								    .append(Segment.ON_BRACKET).append(mainTableAlias).append(Segment.DOT);
								appendIdentifier(joinExpressBuilder, reference.getColumnName(), configuration);
								joinExpressBuilder.append(Segment.EQUALS_B).append(joinTableAlias).append(Segment.DOT);
								appendIdentifier(joinExpressBuilder, joinTable.getColumnName(), configuration);
								joinExpressBuilder.append(Segment.RIGHT_BRACKET);
								
								joinExpressBuilder.append(Segment.INNER_JOIN_B);
								appendIdentifier(joinExpressBuilder, reference.getInverseTableName(), configuration);
								joinExpressBuilder.append(Segment.SPACE).append(inverseTableAlias)
							        .append(Segment.ON_BRACKET).append(joinTableAlias).append(Segment.DOT);
								appendIdentifier(joinExpressBuilder, joinTable.getInverseColumnName(), configuration);
								joinExpressBuilder.append(Segment.EQUALS_B).append(inverseTableAlias).append(Segment.DOT);
								appendIdentifier(joinExpressBuilder, reference.getInverseColumnName(), configuration);
								joinExpressBuilder.append(Segment.RIGHT_BRACKET);
								return joinExpressBuilder.toString();
							});
						} else {
							joinExpressMap.computeIfAbsent(reference.getInverseTableName(), key -> {
								StringBuilder joinExpressBuilder = new StringBuilder();
								joinExpressBuilder.append(Segment.INNER_JOIN_B);
								appendIdentifier(joinExpressBuilder, reference.getInverseTableName(), configuration);
								joinExpressBuilder.append(Segment.SPACE).append(inverseTableAlias)
								    .append(Segment.ON_BRACKET).append(mainTableAlias).append(Segment.DOT);
								appendIdentifier(joinExpressBuilder, reference.getColumnName(), configuration);
								joinExpressBuilder.append(Segment.EQUALS_B).append(inverseTableAlias).append(Segment.DOT);
								appendIdentifier(joinExpressBuilder, reference.getInverseColumnName(), configuration);
								joinExpressBuilder.append(Segment.RIGHT_BRACKET);
								return joinExpressBuilder.toString();
							});
						}
						
						Attribute refAttr = refEntity.getAttribute(possiblePropertyMap.get(prop));
						joinQueryColumnNap.put(property, inverseTableAlias + Segment.DOT + wrapIdentifier(refAttr.getColumnName(), configuration));

						hasAttr = true;
						break;
					}
				}
			}
			if(!hasAttr) {
				throw new SqlExpressionBuildingException(String.format("未知的查询属性：%s", property));
			}
		}
	}
	
	public static Map<String, String> possibleProperty(String property) {
		Map<String, String> possibleProperty = new HashMap<>();
		
		if(property.contains("_")) {
			String[] rs = property.split("_");
			possibleProperty.put(rs[0], StringUtils.uncapitalize(rs[1]));
		}
		
		String[] rs = property.split("(?=\\p{Upper})");
		if(rs.length == 1) {
			throw new SqlExpressionBuildingException(String.format("未知的查询属性：%s", property));
		} else if(rs.length == 2) {
			possibleProperty.put(rs[0], StringUtils.uncapitalize(rs[1]));
		} else {
			for(int i = 1; i < rs.length; i++) {
				possibleProperty.put(StringUtils.concat(Arrays.copyOfRange(rs, 0, i)), StringUtils.uncapitalize(StringUtils.concat(Arrays.copyOfRange(rs, i, rs.length))));
			}
		}
		
		return possibleProperty;
	}
	
	public static void appendIdentifier(StringBuilder stringBuilder , String identifier, MiluConfiguration configuration) {
		if(!configuration.isIdentifierWrapQuote()) {
			stringBuilder.append(identifier);
			return;
		}
		String identifierQuoteString = configuration.getDbMeta().getIdentifierQuoteString();
		if(StringUtils.isBlank(identifierQuoteString)) {
			stringBuilder.append(identifier);
		} else {
			stringBuilder.append(identifierQuoteString).append(identifier).append(identifierQuoteString);
		}
	}
	
	public static String wrapIdentifier(String identifier, MiluConfiguration configuration) {
		if(!configuration.isIdentifierWrapQuote()) {
			return identifier;
		}
		String identifierQuoteString = configuration.getDbMeta().getIdentifierQuoteString();
		if(StringUtils.isBlank(identifierQuoteString)) {
			return identifier;
		} else {
			return identifierQuoteString + identifier + identifierQuoteString;
		}
	}
	
	public static class TableAliasDispacher {
		private int curIndex = 0;
		private static String[] ALIAS = new String[] {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
	
		private Map<String, Integer> tableAliasIndexMap = new HashMap<>();
		public String dispach(String tableName) {
			Integer index = tableAliasIndexMap.computeIfAbsent(tableName, key -> {
				return curIndex++;
			});
			return ALIAS[index];
		}
	}
	
	public static String columnHolder(String attrName) {
		return Segment.DOLLAR + attrName + Segment.DOLLAR;
	}
	
	/**
	 * 自动填充由@Filler声明的属性
	 * @param param 输入参数
	 * @param insert 新增/更新
	 * @param configuration 配置
	 */
	public static void fill(Object param, boolean insert, MiluConfiguration configuration) {
		if(Map.class.isInstance(param)) {
			Collection<?> values = ((Map<?,?>) param).values();
			for(Object obj : values) {
				fillEntity(obj, insert, configuration);
			}
		} else {
			fillEntity(param, insert, configuration);
		}
	}
	
	private static void fillEntity(Object param, boolean insert, MiluConfiguration configuration) {
		if(param == null) {
			return;
		}
		if(Iterable.class.isInstance(param)) {
			Iterator<?> iterator = ((Iterable<?>)param).iterator();
			while(iterator.hasNext()) {
				Object el = iterator.next();
				if(el == null) {
					continue;
				}
				if(configuration.getMetaModel().hasEntity(el.getClass())) {
					boolean hasFiller = fillEntity(configuration.getMetaModel().getEntity(el.getClass()), el, insert);
					if(!hasFiller) {
						break;
					}
				} else {
					break;
				}
			}
		} else {
			if(configuration.getMetaModel().hasEntity(param.getClass())) {
				fillEntity(configuration.getMetaModel().getEntity(param.getClass()), param, insert);
			}
		}
	}
	
	private static boolean fillEntity(Entity entity, Object target, boolean insert) {
		List<Filler> fillers;
		if(insert) {
			fillers = entity.getOnInsertFillers();
		} else {
			fillers = entity.getOnUpdateFillers();
		}
		
		if(fillers.isEmpty()) {
			return false;
		}
		
		for(Filler filler : fillers) {
			filler.setValue(target, insert);
		}
		
		return true;
	}
	
	public static String matchExpression(Attribute attr, MiluConfiguration configuration) {
		Part.Type type;
	    switch (attr.getExampleMatchType()) {
	    	case EQUAL:
	    		type = Part.Type.SIMPLE_PROPERTY;
	    		break;
	    	case CONTAIN:
	    		type = Part.Type.CONTAINING;
	    		break;
	    	case END_WITH:
	    		type = Part.Type.ENDING_WITH;
	    		break;
	    	case START_WITH:
	    		type = Part.Type.STARTING_WITH;
	    		break;
	    	default:
	    		type = Part.Type.SIMPLE_PROPERTY;
	    		break;
	    }
	    return String.format(configuration.getDialect().getPartTypeExpression(type), Segment.HASH_EXAMPLE  + attr.getName() + Segment.RIGHT_BRACE);
	}
	
	public static String matchExpression(Part.Type type, String keyName, MiluConfiguration configuration) {
		if(type == Part.Type.IN) {
			return String.format(configuration.getDialect().getPartTypeExpression(type), Segment.EXAMPLE_TO_COLLECTION + keyName + Segment.RIGHT_BRACKET);
		}
		return String.format(configuration.getDialect().getPartTypeExpression(type), Segment.HASH_EXAMPLE + keyName + Segment.RIGHT_BRACE);
	}
	
	//转换PageHelper中的排序中的属性为column
	public static void convertLocalPageOrder(Entity entity, MiluConfiguration configuration) {
		Page<Object> page = PageHelper.getLocalPage();
		if(page == null) {
			return;
		}
		String orders = page.getOrderBy();
		if(StringUtils.isBlank(orders)) {
			return;
		}
		
		String[] orderArray = orders.split(",");
		if(orderArray.length == 1) {
			String[] orderEl = orderArray[0].trim().split("\\s");
			Attribute attr = entity.getAttribute(orderEl[0]);
			if(attr != null) {
				page.setOrderBy(wrapIdentifier(attr.getColumnName(), configuration) + (orderEl.length == 1 ? StringUtils.EMPTY : Segment.SPACE + orderEl[1]));
			}
		} else {
			Map<String, String> orderMap = new LinkedHashMap<>();
			for(String order : orderArray) {
				String[] orderEl = order.trim().split("\\s");
				Attribute attr = entity.getAttribute(orderEl[0]);
				if(attr != null) {
					orderMap.put(wrapIdentifier(attr.getColumnName(), configuration), orderEl.length == 1 ? StringUtils.EMPTY : orderEl[1]);
				} else {
					orderMap.put(orderEl[0], orderEl.length == 1 ? StringUtils.EMPTY : orderEl[1]);
				}
			}
			String orderEl = orderMap.entrySet().stream().map(item -> item.getKey() + Segment.SPACE + item.getValue()).collect(Collectors.joining(", "));
			page.setOrderBy(orderEl);
		}
	}
}
