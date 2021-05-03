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

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.LockModeType;

import org.apache.ibatis.annotations.Param;

import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.annotation.StatementOptions;
import com.yuehuanghun.mybatis.milu.data.Part.Type;
import com.yuehuanghun.mybatis.milu.data.PartTree.OrPart;
import com.yuehuanghun.mybatis.milu.data.Sort.Order;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper.TableAliasDispacher;
import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.tool.Segment;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

public class SqlBuilder {
	private final Entity entity;
	private final PartTree partTree;
	private final MiluConfiguration configuration;
	private final Parameter[] parameters;
	
	private Set<String> selectAttrs = Collections.emptySet();
	private Set<String> exselectAttrs = Collections.emptySet();
	
	private TableAliasDispacher tableAliasDispacher = new TableAliasDispacher();
	private Map<String, String> joinExpressMap = new HashMap<>();
	private Map<String, String> joinQueryColumnNap = new HashMap<>();
	
	private LockModeType lockModeType = LockModeType.NONE;

	private SqlBuilder(Class<?> domainClazz, Method method, PartTree partTree, MiluConfiguration configuration) {
		super();
		this.entity = configuration.getMetaModel().getEntity(domainClazz);
		this.partTree = partTree;
		this.configuration = configuration;
		this.parameters = method.getParameters();
		setOptions(method);
	}
	
	public static SqlBuilder instance(Class<?> domainClazz, Method method, PartTree partTree, MiluConfiguration configuration) {
		return new SqlBuilder(domainClazz, method, partTree, configuration);
	}

	public String build() {
		StringBuilder sqlBuilder = new StringBuilder(1024);
		
		String mainTableAlias = tableAliasDispacher.dispach(Segment.TABLE_ + entity.getTableName());
		analyseDomain(entity);
		
		if(partTree.isDelete()) {
			sqlBuilder.append(Segment.DELETE_FROM);
		} else {
			sqlBuilder.append(Segment.SELECT);
			if(partTree.isCountProjection()) {
				sqlBuilder.append(Segment.COUNT_ALL);
			} else {
				if(partTree.isDistinct()) {
					sqlBuilder.append(Segment.DISTINCT);
				}
				
				Iterator<Attribute> it = entity.getAttributes().iterator();
				boolean first = true;
				while(it.hasNext()) {
					Attribute attr = it.next();
					if(!selectAttrs.isEmpty()) {
						if(!selectAttrs.contains(attr.getName())) {
							continue;
						}
					} else if(!exselectAttrs.isEmpty() && exselectAttrs.contains(attr.getName())) {
						continue;
					}
					if(attr.isSelectable()) {
						if(first) {
							first = false;
						} else {
							sqlBuilder.append(Segment.COMMA_B);
						}						
						if(!joinExpressMap.isEmpty()) { //没有关联查询时，不需要使用表别名
							sqlBuilder.append(mainTableAlias).append(Segment.DOT);
						}
						appendIdentifier(sqlBuilder, attr.getColumnName());
					}
				}
			}
			
			sqlBuilder.append(Segment.FROM_B);
		}
		
		appendIdentifier(sqlBuilder, entity.getTableName()); //表名
		if(!joinExpressMap.isEmpty()) { //没有关联查询时，不需要使用表别名
			sqlBuilder.append(Segment.SPACE).append(mainTableAlias);

            for(String joinExpress : joinExpressMap.values()) {
            	sqlBuilder.append(joinExpress);
            }
		}		
		
		Iterator<OrPart> orPartIt = partTree.iterator();
		int paramIndex = 0;
		if(orPartIt.hasNext()) {
			sqlBuilder.append(Segment.WHERE_B);
			boolean first = true;
			while(orPartIt.hasNext()) {
				if(first) {
					first = false;
				} else {
					sqlBuilder.append(Segment.OR_B);
				}
				OrPart orPart = orPartIt.next();
				Iterator<Part> partIt = orPart.iterator();
				boolean first2 = true;
				while(partIt.hasNext()) {
					if(first2) {
						first2 = false;
					} else {
						sqlBuilder.append(Segment.AND_B);
					}
					Part part = partIt.next();
					
					if(joinQueryColumnNap.containsKey(part.getProperty())) {
						sqlBuilder.append(joinQueryColumnNap.get(part.getProperty()));
					} else {
						if(!joinExpressMap.isEmpty()) {
							sqlBuilder.append(mainTableAlias).append(Segment.DOT);
						}
						appendIdentifier(sqlBuilder, entity.getAttribute(part.getProperty()).getColumnName()); //查询列名
					}					
					
					String express = configuration.getDialect().getPartTypeExpression(part.getType());
					
					int numberOfArguments = part.getNumberOfArguments();
					if(numberOfArguments > 0) {
						Object[] params = new String[numberOfArguments];
						for(int i = 0 ; i < numberOfArguments; i++) {
							//TODO 是否做parameters长度判断？
							if(part.getType() == Type.IN || part.getType() == Type.NOT_IN ) {
								params[i] = getMybatisParamName(parameters[paramIndex++]); //mybatis的顺序参数
							} else {
								params[i] = Segment.HASH_LEFT_BRACE + getMybatisParamName(parameters[paramIndex++]) + Segment.RIGHT_BRACE; //mybatis的顺序参数
							}
						}
						
						sqlBuilder.append(String.format(express, params));
					} else {
						sqlBuilder.append(express);
					}
				}
			}
		}
		
		Sort sort = partTree.getSort();
        if(sort != null) {
            Iterator<Order> it = sort.iterator();
            if(it.hasNext()) {
            	boolean first = true;
            	sqlBuilder.append(Segment.ORDER_BY);
            	while(it.hasNext()) {
            		if(first) {
						first = false;
					} else {
						sqlBuilder.append(Segment.COMMA_B);
					}
                    Order order = it.next();
                    
                    if(joinQueryColumnNap.containsKey(order.getProperty())) { //关联表字段排序
						sqlBuilder.append(joinQueryColumnNap.get(order.getProperty()));
					} else {
	                    if(!joinExpressMap.isEmpty()) {
							sqlBuilder.append(mainTableAlias).append(Segment.DOT);
						}
						appendIdentifier(sqlBuilder, entity.getAttribute(order.getProperty()).getColumnName());
					}
                    
                    sqlBuilder.append(Segment.SPACE).append(order.getDirection().name());
                }
            }
        }
        
        String sqlExpression = sqlBuilder.toString();
        if(partTree.isLimiting()) {
        	sqlExpression = configuration.getDialect().getTopLimitSql(sqlExpression, partTree.getMaxResults());
        }
        
        if(lockModeType != LockModeType.NONE) {
        	sqlExpression = configuration.getDialect().getLockSql(sqlExpression, lockModeType);
        }
        
        return Segment.SCRIPT_LABEL + sqlExpression + Segment.SCRIPT_LABEL_END;
	}
	
