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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.tool.Segment;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

public class SelectImpl implements Select {
	private final Set<Function> functions = new HashSet<>();
	private final Set<String> properties = new HashSet<>();

	@Override
	public void add(String functionName, String property, String alias) {
		functions.add(new Function(functionName, property, alias));
	}

	@Override
	public void add(String property) {
		properties.add(property);
	}

	@Override
	public int renderSqlTemplate(GenericProviderContext context,  StringBuilder expressionBuilder, Set<String> columns,
			int paramIndex) {
		if(functions.isEmpty()) {
			throw new SqlExpressionBuildingException("未设置统计字段属性");
		}
		
		expressionBuilder.append(Segment.SELECT);
		functions.forEach(function -> {
			if(StringUtils.isBlank(function.getFunctionName())) {
				expressionBuilder.append(columnHolder(function.getPropertyName()));
			} else {
				expressionBuilder.append(String.format(context.getConfiguration().getDialect().getFunctionExpression(function.getFunctionName()), columnHolder(function.getPropertyName())));
			}
			if(StringUtils.isNotBlank(function.getAlias())) {
				expressionBuilder.append(Segment.SPACE).append(SqlBuildingHelper.wrapIdentifier(function.getAlias(), context.getConfiguration()));
			} else {
				expressionBuilder.append(Segment.SPACE).append(SqlBuildingHelper.wrapIdentifier(getAlias(function.getFunctionName(), function.getPropertyName()), context.getConfiguration()));
			}
			expressionBuilder.append(Segment.COMMA_B);
			columns.add(function.getPropertyName());
		});
		
		properties.forEach(property -> {
			expressionBuilder.append(columnHolder(property)).append(Segment.SPACE).append(property).append(Segment.COMMA_B);
			columns.add(property);
		});
		
		expressionBuilder.setLength(expressionBuilder.length() - Segment.COMMA_B.length()); //去掉最后逗号
		
		return paramIndex;
	}
	
	private String getAlias(String function, String property) {
		return property + StringUtils.capitalize(function);
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 31 * result + functions.hashCode();
		result = 31 * result + properties.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!this.getClass().isInstance(obj)) {
			return false;
		}
		
		SelectImpl that = (SelectImpl) obj;
		return Objects.equals(this.functions, that.functions) && Objects.equals(this.properties, that.properties);
	}
}
