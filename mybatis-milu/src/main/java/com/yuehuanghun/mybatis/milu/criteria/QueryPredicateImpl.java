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
package com.yuehuanghun.mybatis.milu.criteria;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.data.Sort.Direction;
import com.yuehuanghun.mybatis.milu.tool.Segment;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

import lombok.Getter;

public class QueryPredicateImpl extends PredicateImpl implements QueryPredicate {
	
	private final List<Order> orderList = new ArrayList<>();
	private Limit limit;
	@Getter
	private final Set<String> selectAttrs = new HashSet<>();
	@Getter
	private final Set<String> exselectAttrs = new HashSet<>();

	@Override
	public QueryPredicate select(String... attrNames) {
		for(String attrName : attrNames) {
			this.selectAttrs.add(attrName);
		}
		return this;
	}

	@Override
	public QueryPredicate selects(String attrNameChain) {
		String[] arry = attrNameChain.split(Segment.COMMA);
		for(String attrName : arry) {
			if(StringUtils.isNotBlank(attrName)) {
				selectAttrs.add(attrName.trim());
			}
		}
		return this;
	}

	@Override
	public QueryPredicate exselect(String... attrNames) {
		for(String attrName : attrNames) {
			this.exselectAttrs.add(attrName);
		}
		return this;
	}

	@Override
	public QueryPredicate exselects(String attrNameChain) {
		String[] arry = attrNameChain.split(Segment.COMMA);
		for(String attrName : arry) {
			if(StringUtils.isNotBlank(attrName)) {
				exselectAttrs.add(attrName.trim());
			}
		}
		return this;
	}
	
	@Override
	public QueryPredicate order(String... attrNames) {
		for(String attrName : attrNames) {
			orderList.add(OrderImpl.order(attrName));
		}
		return this;
	}

	@Override
	public QueryPredicate order(Direction direction, String... attrNames) {
		for(String attrName : attrNames) {
			orderList.add(OrderImpl.order(attrName, direction));
		}
		return this;
	}

	@Override
	public QueryPredicate limit(int pageSize) {
		this.limit = new LimitImpl(pageSize);
		return this;
	}
	
	@Override
	public QueryPredicate limit(int pageSize, boolean count) {
		this.limit = new LimitImpl(pageSize, count);
		return this;
	}

	@Override
	public QueryPredicate limit(int pageNum, int pageSize) {
		this.limit = new LimitImpl(pageNum, pageSize, true);
		return this;
	}
	
	@Override
	public QueryPredicate limit(int pageNum, int pageSize, boolean count) {
		this.limit = new LimitImpl(pageNum, pageSize, count);
		return this;
	}
	
	@Override
	public QueryPredicate conditionMode(Mode conditionMode) {
		super.conditionMode(conditionMode);
		return this;
	}

	@Override
	public QueryPredicate and(Condition... conditions) {
		super.and(conditions);
		return this;
	}
	
	@Override
	public QueryPredicate and(Consumer<Predicate> predicate) {
		super.and(predicate);
		return this;
	}

	@Override
	public QueryPredicate or(Condition... conditions) {
		super.or(conditions);
		return this;
	}
	
	@Override
	public QueryPredicate or(Consumer<Predicate> predicate) {
		super.or(predicate);
		return this;
	}
	
	@Override
	public QueryPredicate not(Condition... conditions) {
		super.or(conditions);
		return this;
	}
	
	@Override
	public QueryPredicate not(Consumer<Predicate> predicate) {
		super.not(predicate);
		return this;
	}

	@Override
	public QueryPredicate eq(String attrName, Object value) {
		super.eq(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate neq(String attrName, Object value) {
		super.neq(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate lt(String attrName, Object value) {
		super.lt(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate lte(String attrName, Object value) {
		super.lte(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate gt(String attrName, Object value) {
		super.gt(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate gte(String attrName, Object value) {
		super.gte(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate like(String attrName, Object value) {
		super.like(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate notLike(String attrName, Object value) {
		super.notLike(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate contain(String attrName, Object value) {
		super.contain(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate notContain(String attrName, Object value) {
		super.notContain(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate startWith(String attrName, Object value) {
		super.startWith(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate endWith(String attrName, Object value) {
		super.endWith(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate between(String attrName, Object startValue, Object endValue) {
		super.between(attrName, startValue, endValue);
		return this;
	}
	
	@Override
	public QueryPredicate between(boolean accept, String attrName, Object startValue, Object endValue) {
		super.between(accept, attrName, startValue, endValue);
		return this;
	}

	@Override
	public QueryPredicate isNull(String attrName) {
		super.isNull(attrName);
		return this;
	}

	@Override
	public QueryPredicate notNull(String attrName) {
		super.notNull(attrName);
		return this;
	}
	
	@Override
	public QueryPredicate in(String attrName, Object value) {
		super.in(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate notIn(String attrName, Object value) {
		super.notIn(attrName, value);
		return this;
	}
	
	@Override
	public QueryPredicate eq(boolean accept, String attrName, Object value) {
		super.eq(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate neq(boolean accept, String attrName, Object value) {
		super.neq(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate lt(boolean accept, String attrName, Object value) {
		super.lt(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate lte(boolean accept, String attrName, Object value) {
		super.lte(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate gt(boolean accept, String attrName, Object value) {
		super.gt(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate gte(boolean accept, String attrName, Object value) {
		super.gte(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate like(boolean accept, String attrName, Object value) {
		super.like(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate notLike(boolean accept, String attrName, Object value) {
		super.notLike(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate contain(boolean accept, String attrName, Object value) {
		super.contain(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate notContain(boolean accept, String attrName, Object value) {
		super.notContain(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate startWith(boolean accept, String attrName, Object value) {
		super.startWith(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate endWith(boolean accept, String attrName, Object value) {
		super.endWith(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate in(boolean accept, String attrName, Object value) {
		super.in(accept, attrName, value);
		return this;
	}

	@Override
	public QueryPredicate notIn(boolean accept, String attrName, Object value) {
		super.notIn(accept, attrName, value);
		return this;
	}
	
	@Override
	public QueryPredicate regex(String attrName, Object value) {
		super.regex(attrName, value);
		return this;
	}

	@Override
	public QueryPredicate regex(boolean accept, String attrName, Object value) {
		super.regex(accept, attrName, value);
		return this;
	}

	@Override
	public int render(MiluConfiguration configuration, StringBuilder expressionBuilder, Map<String, Object> params, Set<String> columns, int paramIndex) {
		paramIndex = super.render(configuration, expressionBuilder, params, columns, paramIndex);
		
		if(!orderList.isEmpty()) {
			expressionBuilder.append(Segment.ORDER_BY);
			for(int i = 0; i < orderList.size(); i++) {
				if(i > 0) {
					expressionBuilder.append(Segment.COMMA_B);
				}
				
				paramIndex = orderList.get(i).render(configuration, expressionBuilder, params, columns, paramIndex);
			}
		}
		
		if(limit != null) {
			limit.render(configuration, expressionBuilder, params, columns, paramIndex);
		}
		
		return paramIndex;
	}
}
