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
package com.yuehuanghun.mybatis.milu.dialect;

import java.util.HashMap;
import java.util.Map;

import com.yuehuanghun.mybatis.milu.dialect.db.Db2Dialect;
import com.yuehuanghun.mybatis.milu.dialect.db.HerdDBDialect;
import com.yuehuanghun.mybatis.milu.dialect.db.HsqldbDialect;
import com.yuehuanghun.mybatis.milu.dialect.db.InformixDialect;
import com.yuehuanghun.mybatis.milu.dialect.db.MysqlDialect;
import com.yuehuanghun.mybatis.milu.dialect.db.OracleDialect;
import com.yuehuanghun.mybatis.milu.dialect.db.PostgreSqlDialect;
import com.yuehuanghun.mybatis.milu.dialect.db.SqlServer2012Dialect;
import com.yuehuanghun.mybatis.milu.dialect.db.SqlServerDialect;

public class PageDialectManager {
	private static Map<String, Dialect> DIALECT_ALIAS_MAP = new HashMap<>();

    public static void registerDialectAlias(String alias, Dialect dialect){
    	DIALECT_ALIAS_MAP.put(alias, dialect);
    }
    
    static {
        //注册别名
        registerDialectAlias("hsqldb", HsqldbDialect.instance);
        registerDialectAlias("h2", HsqldbDialect.instance);
        registerDialectAlias("postgresql", PostgreSqlDialect.instance);
        registerDialectAlias("phoenix", HsqldbDialect.instance);

        registerDialectAlias("mysql", MysqlDialect.instance);
        registerDialectAlias("mariadb", MysqlDialect.instance);
        registerDialectAlias("sqlite", MysqlDialect.instance);

        registerDialectAlias("herddb", HerdDBDialect.instance);

        registerDialectAlias("oracle", OracleDialect.instance);
        registerDialectAlias("db2", Db2Dialect.instance);
        registerDialectAlias("informix", InformixDialect.instance);
        registerDialectAlias("informix-sqli", InformixDialect.instance);

        registerDialectAlias("sqlserver", SqlServerDialect.instance);
        registerDialectAlias("sqlserver2012", SqlServer2012Dialect.instance);

        registerDialectAlias("derby", SqlServer2012Dialect.instance);
        registerDialectAlias("dm", OracleDialect.instance); //达梦
        registerDialectAlias("edb", OracleDialect.instance);
        //神通数据库
        registerDialectAlias("oscar", MysqlDialect.instance);
        registerDialectAlias("clickhouse", MysqlDialect.instance);
    }
    
    public static Dialect getDialect(String alias) {
    	return DIALECT_ALIAS_MAP.get(alias);
    }
}
