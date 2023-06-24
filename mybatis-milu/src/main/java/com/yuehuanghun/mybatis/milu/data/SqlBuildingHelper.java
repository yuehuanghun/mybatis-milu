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
package com.yuehuanghun.mybatis.milu.data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.ibatis.scripting.xmltags.OgnlCache;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.annotation.ExampleQuery.MatchType;
import com.yuehuanghun.mybatis.milu.criteria.Join;
import com.yuehuanghun.mybatis.milu.criteria.Predicate;
import com.yuehuanghun.mybatis.milu.criteria.Predicates;
import com.yuehuanghun.mybatis.milu.criteria.QueryPredicate;
import com.yuehuanghun.mybatis.milu.data.Part.Type;
import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.filler.Filler;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.PluralAttribute;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.RangeCondition;
import com.yuehuanghun.mybatis.milu.metamodel.KeyType;
import com.yuehuanghun.mybatis.milu.metamodel.ref.ManyToManyReference;
import com.yuehuanghun.mybatis.milu.metamodel.ref.MappedReference;
import com.yuehuanghun.mybatis.milu.metamodel.ref.Reference;
import com.yuehuanghun.mybatis.milu.tool.Constants;
import com.yuehuanghun.mybatis.milu.tool.InstanceUtils;
import com.yuehuanghun.mybatis.milu.tool.Segment;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;
import com.yuehuanghun.mybatis.milu.tool.converter.ExampleQueryConverter;

