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

import java.util.Map;

import com.yuehuanghun.mybatis.milu.criteria.Predicate;
import com.yuehuanghun.mybatis.milu.criteria.Predicates;
import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.tool.Assert;
import com.yuehuanghun.mybatis.milu.tool.Constants;

public class GenericUpdatePatchByIdsProviderSql extends GenericUpdatePatchByCriteriaProviderSql {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		Attribute idAttr = context.getEntity().getId();
		if(idAttr == null) {
			throw new SqlExpressionBuildingException("id属性不存在");
		}
		
		Map paramMap = (Map)params;
		Object ids = paramMap.remove(Constants.IDS);
		Assert.notEmpty(ids, "主键集不能为空");
		Predicate predicate = Predicates.predicate();
		predicate.in(idAttr.getName(), ids);
		paramMap.put(Constants.CRITERIA, predicate);
		
		return super.provideSql(context, params);
	}

	@Override
	public String getMethodName() {
		return "updatePatchByIds";
	}

}
