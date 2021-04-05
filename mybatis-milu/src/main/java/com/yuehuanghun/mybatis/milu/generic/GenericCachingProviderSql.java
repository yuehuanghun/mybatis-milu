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
package com.yuehuanghun.mybatis.milu.generic;

import java.util.Map;

import org.apache.ibatis.javassist.scopedpool.SoftValueHashMap;

import com.yuehuanghun.mybatis.milu.tool.StringUtils;

public abstract class GenericCachingProviderSql implements GenericProviderSql {
	protected final Map<String, String> cache = new SoftValueHashMap<>();

	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		return cache.computeIfAbsent(context.getMapperType().getName(), (key) -> {return provideCachingSql(context, params);});
	}

	public abstract String provideCachingSql(GenericProviderContext context, Object params);
	
	protected String wrapIdentifier(String identifier, GenericProviderContext context) {
		String identifierQuoteString = context.getConfiguration().getDbMeta().getIdentifierQuoteString();
		if(StringUtils.isBlank(identifierQuoteString)) {
			return identifier;
		} else {
			return identifierQuoteString + identifier + identifierQuoteString;
		}
	}
}
