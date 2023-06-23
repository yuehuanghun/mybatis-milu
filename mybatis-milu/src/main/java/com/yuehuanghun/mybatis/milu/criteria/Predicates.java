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

import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;

/**
 * 创建Predicate的工具类
 * @author yuehuanghun
 *
 */
public class Predicates {

	public static Predicate predicate() {
		return new PredicateImpl();
	}
	
	public static QueryPredicate queryPredicate() {
		return new QueryPredicateImpl();
	}
	
	/**
	 * 通过样例创建动态查询条件
	 * @param example 实体类的实例
	 * @return 查询条件
	 */
	public static QueryPredicate queryPredicate(Object example) {
		if(example != null) {
			Entity entity = null;
			
			for(MiluConfiguration configuration : MiluConfiguration.getInstances()) {
				entity = configuration.getMetaModel().getEntity(example.getClass());
				if(entity != null) {
					break;
				}
			}
			
			if(entity == null) {
				throw new SqlExpressionBuildingException(String.format("类%s找不到实体类信息", example.getClass().getName()));
			}
			
			return SqlBuildingHelper.exampleToQueryPredicate(entity, example);
		}
		return new QueryPredicateImpl();
	}
	
	public static StatisticPredicate statisticPredicate() {
		return new StatisticPredicateImpl();
	}
	
	public static UpdatePredicate updatePredicate() {
		return new UpdatePredicateImpl();
	}
}
