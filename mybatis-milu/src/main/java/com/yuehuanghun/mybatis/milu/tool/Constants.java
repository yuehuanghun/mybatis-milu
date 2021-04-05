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
	 * 
	 */
	String COLUMN_HOLDER = "ColumnHolder";
}
