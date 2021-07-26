package com.yuehuanghun.mybatis.milu.criteria;

import java.util.Objects;
import java.util.function.Consumer;

import com.yuehuanghun.mybatis.milu.annotation.Mode;

public class UpdatePredicateImpl extends PredicateImpl implements UpdatePredicate {
	private Mode updateMode = Mode.NOT_NULL;
	
	@Override
	public UpdatePredicate updateMode(Mode updateMode) {
		if(updateMode != null) {
			this.updateMode = updateMode;
		}
		return this;
	}

	@Override
	public Mode getUpdateMode() {
		return updateMode;
	}

	@Override
	public UpdatePredicate conditionMode(Mode conditionMode) {
		super.conditionMode(conditionMode);
		return this;
	}

	@Override
	public UpdatePredicate and(Condition... conditions) {
		super.and(conditions);
		return this;
	}

	@Override
	public UpdatePredicate and(Consumer<Predicate> predicate) {
		super.and(predicate);
		return this;
	}

	@Override
	public UpdatePredicate or(Condition... conditions) {
		super.or(conditions);
		return this;
	}

	@Override
	public UpdatePredicate or(Consumer<Predicate> predicate) {
		super.or(predicate);
		return this;
	}

	@Override
	public UpdatePredicate not(Condition... conditions) {
		super.or(conditions);
		return this;
	}

	@Override
	public UpdatePredicate not(Consumer<Predicate> predicate) {
		super.not(predicate);
		return this;
	}

	@Override
	public UpdatePredicate eq(String attrName, Object value) {
		super.eq(attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate neq(String attrName, Object value) {
		super.neq(attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate lt(String attrName, Object value) {
		super.lt(attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate lte(String attrName, Object value) {
		super.lte(attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate gt(String attrName, Object value) {
		super.gt(attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate gte(String attrName, Object value) {
		super.gte(attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate like(String attrName, Object value) {
		super.like(attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate notLike(String attrName, Object value) {
		super.notLike(attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate contain(String attrName, Object value) {
		super.contain(attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate notContain(String attrName, Object value) {
		super.notContain(attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate startWith(String attrName, Object value) {
		super.startWith(attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate endWith(String attrName, Object value) {
		super.endWith(attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate between(String attrName, Object startValue, Object endValue) {
		super.between(attrName, startValue, endValue);
		return this;
	}

	@Override
	public UpdatePredicate between(boolean accept, String attrName, Object startValue, Object endValue) {
		super.between(accept, attrName, startValue, endValue);
		return this;
	}

	@Override
	public UpdatePredicate isNull(String attrName) {
		super.isNull(attrName);
		return this;
	}

	@Override
	public UpdatePredicate notNull(String attrName) {
		super.notNull(attrName);
		return this;
	}

	@Override
	public UpdatePredicate in(String attrName, Object value) {
		super.in(attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate notIn(String attrName, Object value) {
		super.notIn(attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate eq(boolean accept, String attrName, Object value) {
		super.eq(accept, attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate neq(boolean accept, String attrName, Object value) {
		super.neq(accept, attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate lt(boolean accept, String attrName, Object value) {
		super.lt(accept, attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate lte(boolean accept, String attrName, Object value) {
		super.lte(accept, attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate gt(boolean accept, String attrName, Object value) {
		super.gt(accept, attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate gte(boolean accept, String attrName, Object value) {
		super.gte(accept, attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate like(boolean accept, String attrName, Object value) {
		super.like(accept, attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate notLike(boolean accept, String attrName, Object value) {
		super.notLike(accept, attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate contain(boolean accept, String attrName, Object value) {
		super.contain(accept, attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate notContain(boolean accept, String attrName, Object value) {
		super.notContain(accept, attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate startWith(boolean accept, String attrName, Object value) {
		super.startWith(accept, attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate endWith(boolean accept, String attrName, Object value) {
		super.endWith(accept, attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate in(boolean accept, String attrName, Object value) {
		super.in(accept, attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate notIn(boolean accept, String attrName, Object value) {
		super.notIn(accept, attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate regex(String attrName, Object value) {
		super.regex(attrName, value);
		return this;
	}

	@Override
	public UpdatePredicate regex(boolean accept, String attrName, Object value) {
		super.regex(accept, attrName, value);
		return this;
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + updateMode.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!this.getClass().isInstance(obj)) {
			return false;
		}
		if (!super.equals(obj)) {
			return false;
		}
		return Objects.equals(this.updateMode, ((UpdatePredicate)obj).getUpdateMode());
	}
}
