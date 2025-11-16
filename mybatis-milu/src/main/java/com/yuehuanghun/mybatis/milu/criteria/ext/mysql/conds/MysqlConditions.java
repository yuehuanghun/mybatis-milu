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

package com.yuehuanghun.mybatis.milu.criteria.ext.mysql.conds;

import com.yuehuanghun.mybatis.milu.criteria.ext.CompareMode;
import com.yuehuanghun.mybatis.milu.criteria.ext.mysql.conds.MysqlJsonLike.LikeMode;

/**
 * mysql专用查询条件工具类
 */
public class MysqlConditions {

	/**
	 * json值搜索-包含指定值
	 * @param attrName json属性
	 * @param jsonKey 定位数据的json键路径，如$.name
	 * @param jsonVal 搜索的值
	 * @return 动态条件
	 */
	public static MysqlJsonLike jsonContains(String attrName, String jsonKey, String jsonVal) {
		return new MysqlJsonLike(attrName, jsonKey, jsonVal, LikeMode.CONTAINS);
	}

	/**
	 * json值搜索-以指定值开头
	 * @param attrName json属性
	 * @param jsonKey 定位数据的json键路径，如$.name
	 * @param jsonVal 搜索的值
	 * @return 动态条件
	 */
	public static MysqlJsonLike jsonStartsWith(String attrName, String jsonKey, String jsonVal) {
		return new MysqlJsonLike(attrName, jsonKey, jsonVal, LikeMode.STARTS_WITH);
	}

	/**
	 * json值搜索-以指定值结尾
	 * @param attrName json属性
	 * @param jsonKey 定位数据的json键路径，如$.name
	 * @param jsonVal 搜索的值
	 * @return 动态条件
	 */
	public static MysqlJsonLike jsonEndsWith(String attrName, String jsonKey, String jsonVal) {
		return new MysqlJsonLike(attrName, jsonKey, jsonVal, LikeMode.ENDS_WITH);
	}

	/**
	 * json值比较-等于
	 * @param attrName json属性
	 * @param jsonKey 定位数据的json键路径，如$.name
	 * @param jsonVal 比较的值
	 * @return 动态条件
	 */
	public static MysqlJsonCompare jsonEquals(String attrName, String jsonKey, Object jsonVal) {
		return new MysqlJsonCompare(attrName, jsonKey, jsonVal, CompareMode.EQUALS);
	}

	/**
	 * json值比较-不等于
	 * @param attrName json属性
	 * @param jsonKey 定位数据的json键路径，如$.name
	 * @param jsonVal 比较的值
	 * @return 动态条件
	 */
	public static MysqlJsonCompare jsonNotEquals(String attrName, String jsonKey, Object jsonVal) {
		return new MysqlJsonCompare(attrName, jsonKey, jsonVal, CompareMode.NOT_EQUALS);
	}

	/**
	 * json值比较-大于
	 * @param attrName json属性
	 * @param jsonKey 定位数据的json键路径，如$.name
	 * @param jsonVal 比较的值
	 * @return 动态条件
	 */
	public static MysqlJsonCompare jsonGreaterThan(String attrName, String jsonKey, Object jsonVal) {
		return new MysqlJsonCompare(attrName, jsonKey, jsonVal, CompareMode.GREATER_THAN);
	}

	/**
	 * json值比较-小于
	 * @param attrName json属性
	 * @param jsonKey 定位数据的json键路径，如$.name
	 * @param jsonVal 比较的值
	 * @return 动态条件
	 */
	public static MysqlJsonCompare jsonLessThan(String attrName, String jsonKey, Object jsonVal) {
		return new MysqlJsonCompare(attrName, jsonKey, jsonVal, CompareMode.LESS_THAN);
	}
}