public class SqlBuildingHelper {
	public static void analyseDomain(Entity entity, Collection<String> properties, TableAliasDispacher tableAliasDispacher, MiluConfiguration configuration, Map<String, String> joinExpressMap, Map<String, String> joinQueryColumnNap) {
		analyseDomain(entity, properties, tableAliasDispacher, configuration, joinExpressMap, joinQueryColumnNap, Collections.emptyMap());
	}
	public static void analyseDomain(Entity entity, Collection<String> properties, TableAliasDispacher tableAliasDispacher, MiluConfiguration configuration, Map<String, String> joinExpressMap, Map<String, String> joinQueryColumnNap, Map<String, Join> joinMap) {
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
						if(attribute.isReference()) {
							refClass = attribute.getEntityClass();
						} else {
							if(PluralAttribute.class.isInstance(attribute)) {
								refClass = ((PluralAttribute) attribute).getElementClass();
							} else {
								refClass = attribute.getJavaType();
							}
						}
						
						Entity refEntity = configuration.getMetaModel().getEntity(refClass);
						
						if(!refEntity.hasAttribute(possiblePropertyMap.get(prop))) {
							continue;
						}
						
						Reference reference = entity.getReference(prop);

						String inverseTableAlias = tableAliasDispacher.dispach(Segment.ATTR_ + reference.getAttributeName());
						
						String joinExpression;
						String joinPredicateExpression;
						if(joinMap.containsKey(prop)) {
							joinExpression = joinMap.get(prop).getJoinMode().getExpression();
							joinPredicateExpression = joinMap.get(prop).getPredicateExpression();
						} else if(joinMap.containsKey(Constants.ANY_REF_PROPERTY)) {
							joinExpression = joinMap.get(Constants.ANY_REF_PROPERTY).getJoinMode().getExpression();
							joinPredicateExpression = null;
						} else {
							joinExpression = Segment.INNER_JOIN_B;
							joinPredicateExpression = null;
						}
						
						if(reference instanceof ManyToManyReference) {
							joinExpressMap.computeIfAbsent(prop, key -> {
								ManyToManyReference m2mRef = (ManyToManyReference) reference;
								
								StringBuilder joinExpressBuilder = new StringBuilder();
								String joinTableAlias = tableAliasDispacher.dispach(Segment.TABLE_ + m2mRef.getJoinTableName());
								
								joinExpressBuilder.append(joinExpression);
								appendIdentifier(joinExpressBuilder, m2mRef.getJoinTableName(), configuration);
								joinExpressBuilder.append(Segment.SPACE).append(joinTableAlias)
								    .append(Segment.ON_BRACKET);

								//多个join条件
								String joinCond = m2mRef.getJoinConditionList().stream().map(cond -> {									
									StringBuilder temp =  new StringBuilder(joinTableAlias).append(Segment.DOT);
									appendIdentifier(temp, cond.getColumnName(), configuration);
									temp.append(Segment.EQUALS_B).append(mainTableAlias).append(Segment.DOT);
									appendIdentifier(temp, cond.getInverseColumnName(), configuration);
									return temp.toString();
								}).collect(Collectors.joining(Segment.AND_B));
								
								joinExpressBuilder.append(joinCond);
								joinExpressBuilder.append(Segment.RIGHT_BRACKET);
								
								joinExpressBuilder.append(joinExpression);
								appendSchema(joinExpressBuilder, reference.getInverseCatalog(), reference.getInverseSchema(), configuration);
								appendIdentifier(joinExpressBuilder, reference.getInverseTableName(), configuration);
								joinExpressBuilder.append(Segment.SPACE).append(inverseTableAlias)
							        .append(Segment.ON_BRACKET);
												
								//多个join条件
								joinCond = m2mRef.getInverseJoinConditionList().stream().map(cond -> {									
									StringBuilder temp =  new StringBuilder(joinTableAlias).append(Segment.DOT);
									appendIdentifier(temp, cond.getColumnName(), configuration);
									temp.append(Segment.EQUALS_B).append(inverseTableAlias).append(Segment.DOT);
									appendIdentifier(temp, cond.getInverseColumnName(), configuration);
									return temp.toString();
								}).collect(Collectors.joining(Segment.AND_B));
								
								joinExpressBuilder.append(joinCond);
								
								if(StringUtils.isNotBlank(joinPredicateExpression)) { // 其它指定联结条件
									joinExpressBuilder.append(Segment.AND_B);
									boolean hasOr = joinPredicateExpression.contains(Segment.OR_B); // 根据是否有OR条件决定是否需要括起"其它指定联结条件"
									if(hasOr) {
										joinExpressBuilder.append(Segment.LEFT_BRACKET);
									}
									joinExpressBuilder.append(joinPredicateExpression);
									if(hasOr) {
										joinExpressBuilder.append(Segment.RIGHT_BRACKET);
									}
								}
								
								joinExpressBuilder.append(Segment.RIGHT_BRACKET);
								return joinExpressBuilder.toString();
							});
						} else {
							joinExpressMap.computeIfAbsent(prop, key -> {
								MappedReference ref = (MappedReference) reference;
								StringBuilder joinExpressBuilder = new StringBuilder();
								joinExpressBuilder.append(joinExpression);
								appendSchema(joinExpressBuilder, reference.getInverseCatalog(), reference.getInverseSchema(), configuration);
								appendIdentifier(joinExpressBuilder, reference.getInverseTableName(), configuration);
								joinExpressBuilder.append(Segment.SPACE).append(inverseTableAlias)
								    .append(Segment.ON_BRACKET);
							
								//多个join条件
								String joinCond = ref.getJoinConditionList().stream().map(cond -> {									
									StringBuilder temp =  new StringBuilder(mainTableAlias).append(Segment.DOT);
									appendIdentifier(temp, cond.getColumnName(), configuration);
									temp.append(Segment.EQUALS_B).append(inverseTableAlias).append(Segment.DOT);
									appendIdentifier(temp, cond.getInverseColumnName(), configuration);
									return temp.toString();
								}).collect(Collectors.joining(Segment.AND_B));
								
								joinExpressBuilder.append(joinCond);
								
								if(StringUtils.isNotBlank(joinPredicateExpression)) { // 其它指定联结条件
									joinExpressBuilder.append(Segment.AND_B);
									boolean hasOr = joinPredicateExpression.contains(Segment.OR_B); // 根据是否有OR条件决定是否需要括起"其它指定联结条件"
									if(hasOr) {
										joinExpressBuilder.append(Segment.LEFT_BRACKET);
									}
									joinExpressBuilder.append(joinPredicateExpression);
									if(hasOr) {
										joinExpressBuilder.append(Segment.RIGHT_BRACKET);
									}
								}
								
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
	
	public static Attribute getAttribute(MiluConfiguration configuration, Entity entity, String attrName, boolean assertNotNull) {
		Attribute attribute = entity.getAttribute(attrName);
		if(attribute != null) {
			return attribute;
		}
		
		Map<String, String> possiblePropertyMap = possibleProperty(attrName);
		Iterator<String> propIt = possiblePropertyMap.keySet().iterator();
		while(propIt.hasNext()) {
			String prop = propIt.next();
			if(!entity.hasAttribute(prop)){
				continue;
			}
			
			Attribute refAttr = entity.getAttribute(prop);
			
			if(refAttr.isAssociation() || refAttr.isCollection()) {
				Class<?> refClass;
				if(refAttr.isReference()) {
					refClass = refAttr.getEntityClass();
				} else {
					if(PluralAttribute.class.isInstance(refAttr)) {
						refClass = ((PluralAttribute) refAttr).getElementClass();
					} else {
						refClass = refAttr.getJavaType();
					}
				}
				
				Entity refEntity = configuration.getMetaModel().getEntity(refClass);
				
				if(!refEntity.hasAttribute(possiblePropertyMap.get(prop))) {
					continue;
				}
				
				return refEntity.getAttribute(possiblePropertyMap.get(prop));
			}
		}
		
		if(assertNotNull) {
			throw new SqlExpressionBuildingException(String.format("未知的查询属性：%s", attrName));
		}
		
		return null;
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
	
	public static void appendSchema(StringBuilder stringBuilder, String catalog, String schema, MiluConfiguration configuration) {
		if(StringUtils.isBlank(catalog) && StringUtils.isBlank(schema)) {
			return;
		}
		
		if(StringUtils.isBlank(catalog)) {
			if(configuration.getPlaceholderResolver() != null) {
				schema = configuration.getPlaceholderResolver().resolvePlaceholder(schema);
			}
			appendIdentifier(stringBuilder, schema, configuration);
			stringBuilder.append(Segment.DOT);
		} else {
			if(configuration.getPlaceholderResolver() != null) {
				catalog = configuration.getPlaceholderResolver().resolvePlaceholder(catalog);
			}
			appendIdentifier(stringBuilder, catalog, configuration);
			stringBuilder.append(Segment.DOT);
			
			if(StringUtils.isNotBlank(schema)) {
				if(configuration.getPlaceholderResolver() != null) {
					schema = configuration.getPlaceholderResolver().resolvePlaceholder(schema);
				}
				appendIdentifier(stringBuilder, schema, configuration);
			}
			stringBuilder.append(Segment.DOT); // 不管schema有没有都加一个点“.”
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
	
	public static String wrapTableName(Entity entity, MiluConfiguration configuration) {
		if(StringUtils.isBlank(entity.getCatalog()) && StringUtils.isBlank(entity.getSchema())) {
			return wrapIdentifier(entity.getTableName(), configuration);
		}
		StringBuilder sBuilder = new StringBuilder();
		appendSchema(sBuilder, entity.getCatalog(), entity.getSchema(), configuration);
		
		return sBuilder.append(wrapIdentifier(entity.getTableName(), configuration)).toString();
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
				Class<?> entityClass = getEntityClass(el, configuration);
				if(entityClass != null) {
					boolean hasFiller = fillEntity(configuration.getMetaModel().getEntity(entityClass), el, insert);
					if(!hasFiller) {
						break;
					}
				} else {
					break;
				}
			}
		} else {
			Class<?> entityClass = getEntityClass(param, configuration);
			if(entityClass != null) { 
				fillEntity(configuration.getMetaModel().getEntity(entityClass), param, insert);
			}
		}
	}
	
	//支持实体类的子类
	private static Class<?> getEntityClass(Object obj, MiluConfiguration configuration) {
		Class<?> clazz = obj.getClass();
		while(clazz != null && clazz != Object.class) {
			if(configuration.getMetaModel().hasEntity(clazz)) {
				return clazz;
			}
			clazz = clazz.getSuperclass();
		}
		
		return null;
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
	    return String.format(configuration.getDialect().getPartTypeExpression(type), Segment.HASH_EXAMPLE  + attr.toParameter() + Segment.RIGHT_BRACE);
	}
	
	public static String matchExpression(Part.Type type, String keyName, Attribute forAttr, MiluConfiguration configuration) {
		if(type == Part.Type.IN || type == Part.Type.NOT_IN) {
			String expression = String.format(configuration.getDialect().getPartTypeExpression(type), Segment.EXAMPLE_TO_COLLECTION + keyName + Segment.RIGHT_BRACKET);
			expression = forAttr.formatParameterExpression(expression);
			return expression;
		}
		return String.format(configuration.getDialect().getPartTypeExpression(type), Segment.HASH_EXAMPLE + forAttr.toParameter(keyName) + Segment.RIGHT_BRACE);
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
	
	/**
	 * example条件转为criteria条件
	 * @param entity 实体类对象
	 * @param example 查询样例条件
	 * @return 动态查询条件
	 */
	public static QueryPredicate exampleToQueryPredicate(Entity entity, Object example) {
		return Predicates.queryPredicate().and(exampleToPredicate(entity, example));
	}
	
	/**
	 * example条件转为criteria条件
	 * @param entity 实体类对象
	 * @param example 查询样例条件
	 * @return 动态查询条件
	 */
	public static Predicate exampleToPredicate(Entity entity, Object example) {
		Predicate predicate = Predicates.predicate();
		
		for(Attribute attr : entity.getAttributes()) {
			String attrName = attr.getName();
			
			if(attr.getExampleMatchType() == MatchType.EQUAL) {
				predicate.eq(attrName, OgnlCache.getValue(attrName, example));
			} else if(attr.getExampleMatchType() == MatchType.CONTAIN) {
				predicate.contain(attrName, OgnlCache.getValue(attrName, example));
			} else if(attr.getExampleMatchType() == MatchType.LIKE) {
				predicate.like(attrName, OgnlCache.getValue(attrName, example));
			} else if(attr.getExampleMatchType() == MatchType.START_WITH) {
				predicate.startWith(attrName, OgnlCache.getValue(attrName, example));
			} else if(attr.getExampleMatchType() == MatchType.END_WITH) {
				predicate.endWith(attrName, OgnlCache.getValue(attrName, example));
			}
			
			for(RangeCondition condition : attr.getRangeList()) {
				Object value = OgnlCache.getValue(condition.getKeyName(), example);
				if(value == null) {
					continue;
				}
				
				ExampleQueryConverter converter = InstanceUtils.getSigleton(condition.getValueConverter());
	
				if(condition.getKeyType() == KeyType.IN) {
					Object collection = StringUtils.toCollection(value);
					List<Object> values = new ArrayList<>();
					if(collection.getClass().isArray()) {
						for(int i = 0; i < Array.getLength(collection); i++) {
							values.add(converter.convert(Array.get(collection, i), condition.getAttrJavaType(), condition.getKeyName(), condition.getKeyType()));
						}
						collection = values;
					} else if(collection instanceof Collection) {
						Iterator<?> it = ((Collection<?>)collection).iterator();
						while(it.hasNext()) {
							values.add(converter.convert(it.next(), condition.getAttrJavaType(), condition.getKeyName(), condition.getKeyType()));
						}
						collection = values;
					}
					
					predicate.in(attrName, collection);
					continue;
				}
				
				Object convertedValue = converter.convert(value, condition.getAttrJavaType(), condition.getKeyName(), condition.getKeyType());
				
				if(condition.getKeyType() == KeyType.START) {
					if(condition.getType() == Type.GREATER_THAN) {
						predicate.gt(attrName, convertedValue);
					} else {
						predicate.gte(attrName, convertedValue);
					}
					continue;
				}
				
				if(condition.getKeyType() == KeyType.END) {
					if(condition.getType() == Type.LESS_THAN) {
						predicate.lt(attrName, convertedValue);
					} else {
						predicate.lte(attrName, convertedValue);
					}
					continue;
				}
			}
		}
		
		return predicate;
	}
}
