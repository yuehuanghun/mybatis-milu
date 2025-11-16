/*
 * Copyright 2020-current the original author or authors.
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

package com.yuehuanghun.mybatis.milu.criteria.ext.postgre.conds;

import com.yuehuanghun.mybatis.milu.criteria.ext.CompareMode;

/**
 * Postgre专用查询条件工具类
 */
public class PostgreConditions {

	/**
	 * json值比较-等于
	 * @param attrName json属性
	 * @param jsonKey 定位数据的json键路径，例如{"a":"b", "c":"d"} ->> 'a' 得到 b，传入a字符串
	 * @param jsonVal 比较的值
	 * @return 动态条件
	 */
	public static PostgreJsonCompare jsonEquals(String attrName, String jsonKey, Object jsonVal) {
		return new PostgreJsonCompare(attrName, jsonKey, jsonVal, CompareMode.EQUALS);
	}

	/**
	 * json值比较-不等于
	 * @param attrName json属性
	 * @param jsonKey 定位数据的json键路径，例如{"a":"b", "c":"d"} ->> 'a' 得到 b，传入a字符串
	 * @param jsonVal 比较的值
	 * @return 动态条件
	 */
	public static PostgreJsonCompare jsonNotEquals(String attrName, String jsonKey, Object jsonVal) {
		return new PostgreJsonCompare(attrName, jsonKey, jsonVal, CompareMode.NOT_EQUALS);
	}

	/**
	 * json值比较-大于
	 * @param attrName json属性
	 * @param jsonKey 定位数据的json键路径，例如{"a":"b", "c":"d"} ->> 'a' 得到 b，传入a字符串
	 * @param jsonVal 比较的值
	 * @return 动态条件
	 */
	public static PostgreJsonCompare jsonGreaterThan(String attrName, String jsonKey, Object jsonVal) {
		return new PostgreJsonCompare(attrName, jsonKey, jsonVal, CompareMode.GREATER_THAN);
	}

	/**
	 * json值比较-小于
	 * @param attrName json属性
	 * @param jsonKey 定位数据的json键路径，例如{"a":"b", "c":"d"} ->> 'a' 得到 b，传入a字符串
	 * @param jsonVal 比较的值
	 * @return 动态条件
	 */
	public static PostgreJsonCompare jsonLessThan(String attrName, String jsonKey, Object jsonVal) {
		return new PostgreJsonCompare(attrName, jsonKey, jsonVal, CompareMode.LESS_THAN);
	}
	
	/**
	 * json值比较-等于
	 * @param attrName json属性
	 * @param jsonKey 定位数据的json键路径，例如{"a":{"b":"c"}} #>> '{a,b}' 得到 c，传入{a,b}字符串
	 * @param jsonVal 比较的值
	 * @return 动态条件
	 */
	public static PostgreJsonPathCompare jsonPathEquals(String attrName, String jsonKey, Object jsonVal) {
		return new PostgreJsonPathCompare(attrName, jsonKey, jsonVal, CompareMode.EQUALS);
	}

	/**
	 * json值比较-不等于
	 * @param attrName json属性
	 * @param jsonKey 定位数据的json键路径，例如{"a":{"b":"c"}} #>> '{a,b}' 得到 c，传入{a,b}字符串
	 * @param jsonVal 比较的值
	 * @return 动态条件
	 */
	public static PostgreJsonPathCompare jsonPathNotEquals(String attrName, String jsonKey, Object jsonVal) {
		return new PostgreJsonPathCompare(attrName, jsonKey, jsonVal, CompareMode.NOT_EQUALS);
	}

	/**
	 * json值比较-大于
	 * @param attrName json属性
	 * @param jsonKey 定位数据的json键路径，例如{"a":{"b":"c"}} #>> '{a,b}' 得到 c，传入{a,b}字符串
	 * @param jsonVal 比较的值
	 * @return 动态条件
	 */
	public static PostgreJsonPathCompare jsonPathGreaterThan(String attrName, String jsonKey, Object jsonVal) {
		return new PostgreJsonPathCompare(attrName, jsonKey, jsonVal, CompareMode.GREATER_THAN);
	}

	/**
	 * json值比较-小于
	 * @param attrName json属性
	 * @param jsonKey 定位数据的json键路径，例如{"a":{"b":"c"}} #>> '{a,b}' 得到 c，传入{a,b}字符串
	 * @param jsonVal 比较的值
	 * @return 动态条件
	 */
	public static PostgreJsonPathCompare jsonPathLessThan(String attrName, String jsonKey, Object jsonVal) {
		return new PostgreJsonPathCompare(attrName, jsonKey, jsonVal, CompareMode.LESS_THAN);
	}
}
