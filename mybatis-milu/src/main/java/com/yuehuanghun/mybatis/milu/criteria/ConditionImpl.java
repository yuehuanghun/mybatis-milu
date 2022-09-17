/*
 * Copyright 2020-2022 the original author or authors.
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
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.tool.Constants;
import com.yuehuanghun.mybatis.milu.tool.Segment;

import lombok.Getter;

public class ConditionImpl implements Condition {

	@Getter
	private Type type;

	@Getter
	private Object[] params;

	@Getter
	private String attributeName;

	private ConditionImpl(Type type, String attributeName, Object... params) {
		this.type = type;
		this.attributeName = attributeName;
		this.params = params;
	}

	public static ConditionImpl between(String attrName, Object startValue, Object endValue) {
		return new ConditionImpl(Type.BETWEEN, attrName, new Object[] { startValue, endValue });
	}

	public static ConditionImpl isNotNull(String attrName) {
		return new ConditionImpl(Type.IS_NOT_NULL, attrName);
	}

	public static ConditionImpl isNull(String attrName) {
		return new ConditionImpl(Type.IS_NULL, attrName);
	}

	public static ConditionImpl lessThan(String attrName, Object value) {
		return new ConditionImpl(Type.LESS_THAN, attrName, new Object[] { value });
	}

	public static ConditionImpl lessThanEqual(String attrName, Object value) {
		return new ConditionImpl(Type.LESS_THAN_EQUAL, attrName, new Object[] { value });
	}

	public static ConditionImpl greaterThan(String attrName, Object value) {
		return new ConditionImpl(Type.GREATER_THAN, attrName, new Object[] { value });
	}

	public static ConditionImpl greaterThanEqual(String attrName, Object value) {
		return new ConditionImpl(Type.GREATER_THAN_EQUAL, attrName, new Object[] { value });
	}

	public static ConditionImpl before(String attrName, Object value) {
		return new ConditionImpl(Type.LESS_THAN, attrName, new Object[] { value });
	}

	public static ConditionImpl after(String attrName, Object value) {
		return new ConditionImpl(Type.GREATER_THAN, attrName, new Object[] { value });
	}

	public static ConditionImpl notLike(String attrName, Object value) {
		return new ConditionImpl(Type.NOT_LIKE, attrName, new Object[] { value });
	}

	public static ConditionImpl like(String attrName, Object value) {
		return new ConditionImpl(Type.LIKE, attrName, new Object[] { value });
	}

	public static ConditionImpl startWith(String attrName, Object value) {
		return new ConditionImpl(Type.STARTING_WITH, attrName, new Object[] { value });
	}

	public static ConditionImpl endWith(String attrName, Object value) {
		return new ConditionImpl(Type.ENDING_WITH, attrName, new Object[] { value });
	}

	public static ConditionImpl notContaining(String attrName, Object value) {
		return new ConditionImpl(Type.NOT_CONTAINING, attrName, new Object[] { value });
	}

	public static ConditionImpl containing(String attrName, Object value) {
		return new ConditionImpl(Type.CONTAINING, attrName, new Object[] { value });
	}

	public static ConditionImpl notIn(String attrName, Object value) {
		return new ConditionImpl(Type.NOT_IN, attrName, new Object[] { value });
	}

	public static ConditionImpl in(String attrName, Object value) {
		return new ConditionImpl(Type.IN, attrName, new Object[] { value });
	}

	public static ConditionImpl regex(String attrName, Object value) {
		return new ConditionImpl(Type.REGEX, attrName, new Object[] { value });
	}

	public static ConditionImpl isTrue(String attrName) {
		return new ConditionImpl(Type.TRUE, attrName);
	}

	public static ConditionImpl isFalse(String attrName) {
		return new ConditionImpl(Type.FALSE, attrName);
	}

	public static ConditionImpl notEqual(String attrName, Object value) {
		return new ConditionImpl(Type.NEGATING_SIMPLE_PROPERTY, attrName, new Object[] { value });
	}

	public static ConditionImpl equal(String attrName, Object value) {
		return new ConditionImpl(Type.SIMPLE_PROPERTY, attrName, new Object[] { value });
	}

	@Override
	public int renderSqlTemplate(GenericProviderContext context, StringBuilder expressionBuilder, Set<String> columns, int paramIndex) {
		String express = context.getConfiguration().getDialect().getPartTypeExpression(getType());

		Object[] keys = new Object[getParams().length];
		if(getType().getNumberOfArguments() > 0) {
			for(int i = 0; i < getParams().length; i++) {
				String key = attributeName + "_" + paramIndex;
				if(getType() == Type.IN || getType() == Type.NOT_IN) { //collection
					keys[i] = key;
				} else {
					keys[i] = Segment.HASH_LEFT_BRACE +  key + Segment.RIGHT_BRACE;
				}
				
				paramIndex ++;
			}
		}
		
		String partTypeExpression = String.format(express, keys);
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
		Object[] keys = new Object[getParams().length];
		if(getType().getNumberOfArguments() > 0) {
			for(int i = 0; i < getParams().length; i++) {
				String key = attributeName + "_" + paramIndex;
				if(getType() == Type.IN || getType() == Type.NOT_IN) { //collection
					keys[i] = key;
				} else {
					keys[i] = Segment.HASH_LEFT_BRACE +  key + Segment.RIGHT_BRACE;
				}
				
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
