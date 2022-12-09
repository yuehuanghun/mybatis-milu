/*
 * Copyright 2020-2022 the original author or authors.
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
package com.yuehuanghun.mybatis.milu.tool;

/**
 * 字符串片断
 * @author yuehuanghun
 *
 */
public interface Segment {

	String AMPERSAND = "&";
    String AND = "AND";
    String AND_B = " AND ";
    String ATTR_ = "attr_";
    String CLOSING_ANGLE_BRACKET = ">";
    String COMMA = ",";
    String COMMA_B = ", ";
    String COUNT_ALL = "COUNT(*) ";
    String DELETE_FROM = "DELETE FROM ";
    String DISTINCT = "DISTINCT ";
    String DOLLAR = "$";
    String DOT = ".";
    String EQUALS = "=";
    String EQUALS_B = " = ";
    String EMPTY = "";
    String EXAMPLE_TO_COLLECTION = "@com.yuehuanghun.mybatis.milu.tool.StringUtils@toCollection(example.";
    String FOR_UPDATE = " FOR UPDATE";
    String FROM_B = " FROM ";
    String GROUP_BY_B = " GROUP BY ";
    String INNER_JOIN_B = " INNER JOIN ";
    String IF_LABEL_END = "</if>";
    String IF_TEST_EXAMPLE = "<if test=\"example.";
    String IF_TEST_EXAMPLE_NOT_BLANK = "<if test=\"@com.yuehuanghun.mybatis.milu.tool.StringUtils@notBlank(example.";
    String LEFT_BRACE = "{";
    String LEFT_BRACKET = "(";
    String LEFT_JOIN_B = " LEFT JOIN ";
    String NOT_EQUAL_NULL_CLOSING = " != null\">";
    String NOT_EQUAL_NULL_AND_EXAMPLE = " != null and example.";
    String NOT_EMPTY_NULL_CLOSING = " != ''\">";
    String OR_B = " OR ";
    String ORDER_BY = " ORDER BY ";
    String ON_BRACKET = " ON (";
    String RIGHT_BRACE = "}";
    String RIGHT_BRACKET = ")";
    String RIGHT_BRACKET_CLOSING = ")\">";
    String RIGHT_JOIN_B = " RIGHT JOIN ";
    String HASH_LEFT_BRACE = "#{";
    String HASH_EXAMPLE = "#{example.";
    String HYPHEN = "-";
    String UNDER_LINE = "_";
    String UPDATE = "UPDATE ";
    String WHERE_B = " WHERE ";
    String SCRIPT_LABEL = "<script>";
    String SCRIPT_LABEL_END = "</script>";
    String SELECT_COUNT_FROM = "SELECT COUNT(*) FROM ";
    String SIGLE_QUOT = "'";
    String TABLE_ = "table_";
    String WHERE_LABEL = "<where>";
    String WHERE_LABEL_END = "</where>";
    String SET_LABEL = "<set>";
    String SET_LABEL_END = "</set>";
    String SELECT = "SELECT ";
    String SELECT_COUNT = "SELECT COUNT(*)";
    String SORT = "sort";
    String SPACE = " ";
    String DELETE_COND_HODLER = "$deledHolder$";
    String UNDELETE_COND_HODLER = "$undeledHolder$";
}
