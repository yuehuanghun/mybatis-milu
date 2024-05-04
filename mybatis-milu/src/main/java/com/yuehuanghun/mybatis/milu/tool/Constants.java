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
package com.yuehuanghun.mybatis.milu.tool;

public interface Constants {

	/**
	 * 内置的32位UUID构造器（String）
	 */
	String ID_GENERATOR_UUID = "uuid";
	/**
	 * 内置的snowflakeId构造器（Long）
	 */
	String ID_GENERATOR_SNOWFLAKE = "snowflake";
	
	/**
	 * 分页参数
	 */
	String PAGE_KEY = "_MILU_PAGE_";
	
	/**
	 * 列名占位
	 */
	String COLUMN_HOLDER = "ColumnHolder";
	
	/**
	 * FROM TABLE 占位
	 */
	String TABLE_HOLDER = "__FromTablesHolder__";
	
	/**
	 * 任意关联属性标识
	 */
	String ANY_REF_PROPERTY = "default";
	
	String CRITERIA = "criteria";
	
	String ID = "id";
	
	String IDS = "ids";
	
	String SORT = "sort";
	
	String EXAMPLE = "example";
	
	String ENTITY_LIST = "entityList";
	
	String ENTITY = "entity";
	
	String RESULT_TYPE = "resultType";
	
	String PAGE = "page";
	
	String PATCH = "patch";
	
	String ATTR_NAME = "attrName";
	
	String VALUE = "value";

	String GROUP = "group";
	
	String SELECTS = "selects";
	
	String INDEX = "index";
}
