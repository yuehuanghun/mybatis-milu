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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.javassist.scopedpool.SoftValueHashMap;

import com.yuehuanghun.mybatis.milu.annotation.Filler.FillMode;
import com.yuehuanghun.mybatis.milu.criteria.Patch;
import com.yuehuanghun.mybatis.milu.criteria.Predicate;
import com.yuehuanghun.mybatis.milu.criteria.Predicates;
import com.yuehuanghun.mybatis.milu.criteria.builder.UpdatePatchSqlTemplateBuilder;
import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderSql;
import com.yuehuanghun.mybatis.milu.tool.Constants;

public class GenericUpdatePatchByCriteriaProviderSql implements GenericProviderSql {
	private final Map<Class<?>, Map<Object, String>> cache = new ConcurrentHashMap<>();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		Map paramMap = (Map)params;
		
		Patch patch = (Patch)paramMap.remove(Constants.PATCH);
		if(patch.isEmpty()) {
			throw new SqlExpressionBuildingException("需要更新的属性不能为空");
		}
		paramMap.put(Constants.ENTITY, patch.getContainer());
		
		Object predicateObj = paramMap.remove(Constants.CRITERIA);
		Predicate predicate;
		if(predicateObj instanceof Predicate) {
			predicate = (Predicate)predicateObj;
		} else {
			predicate = Predicates.predicate();
			((Consumer<Predicate>)predicateObj).accept(predicate);
		}
		
		Map<String, Object> queryParams = new HashMap<>();
		predicate.renderParams(context, queryParams, 0);
		paramMap.putAll(queryParams);
		
		context.getEntity().getOnUpdateFillers().forEach(filler -> { // 自动填充值处理，填充值不为空时才设置
			Object attrValue = patch.getAttrValue(filler.getAttrName());
			if(attrValue == null) {
				Object updateValue = filler.getUpdateValue();
				if(updateValue != null) {
					patch.add(filler.getAttrName(), updateValue);
				}
			} else {
				if(filler.getFillMode() == FillMode.ANY) {
					Object updateValue = filler.getUpdateValue();
					if(updateValue != null) {
						patch.add(filler.getAttrName(), updateValue);
					}
				}
			}
		});
		
		return cache.computeIfAbsent(context.getMapperType(), mapperType -> {
			return new SoftValueHashMap<>();
		}).computeIfAbsent(new CacheKey(new Object[] { predicate, patch.getAttrNames() }), key -> {			
			return new UpdatePatchSqlTemplateBuilder(context, patch, predicate).build();
		});
	}

	@Override
	public String getMethodName() {
		return "updatePatchByCriteria";
	}

}
