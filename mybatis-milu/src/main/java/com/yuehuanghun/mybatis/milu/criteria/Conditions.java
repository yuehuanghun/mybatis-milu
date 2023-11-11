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

import com.yuehuanghun.mybatis.milu.criteria.lambda.LambdaReflections;
import com.yuehuanghun.mybatis.milu.criteria.lambda.SerializableFunction;
import com.yuehuanghun.mybatis.milu.data.Part.Type;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

/**
 * Condition构建工具类
 * @author yuehuanghun
 *
 */
public class Conditions {

	/**
	 * 
	 * 范围条件
	 * 
	 */
	public static Condition between(String attrName, Object startValue, Object endValue) {
		return new ConditionImpl(Type.BETWEEN, attrName, new Object[] { startValue, endValue });
	}
	/**
	 * 
	 * 范围条件
	 * 
	 */
	public static <T> Condition between(SerializableFunction<T, ?> attrGetter, Object startValue, Object endValue) {
		return between(LambdaReflections.fnToFieldName(attrGetter), startValue, endValue);
	}
	/**
	 * 
	 * 不为null值条件
	 * 
	 */
	public static Condition isNotNull(String attrName) {
		return new ConditionImpl(Type.IS_NOT_NULL, attrName);
	}
	/**
	 * 
	 * 不为null值条件
	 * 
	 */
	public static <T> Condition isNotNull(SerializableFunction<T, ?> attrGetter) {
		return isNotNull(LambdaReflections.fnToFieldName(attrGetter));
	}
	/**
	 * 
	 * 为null值条件
	 * 
	 */
	public static Condition isNull(String attrName) {
		return new ConditionImpl(Type.IS_NULL, attrName);
	}
	/**
	 * 
	 * 为null值条件
	 * 
	 */
	public static <T> Condition isNull(SerializableFunction<T, ?> attrGetter) {
		return isNull(LambdaReflections.fnToFieldName(attrGetter));
	}
	/**
	 * 
	 * 小于条件
	 * 
	 */
	public static Condition lessThan(String attrName, Object value) {
		return new ConditionImpl(Type.LESS_THAN, attrName, new Object[] { value });
	}
	/**
	 * 
	 * 小于条件
	 * 
	 */
	public static <T> Condition lessThan(SerializableFunction<T, ?> attrGetter, Object value) {
		return lessThan(LambdaReflections.fnToFieldName(attrGetter), value);
	}
	/**
	 * 
	 * 小于或等于条件
	 * 
	 */
	public static Condition lessThanEqual(String attrName, Object value) {
		return new ConditionImpl(Type.LESS_THAN_EQUAL, attrName, new Object[] { value });
	}
	/**
	 * 
	 * 小于或等于条件
	 * 
	 */
	public static <T> Condition lessThanEqual(SerializableFunction<T, ?> attrGetter, Object value) {
		return lessThanEqual(LambdaReflections.fnToFieldName(attrGetter), value);
	}
	/**
	 * 
	 * 大于条件
	 * 
	 */
	public static Condition greaterThan(String attrName, Object value) {
		return new ConditionImpl(Type.GREATER_THAN, attrName, new Object[] { value });
	}
	/**
	 * 
	 * 大于条件
	 * 
	 */
	public static <T> Condition greaterThan(SerializableFunction<T, ?> attrGetter, Object value) {
		return greaterThan(LambdaReflections.fnToFieldName(attrGetter), value);
	}
	/**
	 * 
	 * 大于或等于条件
	 * 
	 */
	public static Condition greaterThanEqual(String attrName, Object value) {
		return new ConditionImpl(Type.GREATER_THAN_EQUAL, attrName, new Object[] { value });
	}
	/**
	 * 
	 * 大于或等于条件
	 * 
	 */
	public static <T> Condition greaterThanEqual(SerializableFunction<T, ?> attrGetter, Object value) {
		return greaterThanEqual(LambdaReflections.fnToFieldName(attrGetter), value);
	}
	/**
	 * 
	 * 同lessThan
	 * 
	 */
	public static Condition before(String attrName, Object value) {
		return new ConditionImpl(Type.LESS_THAN, attrName, new Object[] { value });
	}
	/**
	 * 
	 * 同lessThan
	 * 
	 */
	public static <T> Condition before(SerializableFunction<T, ?> attrGetter, Object value) {
		return before(LambdaReflections.fnToFieldName(attrGetter), value);
	}
	/**
	 * 
	 * 同greaterThan
	 * 
	 */
	public static Condition after(String attrName, Object value) {
		return new ConditionImpl(Type.GREATER_THAN, attrName, new Object[] { value });
	}
	/**
	 * 
	 * 同greaterThan
	 * 
	 */
	public static <T> Condition after(SerializableFunction<T, ?> attrGetter, Object value) {
		return after(LambdaReflections.fnToFieldName(attrGetter), value);
	}
	/**
	 * 
	 * 非Like，不会自动添加模糊匹配
	 * 
	 */
	public static Condition notLike(String attrName, Object value) {
		return new ConditionImpl(Type.NOT_LIKE, attrName, new Object[] { value });
	}
	/**
	 * 
	 * Like，不会自动添加模糊匹配
	 * 
	 */
	public static Condition like(String attrName, Object value) {
		return new ConditionImpl(Type.LIKE, attrName, new Object[] { value });
	}
	/**
	 * 
	 * 以值开始，针对字符类型，前导匹配模式：LIKE {value}%
	 * 
	 */
	public static Condition startWith(String attrName, Object value) {
		return new ConditionImpl(Type.STARTING_WITH, attrName, new Object[] { value });
	}
	/**
	 * 
	 * 以值开始，针对字符类型，前导匹配模式：LIKE {value}%
	 * 
	 */
	public static<T>  Condition startWith(SerializableFunction<T, ?> attrGetter, Object value) {
		return startWith(LambdaReflections.fnToFieldName(attrGetter), value);
	}
	/**
	 * 
	 * 以值结束，针对字符类型，后置匹配模式：LIKE %{value}
	 * 
	 */
	public static Condition endWith(String attrName, Object value) {
		return new ConditionImpl(Type.ENDING_WITH, attrName, new Object[] { value });
	}
	/**
	 * 
	 * 以值结束，针对字符类型，后置匹配模式：LIKE %{value}
	 * 
	 */
	public static <T> Condition endWith(SerializableFunction<T, ?> attrGetter, Object value) {
		return endWith(LambdaReflections.fnToFieldName(attrGetter), value);
	}

