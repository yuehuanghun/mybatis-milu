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
package com.yuehuanghun.mybatis.milu.generic;

import java.util.Map;

import org.apache.ibatis.javassist.scopedpool.SoftValueHashMap;

import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;

public abstract class GenericCachingProviderSql implements GenericProviderSql {
	protected final Map<String, String> cache = new SoftValueHashMap<>();

	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		SqlBuildingHelper.convertLocalPageOrder(context.getEntity(), context.getConfiguration()); //转换排序中的属性为列名
		
		return cache.computeIfAbsent(getCacheKey(context, params), (key) -> {return provideCachingSql(context, params);});
	}
	
	protected String getCacheKey(GenericProviderContext context, Object params) {
		return context.getMapperType().getName();
	}

	public abstract String provideCachingSql(GenericProviderContext context, Object params);
	
	protected String wrapIdentifier(String identifier, GenericProviderContext context) {
		return SqlBuildingHelper.wrapIdentifier(identifier, context.getConfiguration());
	}
	
	protected String wrapTableName(Entity entity, GenericProviderContext context) {
		return SqlBuildingHelper.wrapTableName(entity, context.getConfiguration());
	}
}
