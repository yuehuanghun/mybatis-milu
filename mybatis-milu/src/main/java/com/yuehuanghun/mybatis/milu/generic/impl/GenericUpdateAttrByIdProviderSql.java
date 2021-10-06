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
package com.yuehuanghun.mybatis.milu.generic.impl;

import java.util.Collection;
import java.util.Map;

import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.criteria.lambda.LambdaReflections;
import com.yuehuanghun.mybatis.milu.criteria.lambda.SerializableFunction;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.generic.GenericCachingProviderSql;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.tool.Constants;
import com.yuehuanghun.mybatis.milu.tool.Segment;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

/**
 * 更新指定属性<br>
 * 除了指定属性外，@Version及@AttributeOptions中声明为更新时填充的属性也会被更新<br>
 * 指定更新属性为@Version属性是无意义的<br>
 * 指定更新属性如果为@AttributeOptions声明为更新时填充的属性，则以指定值为准
 * @author yuehuanghun
 *
 */
public class GenericUpdateAttrByIdProviderSql extends GenericCachingProviderSql {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		Entity entity = context.getEntity();
		Map paramMap = (Map)params;
		Object attrNameObj = paramMap.get(Constants.ATTR_NAME);
		if(attrNameObj == null || (attrNameObj instanceof String && StringUtils.isBlank((String)attrNameObj))) {
			throw new SqlExpressionBuildingException("attrName不能为空");
		}
		if(attrNameObj instanceof SerializableFunction) {
			paramMap.put(Constants.ATTR_NAME, LambdaReflections.fnToFieldName((SerializableFunction) attrNameObj));
		}
		
		if(entity.getAttribute((String)paramMap.get(Constants.ATTR_NAME)) == null) {
			throw new SqlExpressionBuildingException(String.format("实体类%s不存在属性%s", entity.getJavaType().getName(), paramMap.get(Constants.ATTR_NAME)));
		}
		
		try {
			Object entityObj = entity.getJavaType().newInstance();
			
			paramMap.put(Constants.ENTITY, entityObj);
			SqlBuildingHelper.fill(params, false, context.getConfiguration());
			return super.provideSql(context, params);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new SqlExpressionBuildingException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected String getCacheKey(GenericProviderContext context, Object params) {
		return context.getMapperType().getName() + "-" + ((Map)params).get(Constants.ATTR_NAME);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public String provideCachingSql(GenericProviderContext context, Object params) {
		Entity entity = context.getEntity();
		Collection<Attribute> attributes = entity.getAttributes();
		StringBuilder sqlBuilder = new StringBuilder(256).append(Segment.SCRIPT_LABEL);
		
		sqlBuilder.append(Segment.UPDATE).append(wrapIdentifier(entity.getTableName(), context));
		sqlBuilder.append(Segment.SET_LABEL);
		
		String updateAttrName = (String)((Map)params).get(Constants.ATTR_NAME);
		for(Attribute attr : attributes) {
			if(!updateAttrName.equals(attr.getName()) && !attr.isUpdateable()) { //指定更新的属性允许为被声明为不可更新的属性
				continue;
			}
			if(attr.isVersion()) { //版本号 + 1，总是+1，如果指定更新的属性为一个Version属性，则更新是无效的
				sqlBuilder.append(wrapIdentifier(attr.getColumnName(), context)).append(" = #{entity.").append(attr.getName()).append("} + 1,");
			} else {
				if(updateAttrName.equals(attr.getName())) { //指定更新属性总是会被更新的
					sqlBuilder.append(wrapIdentifier(attr.getColumnName(), context)).append(" = #{").append(Constants.VALUE).append("}, ");
				} else {
					if(attr.getUpdateMode() == Mode.NOT_EMPTY && CharSequence.class.isAssignableFrom(attr.getJavaType())) {
						sqlBuilder.append(" <if test=\"entity.").append(attr.getName()).append(" != null and entity.").append(attr.getName()).append("!=''\">").append(wrapIdentifier(attr.getColumnName(), context)).append(" = #{entity.").append(attr.getName()).append("}, </if> ");
					} else {
						sqlBuilder.append(" <if test=\"entity.").append(attr.getName()).append(" != null\">").append(wrapIdentifier(attr.getColumnName(), context)).append(" = #{entity.").append(attr.toParameter()).append("}, </if> ");
					}
				}
			}
			
		}
		sqlBuilder.append(Segment.SET_LABEL_END);
		
		Attribute idAttr = entity.getId();
		if(idAttr == null) {
			throw new SqlExpressionBuildingException("id属性不存在");
		}
		sqlBuilder.append(Segment.WHERE_B).append(wrapIdentifier(idAttr.getColumnName(), context)).append(" = #{").append(Constants.ID).append(Segment.RIGHT_BRACE);

		sqlBuilder.append(Segment.SCRIPT_LABEL_END);
		
		return sqlBuilder.toString();
	}

	@Override
	public String getMethodName() {
		return "updateAttrById";
	}

}
