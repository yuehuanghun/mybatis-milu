package com.yuehuanghun.mybatis.milu.criteria;

import java.util.function.Consumer;

import com.yuehuanghun.mybatis.milu.annotation.Mode;

/**
 * 
 * @see Predicates
 *
 */
public interface UpdatePredicate extends Predicate {
	/**
	 * 设置默认的条件生效模式，默认为Mode.NOT_NULL
	 * @param updateMode 更新字段生效模式
	 * @return 当前对象
	 */
	UpdatePredicate updateMode(Mode updateMode);
	
	/**
	 * 返回更新模式
	 * @return 更新模式
	 */
	Mode getUpdateMode();
	
	@Override
	UpdatePredicate conditionMode(Mode conditionMode);

	@Override
	UpdatePredicate and(Condition... conditions);
	
	@Override
	UpdatePredicate and(Consumer<Predicate> predicate);

	@Override
	UpdatePredicate or(Condition... conditions);
	
	@Override
	UpdatePredicate or(Consumer<Predicate> predicate);

	@Override
	UpdatePredicate not(Condition... conditions);
	
	@Override
	UpdatePredicate not(Consumer<Predicate> predicate);

	@Override
	UpdatePredicate eq(String attrName, Object value);

	@Override
	UpdatePredicate neq(String attrName, Object value);

	@Override
	UpdatePredicate lt(String attrName, Object value);

	@Override
	UpdatePredicate lte(String attrName, Object value);

	@Override
	UpdatePredicate gt(String attrName, Object value);

	@Override
	UpdatePredicate gte(String attrName, Object value);

	@Override
	UpdatePredicate like(String attrName, Object value);

	@Override
	UpdatePredicate notLike(String attrName, Object value);

	@Override
	UpdatePredicate contain(String attrName, Object value);

	@Override
	UpdatePredicate notContain(String attrName, Object value);

	@Override
	UpdatePredicate startWith(String attrName, Object value);

	@Override
	UpdatePredicate endWith(String attrName, Object value);

	@Override
	UpdatePredicate between(String attrName, Object startValue, Object endValue);

	@Override
	UpdatePredicate between(boolean accept, String attrName, Object startValue, Object endValue);

	@Override
	UpdatePredicate isNull(String attrName);

	@Override
	UpdatePredicate isNull(boolean accept, String attrName);

	@Override
	UpdatePredicate notNull(String attrName);

	@Override
	UpdatePredicate notNull(boolean accept, String attrName);

	@Override
	UpdatePredicate in(String attrName, Object value);

	@Override
	UpdatePredicate notIn(String attrName, Object value);

	@Override
	UpdatePredicate eq(boolean accept, String attrName, Object value);

	@Override
	UpdatePredicate neq(boolean accept, String attrName, Object value);

	@Override
	UpdatePredicate lt(boolean accept, String attrName, Object value);

	@Override
	UpdatePredicate lte(boolean accept, String attrName, Object value);

	@Override
	UpdatePredicate gt(boolean accept, String attrName, Object value);

	@Override
	UpdatePredicate gte(boolean accept, String attrName, Object value);

	@Override
	UpdatePredicate like(boolean accept, String attrName, Object value);

	@Override
	UpdatePredicate notLike(boolean accept, String attrName, Object value);

	@Override
	UpdatePredicate contain(boolean accept, String attrName, Object value);

	@Override
	UpdatePredicate notContain(boolean accept, String attrName, Object value);

	@Override
	UpdatePredicate startWith(boolean accept, String attrName, Object value);

	@Override
	UpdatePredicate endWith(boolean accept, String attrName, Object value);

	@Override
	UpdatePredicate in(boolean accept, String attrName, Object value);

	@Override
	UpdatePredicate notIn(boolean accept, String attrName, Object value);

	@Override
	UpdatePredicate regex(String attrName, Object value);

	@Override
	UpdatePredicate regex(boolean accept, String attrName, Object value);
	
	@Override
	UpdatePredicate undeleted();
	
	@Override
	UpdatePredicate deleted();
}
