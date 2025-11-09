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
package com.yuehuanghun.mybatis.milu.generic.impl;

import java.util.Collection;

import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.generic.GenericCachingProviderSql;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.FuncCol;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.FunctionAttribute;
import com.yuehuanghun.mybatis.milu.tool.Segment;

public class GenericFindByIdsProviderSql extends GenericCachingProviderSql {

	@Override
	public String provideCachingSql(GenericProviderContext context, Object params) {
		Entity entity = context.getEntity();
		Collection<Attribute> attributes = entity.getAttributes();
		StringBuilder sqlBuilder = new StringBuilder(512).append(Segment.SCRIPT_LABEL);
		
		sqlBuilder.append(Segment.SELECT);
		
		Attribute idAttr = entity.getId();
		if(idAttr == null) {
			throw new SqlExpressionBuildingException("实体类主键属性不存在。请确认实体类是否存在被@Id声明的属性。");
		}
		
		MiluConfiguration configuration = context.getConfiguration();
		
		boolean first = true;
		for(Attribute attr : attributes) {
			if(!attr.isSelectable()) {
				continue;
			}
			
			if(!first) {
				sqlBuilder.append(Segment.COMMA_B);
			} else {
				first = false;
			}
			
			if (attr instanceof FunctionAttribute) {
				FunctionAttribute funcAttr = ((FunctionAttribute) attr);
				FuncCol funcCol = funcAttr.getFuncCol(configuration.getDbMeta().getDbEnum());
				String funcExp = funcCol.getFuncExp();
				
				for(String attrName : funcCol.getVarAttrNames()) {
					String columnName = "";
					Attribute funcRefAttr = attr.getOwner().getAttribute(attrName); // 函数列表达式只能引用本实体的属性
					if(funcRefAttr == null) { // 如果为null，则直接当值表列名处理
						columnName += SqlBuildingHelper.wrapIdentifier(attrName, configuration);
					} else if(funcRefAttr instanceof FunctionAttribute) {
						throw new SqlExpressionBuildingException(String.format("函数属性%s表达式%s中的属性引用%s是一个函数属性，不允许函数属性", funcAttr.getName(), funcExp, attrName));
					} else if(funcRefAttr.isReference()) {
						throw new SqlExpressionBuildingException(String.format("函数属性%s表达式%s中的属性引用%s是一个关联属性，不允许关联属性", funcAttr.getName(), funcExp, attrName));
					} else { // 属性映射的表字段名
						columnName += SqlBuildingHelper.wrapIdentifier(funcRefAttr.getColumnName(), configuration);
					}
					
					funcExp = funcExp.replace("${" + attrName + "}", columnName); // 替换占位
				}
				sqlBuilder.append(funcExp).append(Segment.SPACE);
				SqlBuildingHelper.appendAlias(sqlBuilder, attr.getName(), configuration);
				
				continue;
			}
			
			sqlBuilder.append(wrapIdentifier(attr.getColumnName(), context));
		}
		
		
		sqlBuilder.append(Segment.FROM_B).append(wrapTableName(entity, context));
		sqlBuilder.append(Segment.WHERE_B).append(wrapIdentifier(idAttr.getColumnName(), context)).append(" IN (<foreach collection=\"ids\" item=\"id\" separator=\",\">#{id}</foreach>)");
		sqlBuilder.append(Segment.SCRIPT_LABEL_END);
		
		return sqlBuilder.toString();
	}

	@Override
	public String getMethodName() {
		return "findByIds";
	}

}