	private String getMybatisParamName(Parameter parameter) {
		if(parameter.isAnnotationPresent(Param.class)) {
			Param paramAnno = parameter.getAnnotation(Param.class);
			if(StringUtils.isNotBlank(paramAnno.value())) {
				return paramAnno.value();
			}
		}
		
		return parameter.getName();
	}
	
	private void analyseDomain(Entity entity) {
		Iterator<OrPart> orPartIt = partTree.iterator();
		
		List<String> properties = new ArrayList<>();
		while(orPartIt.hasNext()) {
			OrPart orPart = orPartIt.next();
			Iterator<Part> partIt = orPart.iterator();
			while(partIt.hasNext()) {
				properties.add(partIt.next().getProperty());
			}
		}
		
		Sort sort = partTree.getSort();
		if(partTree.getSort() != null) {
			for(Iterator<Order> it = sort.iterator(); it.hasNext();) {
				properties.add(it.next().getProperty());
			}
		}
		
		SqlBuildingHelper.analyseDomain(entity, properties, tableAliasDispacher, configuration, joinExpressMap, joinQueryColumnNap);
	}
	
	private void appendIdentifier(StringBuilder stringBuilder , String identifier) {
		SqlBuildingHelper.appendIdentifier(stringBuilder, identifier, configuration);
	}
	
	private void setOptions(Method method) {
		if(!method.isAnnotationPresent(StatementOptions.class)) {
			return;
		}
		StatementOptions options = method.getAnnotation(StatementOptions.class);
		if(StringUtils.isNotBlank(options.selects())) {
			selectAttrs = string2Set(options.selects());
			assertAttrExists(selectAttrs);
		}
		if(StringUtils.isNotBlank(options.exselects())) {
			exselectAttrs = string2Set(options.exselects());
			assertAttrExists(exselectAttrs);
		}
		
		this.lockModeType = options.lockModeType();
	}
	
	private Set<String> string2Set(String attrStr){
		String[] attrArray = attrStr.split(Segment.COMMA);
		Set<String> attrs = new HashSet<>();
		for(String attr: attrArray) {
			if(StringUtils.isNotBlank(attr)) {
				attrs.add(attr.trim());
			}
		}
		return attrs;
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
}
