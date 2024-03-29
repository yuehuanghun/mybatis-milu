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
import java.util.Set;

import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;

public interface Expression {
	
	default int renderSqlTemplate(GenericProviderContext context, StringBuilder expressionBuilder, Set<String> columns, int paramIndex) {
		return paramIndex;
	}

	default int renderParams(GenericProviderContext context, Map<String, Object> params, int paramIndex) {
		return paramIndex;
	}
	
	default String columnHolder(String attrName) {
		return SqlBuildingHelper.columnHolder(attrName);
	}
	
	default void end() {
		
	}
}
