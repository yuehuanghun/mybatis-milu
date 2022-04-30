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
package com.yuehuanghun.mybatis.milu.dialect;

import java.util.HashMap;
import java.util.Map;

import com.yuehuanghun.mybatis.milu.data.Part.Type;
import com.yuehuanghun.mybatis.milu.tool.Assert;

public abstract class AbstractDialect implements Dialect {
	protected final Map<Type, String> partTypeExpressionMap = new HashMap<>();
	protected final Map<String, String> functionMap = new HashMap<>();
	
	protected AbstractDialect() {
		initPartTypeExpression();
		initFunctionMap();
	}
	
	protected void initPartTypeExpression() {
		partTypeExpressionMap.put(Type.BETWEEN, " BETWEEN %s AND %s");
		partTypeExpressionMap.put(Type.IS_NOT_NULL, " IS NOT NULL");
		partTypeExpressionMap.put(Type.IS_NULL, " IS NULL");
		partTypeExpressionMap.put(Type.LESS_THAN, " &lt; %s");
		partTypeExpressionMap.put(Type.LESS_THAN_EQUAL, " &lt;= %s");
		partTypeExpressionMap.put(Type.GREATER_THAN, " &gt; %s");
		partTypeExpressionMap.put(Type.GREATER_THAN_EQUAL, " &gt;= %s");
		partTypeExpressionMap.put(Type.BEFORE, " &lt; %s");
		partTypeExpressionMap.put(Type.AFTER, " &gt; %s");
		partTypeExpressionMap.put(Type.NOT_LIKE, " NOT LIKE %s");
		partTypeExpressionMap.put(Type.LIKE, " LIKE %s ");
		partTypeExpressionMap.put(Type.STARTING_WITH, " LIKE CONCAT(%s,'%%')");
		partTypeExpressionMap.put(Type.ENDING_WITH, " LIKE CONCAT('%%', %s)");
		partTypeExpressionMap.put(Type.NOT_CONTAINING, " NOT LIKE CONCAT('%%', %s, '%%')");
		partTypeExpressionMap.put(Type.CONTAINING, " LIKE CONCAT('%%', %s, '%%')");
		partTypeExpressionMap.put(Type.NOT_IN, " NOT IN (<foreach collection=\"%s\" item=\"item\" separator=\",\">#{item}</foreach>)");
		partTypeExpressionMap.put(Type.IN, " IN (<foreach collection=\"%s\" item=\"item\" separator=\",\">#{item}</foreach>)");
		partTypeExpressionMap.put(Type.REGEX, " REGEXP %s");
		partTypeExpressionMap.put(Type.TRUE, " = TRUE");
		partTypeExpressionMap.put(Type.FALSE, " = FALSE");
		partTypeExpressionMap.put(Type.SIMPLE_PROPERTY, " = %s");
		partTypeExpressionMap.put(Type.NEGATING_SIMPLE_PROPERTY, " &lt;&gt; %s");
	}
	
	protected void initFunctionMap() {
		functionMap.put(SUM, "SUM(%s)");
		functionMap.put(COUNT, "COUNT(%s)");
		functionMap.put(MIN, "MIN(%s)");
		functionMap.put(MAX, "MAX(%s)");
		functionMap.put(AVG, "AVG(%s)");
	}

	public String getPartTypeExpression(Type partType) {
		String expression = partTypeExpressionMap.get(partType);
		Assert.notBlank(expression, "未支持的表达式类型");
		return expression;
	}

	@Override
	public String getFunctionExpression(String function) {
		return functionMap.get(function);
	}
}
