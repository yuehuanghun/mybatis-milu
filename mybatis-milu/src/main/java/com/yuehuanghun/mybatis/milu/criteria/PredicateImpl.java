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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.tool.Assert;
import com.yuehuanghun.mybatis.milu.tool.Segment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public class PredicateImpl implements Predicate {

	protected final Logic logic;

	private Mode conditionMode = Mode.NOT_EMPTY;

	private List<Condition> conditionList = new ArrayList<>();
	
	@Setter(value = AccessLevel.PROTECTED)
	@Getter(value = AccessLevel.PROTECTED)
	@Accessors(chain = true)
	private int depth;
	
	public PredicateImpl() {
		logic = Logic.AND;
	}

	private PredicateImpl(Logic logic, Condition... conditions) {
		this.logic = logic;
		for (Condition condition : conditions) {
			this.conditionList.add(condition);
		}
	}

	@Override
	public Predicate conditionMode(Mode conditionMode) {
		Assert.notNull(conditionMode, "conditionMode不能为Null");
		if(conditionMode != Mode.AUTO) {
			this.conditionMode = conditionMode;
		}
		return this;
	}

	@Override
	public Predicate and(Condition... conditions) {
		if(conditions.length == 1 && PredicateImpl.class.isInstance(conditions[0])) {
			if(((PredicateImpl)conditions[0]).isEmpty()) {
				return this;
			}
			conditionList.add(((PredicateImpl)conditions[0]).setDepth(getDepth() + 1));
		} else if(conditions.length > 0) {
			conditionList.add(new PredicateImpl(Logic.AND, conditions).setDepth(getDepth() + 1));	
		}
		return this;
	}
	
	@Override
	public Predicate and(Consumer<Predicate> predicate) {
		Predicate p = new PredicateImpl(Logic.AND).setDepth(getDepth() + 1);
		predicate.accept(p);
		if(!p.isEmpty()) {
			conditionList.add(p);
		}
		return this;
	}

	@Override
	public Predicate or(Condition... conditions) {
		if(conditions.length == 1 && PredicateImpl.class.isInstance(conditions[0])) {
			if(((PredicateImpl)conditions[0]).isEmpty()) {
				return this;
			}
			conditionList.add(((PredicateImpl)conditions[0]).setDepth(getDepth() + 1));
		} else if(conditions.length > 0) {
			conditionList.add(new PredicateImpl(Logic.OR, conditions).setDepth(getDepth() + 1));	
		}
		return this;
	}
	
	@Override
	public Predicate or(Consumer<Predicate> predicate) {
		Predicate p = new PredicateImpl(Logic.OR).setDepth(getDepth() + 1);
		predicate.accept(p);
		if(!p.isEmpty()) {
			conditionList.add(p);
		}
		return this;
	}
	
	@Override
	public Predicate not(Condition... conditions) {
		if(conditions.length == 1 && PredicateImpl.class.isInstance(conditions[0])) {
			if(((PredicateImpl)conditions[0]).isEmpty()) {
				return this;
			}
			conditionList.add(((PredicateImpl)conditions[0]).setDepth(getDepth() + 1));
		} else if(conditions.length > 0) {
			conditionList.add(new PredicateImpl(Logic.NOT, conditions).setDepth(getDepth() + 1));	
		}
		return this;
	}
	
	@Override
	public Predicate not(Consumer<Predicate> predicate) {
		Predicate p = new PredicateImpl(Logic.NOT).setDepth(getDepth() + 1);
		predicate.accept(p);
		if(!p.isEmpty()) {
			conditionList.add(p);
		}
		return this;
	}

	@Override
	public Predicate eq(String attrName, Object value) {
		this.eq(acceptCondition(value), attrName, value);
		return this;
	}

	@Override
	public Predicate neq(String attrName, Object value) {
		this.neq(acceptCondition(value), attrName, value);
		return this;
	}

	@Override
	public Predicate lt(String attrName, Object value) {
		this.lt(acceptCondition(value), attrName, value);
		return this;
	}

	@Override
	public Predicate lte(String attrName, Object value) {
		this.lte(acceptCondition(value), attrName, value);
		return this;
	}

	@Override
	public Predicate gt(String attrName, Object value) {
		this.gt(acceptCondition(value), attrName, value);
		return this;
	}

	@Override
	public Predicate gte(String attrName, Object value) {
		this.gte(acceptCondition(value), attrName, value);
		return this;
	}

	@Override
	public Predicate like(String attrName, Object value) {
		this.like(acceptCondition(value), attrName, value);
		return this;
	}

	@Override
	public Predicate notLike(String attrName, Object value) {
		this.notLike(acceptCondition(value), attrName, value);
		return this;
	}

	@Override
	public Predicate contain(String attrName, Object value) {
		this.contain(acceptCondition(value), attrName, value);
		return this;
	}

	@Override
	public Predicate notContain(String attrName, Object value) {
		this.notContain(acceptCondition(value), attrName, value);
		return this;
	}

	@Override
	public Predicate startWith(String attrName, Object value) {
		this.startWith(acceptCondition(value), attrName, value);
		return this;
	}

	@Override
	public Predicate endWith(String attrName, Object value) {
		this.endWith(acceptCondition(value), attrName, value);
		return this;
	}

	@Override
	public Predicate between(String attrName, Object startValue, Object endValue) {
		and(Conditions.between(attrName, startValue, endValue));
		return this;
	}
	
	@Override
	public Predicate between(boolean accept, String attrName, Object startValue, Object endValue) {
		if(accept) {
			and(Conditions.between(attrName, startValue, endValue));
		}
		return this;
	}

	@Override
	public Predicate isNull(String attrName) {
		and(Conditions.isNull(attrName));
		return this;
	}

	@Override
	public Predicate isNull(boolean accept, String attrName) {
		if(accept) {
			and(Conditions.isNull(attrName));
		}
		return this;
	}

	@Override
	public Predicate notNull(String attrName) {
		and(Conditions.isNotNull(attrName));
		return this;
	}

	@Override
	public Predicate notNull(boolean accept, String attrName) {
		if(accept) {
			and(Conditions.isNotNull(attrName));
		}
		return this;
	}

	@Override
	public Predicate in(String attrName, Object value) {
		this.in(acceptCondition(value), attrName, value);
		return this;
	}

	@Override
	public Predicate notIn(String attrName, Object value) {
		this.notIn(acceptCondition(value), attrName, value);
		return this;
	}
	
	@Override
	public Predicate eq(boolean accept, String attrName, Object value) {
		if(accept) {
			and(Conditions.equal(attrName, value));
		}
		return this;
	}

	@Override
	public Predicate neq(boolean accept, String attrName, Object value) {
		if(accept) {
			and(Conditions.notEqual(attrName, value));
		}
		return this;
	}

	@Override
	public Predicate lt(boolean accept, String attrName, Object value) {
		if(accept) {
			and(Conditions.lessThan(attrName, value));
		}
		return this;
	}

	@Override
	public Predicate lte(boolean accept, String attrName, Object value) {
		if(accept) {
			and(Conditions.lessThanEqual(attrName, value));
		}
		return this;
	}

	@Override
	public Predicate gt(boolean accept, String attrName, Object value) {
		if(accept) {
			and(Conditions.greaterThan(attrName, value));
		}
		return this;
	}

	@Override
	public Predicate gte(boolean accept, String attrName, Object value) {
		if(accept) {
			and(Conditions.greaterThanEqual(attrName, value));
		}
		return this;
	}

	@Override
	public Predicate like(boolean accept, String attrName, Object value) {
		if(accept) {
			and(Conditions.like(attrName, value));
		}
		return this;
	}

	@Override
	public Predicate notLike(boolean accept, String attrName, Object value) {
		if(accept) {
			and(Conditions.notLike(attrName, value));
		}
		return this;
	}

	@Override
	public Predicate contain(boolean accept, String attrName, Object value) {
		if(accept) {
			and(Conditions.containing(attrName, value));
		}
		return this;
	}

	@Override
	public Predicate notContain(boolean accept, String attrName, Object value) {
		if(accept) {
			and(Conditions.notContaining(attrName, value));
		}
		return this;
	}

	@Override
	public Predicate startWith(boolean accept, String attrName, Object value) {
		if(accept) {
			and(Conditions.startWith(attrName, value));
		}
		return this;
	}

	@Override
	public Predicate endWith(boolean accept, String attrName, Object value) {
		if(accept) {
			and(Conditions.endWith(attrName, value));
		}
		return this;
	}

	@Override
	public Predicate in(boolean accept, String attrName, Object value) {
		if(accept) {
			assertCollection(value);
			and(Conditions.in(attrName, value));
		}
		return this;
	}

	@Override
	public Predicate notIn(boolean accept, String attrName, Object value) {
		if(accept) {
			assertCollection(value);
			and(Conditions.notIn(attrName, value));
		}
		return this;
	}

	@Override
	public Predicate regex(String attrName, Object value) {
		this.regex(acceptCondition(value), attrName, value);
		return this;
	}

	@Override
	public Predicate regex(boolean accept, String attrName, Object value) {
		if(accept) {
			and(Conditions.regex(attrName, value));
		}
		return this;
	}

	@Override
	public Predicate undeleted() {
		and(new Deleted(false));
		return this;
	}

	@Override
	public Predicate deleted() {
		and(new Deleted(true));
		return this;
	}
	
	protected boolean acceptCondition(Object value) {
		if(conditionMode == Mode.NOT_NULL) {
			return value != null;
		}
		if(conditionMode == Mode.NOT_EMPTY) {
			if(value == null) {
				return false;
			}
			if(CharSequence.class.isInstance(value)) {
				return ((CharSequence)value).length() > 0;
			}
			if(Collection.class.isInstance(value)) {
				return ((Collection<?>)value).size() > 0;
			}
			if(value.getClass().isArray()) {
				return Array.getLength(value) > 0;
			}
			return true;
		}
		return true;
	}
	
	private void assertCollection(Object value) {
		if(value == null) {
			throw new SqlExpressionBuildingException("IN/NOT_IN查询值不能为空");
		}
		
		if(!Collection.class.isInstance(value) && !value.getClass().isArray()) { //数组或集合
			throw new SqlExpressionBuildingException("IN/NOT_IN查询值应该数组或集合");
		}
	}

	@Override
	public int renderSqlTemplate(GenericProviderContext context, StringBuilder expressionBuilder, Set<String> columns,
			int paramIndex) {
		if(conditionList.isEmpty()) {
			return paramIndex;
		}
		
		boolean group = conditionList.size() > 1 && getDepth() > 0;
		if(group) {
			expressionBuilder.append(Segment.LEFT_BRACKET);
		}

		StringBuilder subBuilder = new StringBuilder();
		for(int i = 0; i < conditionList.size(); i++) {
			Condition condition = conditionList.get(i);
			if(i > 0 && condition instanceof PredicateImpl) {
				Logic childLogic = ((PredicateImpl) condition).logic;
				expressionBuilder.append(Segment.SPACE).append(childLogic.name()).append(Segment.SPACE);
			}
			paramIndex = condition.renderSqlTemplate(context, expressionBuilder, columns, paramIndex);
		}
		
		expressionBuilder.append(subBuilder);
		
		if(group) {
			expressionBuilder.append(Segment.RIGHT_BRACKET);
		}
		return paramIndex;
	}

	@Override
	public int renderParams(GenericProviderContext context, Map<String, Object> params, int paramIndex) {
		for(Condition condition : conditionList) {
			paramIndex = condition.renderParams(context, params, paramIndex);
		}
		return paramIndex;
	}

	@Override
	public int hashCode() {
		return logic.hashCode() + conditionMode.hashCode() + conditionList.hashCode() + depth;
	}

	@Override
	public boolean equals(Object obj) {
		if(!this.getClass().isInstance(obj)) {
			return false;
		}
		
		PredicateImpl that = (PredicateImpl) obj;
		
		return Objects.equals(this.logic, that.logic) && Objects.equals(this.conditionMode, that.conditionMode) && 
				Objects.equals(this.conditionList, that.conditionList) && this.depth == that.depth;
	}

	@Override
	public boolean isEmpty() {
		return conditionList.isEmpty();
	}
	
}
