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
package com.yuehuanghun.mybatis.milu.db;

import lombok.Getter;

public enum DbEnum {

	ORCLE("oracle"), MYSQL("mysql"), MARIADB("mariadb"), POSTGRES("postgresql"), SQLSERVER("sqlserver"), H2("h2"),
	DB2("db2"), SQLITE("sqlite"), HERDDB("herddb"), INFORMIX("informix"), DERBY("derby"), DM("dm"), EDB("edb"),
	OSCAR("oscar"), CLICKHOUSE("clickhouse"), OTHER("other");

	@Getter
	private String dbName;
	private DbEnum(String dbName) {
		this.dbName = dbName;
	}
}
