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
package com.yuehuanghun.mybatis.milu.criteria;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.yuehuanghun.mybatis.milu.data.Part.Type;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.tool.Constants;
import com.yuehuanghun.mybatis.milu.tool.Segment;

import lombok.Getter;

/**
 * @see Conditions
 *
 */
public class ConditionImpl implements Condition {

	@Getter
	private Type type;

	@Getter
	private Object[] params;

	@Getter
	private String attributeName;

	protected ConditionImpl(Type type, String attributeName, Object... params) {
		this.type = type;
		this.attributeName = attributeName;
		this.params = params;
	}

	@Override
	public int renderSqlTemplate(GenericProviderContext context, StringBuilder expressionBuilder, Set<String> columns, int paramIndex) {
		Attribute attribute = SqlBuildingHelper.getAttribute(context.getConfiguration(), context.getEntity(), getAttributeName(), true);
		
		String expression = context.getConfiguration().getDialect().getPartTypeExpression(getType());

		Object[] keys = new Object[getParams().length];
		if(getType().getNumberOfArguments() > 0) {
			for(int i = 0; i < getParams().length; i++) {
				String key = attributeName + "_" + paramIndex;
				if(getType() == Type.IN || getType() == Type.NOT_IN) { //collection
					keys[i] = key;
					expression = attribute.formatParameterExpression(expression);
				} else {
					keys[i] = Segment.HASH_LEFT_BRACE +  attribute.toParameter(key) + Segment.RIGHT_BRACE;
				}
				
				paramIndex ++;
			}
		}
		
		String partTypeExpression = String.format(expression, keys);
		if(partTypeExpression.contains(Constants.COLUMN_HOLDER)) {
			expressionBuilder.append(partTypeExpression.replace(Constants.COLUMN_HOLDER, Segment.DOLLAR + attributeName + Segment.DOLLAR));
		} else {
			expressionBuilder.append(Segment.DOLLAR).append(attributeName).append(Segment.DOLLAR).append(partTypeExpression);
		}
		
		columns.add(attributeName);
		return paramIndex;
	}

	@Override
	public int renderParams(GenericProviderContext context, Map<String, Object> params, int paramIndex) {
		if(getType().getNumberOfArguments() > 0) {
			for(int i = 0; i < getParams().length; i++) {
				String key = attributeName + "_" + paramIndex;
				params.put(key, getParams()[i]);
				paramIndex ++;
			}
		}
		
		return paramIndex;
	}
	

	@Override
	public boolean equals(Object that) {
		if(!this.getClass().isInstance(that)) {
			return false;
		}
		return Objects.equals(this.getType(), ((ConditionImpl)that).getType()) && Objects.equals(this.getAttributeName(), ((ConditionImpl)that).getAttributeName());
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 31 * result + type.hashCode();
		result = 31 * result + attributeName.hashCode();
		return result;
	}
}
