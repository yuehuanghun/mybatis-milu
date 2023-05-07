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

import com.yuehuanghun.mybatis.milu.data.Part.Type;

/**
 * Condition构建工具类
 * @author yuehuanghun
 *
 */
public class Conditions {

	public static Condition between(String attrName, Object startValue, Object endValue) {
		return new ConditionImpl(Type.BETWEEN, attrName, new Object[] { startValue, endValue });
	}

	public static Condition isNotNull(String attrName) {
		return new ConditionImpl(Type.IS_NOT_NULL, attrName);
	}

	public static Condition isNull(String attrName) {
		return new ConditionImpl(Type.IS_NULL, attrName);
	}

	public static Condition lessThan(String attrName, Object value) {
		return new ConditionImpl(Type.LESS_THAN, attrName, new Object[] { value });
	}

	public static Condition lessThanEqual(String attrName, Object value) {
		return new ConditionImpl(Type.LESS_THAN_EQUAL, attrName, new Object[] { value });
	}

	public static Condition greaterThan(String attrName, Object value) {
		return new ConditionImpl(Type.GREATER_THAN, attrName, new Object[] { value });
	}

	public static Condition greaterThanEqual(String attrName, Object value) {
		return new ConditionImpl(Type.GREATER_THAN_EQUAL, attrName, new Object[] { value });
	}

	public static Condition before(String attrName, Object value) {
		return new ConditionImpl(Type.LESS_THAN, attrName, new Object[] { value });
	}

	public static Condition after(String attrName, Object value) {
		return new ConditionImpl(Type.GREATER_THAN, attrName, new Object[] { value });
	}

	public static Condition notLike(String attrName, Object value) {
		return new ConditionImpl(Type.NOT_LIKE, attrName, new Object[] { value });
	}

	public static Condition like(String attrName, Object value) {
		return new ConditionImpl(Type.LIKE, attrName, new Object[] { value });
	}

	public static Condition startWith(String attrName, Object value) {
		return new ConditionImpl(Type.STARTING_WITH, attrName, new Object[] { value });
	}

	public static Condition endWith(String attrName, Object value) {
		return new ConditionImpl(Type.ENDING_WITH, attrName, new Object[] { value });
	}

	public static Condition notContaining(String attrName, Object value) {
		return new ConditionImpl(Type.NOT_CONTAINING, attrName, new Object[] { value });
	}

	public static Condition containing(String attrName, Object value) {
		return new ConditionImpl(Type.CONTAINING, attrName, new Object[] { value });
	}

	public static Condition notIn(String attrName, Object value) {
		return new ConditionImpl(Type.NOT_IN, attrName, new Object[] { value });
	}

	public static Condition in(String attrName, Object value) {
		return new ConditionImpl(Type.IN, attrName, new Object[] { value });
	}

	public static Condition regex(String attrName, Object value) {
		return new ConditionImpl(Type.REGEX, attrName, new Object[] { value });
	}

	public static Condition isTrue(String attrName) {
		return new ConditionImpl(Type.TRUE, attrName);
	}

	public static Condition isFalse(String attrName) {
		return new ConditionImpl(Type.FALSE, attrName);
	}

	public static Condition notEqual(String attrName, Object value) {
		return new ConditionImpl(Type.NEGATING_SIMPLE_PROPERTY, attrName, new Object[] { value });
	}

	public static Condition equal(String attrName, Object value) {
		return new ConditionImpl(Type.SIMPLE_PROPERTY, attrName, new Object[] { value });
	}
}
