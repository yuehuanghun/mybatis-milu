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
	
	public static StatisticPredicate statisticPredicate() {
		return new StatisticPredicateImpl();
	}
	
	public static UpdatePredicate updatePredicate() {
		return new UpdatePredicateImpl();
	}
}