	/**
	 * 不包含：NOT LIKE %{value}%
	 */
	public static Condition notContaining(String attrName, Object value) {
		return new ConditionImpl(Type.NOT_CONTAINING, attrName, new Object[] { value });
	}
	/**
	 * 不包含：NOT LIKE %{value}%
	 */
	public static <T> Condition notContaining(SerializableFunction<T, ?> attrGetter, Object value) {
		return notContaining(LambdaReflections.fnToFieldName(attrGetter), value);
	}
	/**
	 * 包含：LIKE %{value}%
	 */
	public static Condition containing(String attrName, Object value) {
		return new ConditionImpl(Type.CONTAINING, attrName, new Object[] { value });
	}
	/**
	 * 包含：LIKE %{value}%
	 */
	public static <T> Condition containing(SerializableFunction<T, ?> attrGetter, Object value) {
		return containing(LambdaReflections.fnToFieldName(attrGetter), value);
	}
	/**
	 * 不在：NOT IN {value}，value可为数组、集合或字符串（以半角逗号隔开）
	 */
	public static Condition notIn(String attrName, Object value) {
		return new ConditionImpl(Type.NOT_IN, attrName, new Object[] { value });
	}
	/**
	 * 不在：NOT IN {value}，value可为数组、集合或字符串（以半角逗号隔开）
	 */
	public static <T> Condition notIn(SerializableFunction<T, ?> attrGetter, Object value) {
		return notIn(LambdaReflections.fnToFieldName(attrGetter), value);
	}
	/**
	 * 在：IN {value}，value可为数组、集合或字符串（以半角逗号隔开）
	 */
	public static Condition in(String attrName, Object value) {
		return new ConditionImpl(Type.IN, attrName, new Object[] { value });
	}
	/**
	 * 在：IN {value}，value可为数组、集合或字符串（以半角逗号隔开）
	 */
	public static <T> Condition in(SerializableFunction<T, ?> attrGetter, Object value) {
		return in(LambdaReflections.fnToFieldName(attrGetter), value);
	}
	/**
	 * 正则匹配，部分数据库支持
	 */
	public static Condition regex(String attrName, Object value) {
		return new ConditionImpl(Type.REGEX, attrName, new Object[] { value });
	}
	/**
	 * 正则匹配，部分数据库支持
	 */
	public static <T> Condition regex(SerializableFunction<T, ?> attrGetter, Object value) {
		return regex(LambdaReflections.fnToFieldName(attrGetter), value);
	}
	/**
	 * 值为true
	 */
	public static Condition isTrue(String attrName) {
		return new ConditionImpl(Type.TRUE, attrName);
	}
	/**
	 * 值为true
	 */
	public static <T> Condition isTrue(SerializableFunction<T, ?> attrGetter) {
		return isTrue(LambdaReflections.fnToFieldName(attrGetter));
	}
	/**
	 * 值为false
	 */
	public static Condition isFalse(String attrName) {
		return new ConditionImpl(Type.FALSE, attrName);
	}
	/**
	 * 值为false
	 */
	public static <T> Condition isFalse(SerializableFunction<T, ?> attrGetter) {
		return isFalse(LambdaReflections.fnToFieldName(attrGetter));
	}
	/**
	 * 不等于
	 */
	public static Condition notEqual(String attrName, Object value) {
		return new ConditionImpl(Type.NEGATING_SIMPLE_PROPERTY, attrName, new Object[] { value });
	}
	/**
	 * 不等于
	 */
	public static <T> Condition notEqual(SerializableFunction<T, ?> attrGetter, Object value) {
		return notEqual(LambdaReflections.fnToFieldName(attrGetter), value);
	}
	/**
	 * 等于
	 */
	public static Condition equal(String attrName, Object value) {
		return new ConditionImpl(Type.SIMPLE_PROPERTY, attrName, new Object[] { value });
	}
	/**
	 * 等于
	 */
	public static <T> Condition equal(SerializableFunction<T, ?> attrGetter, Object value) {
		return equal(LambdaReflections.fnToFieldName(attrGetter), value);
	}
	
	/**
	 * 组合一个引用实体属性名（作为查询条件的属性）
	 * @param refAttrGetter 关联属性名
	 * @param refEntityAttGetter 关联属性对应实体的属性名
	 * @return 组合属性名
	 */
	public static <T> String composeRefEntityAttr(SerializableFunction<T, ?> refAttrGetter, SerializableFunction<T, ?> refEntityAttrGetter) {
		return LambdaReflections.fnToFieldName(refAttrGetter) + StringUtils.capitalize(LambdaReflections.fnToFieldName(refEntityAttrGetter));
	}
}
