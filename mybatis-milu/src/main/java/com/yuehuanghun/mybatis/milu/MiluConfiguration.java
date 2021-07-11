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
package com.yuehuanghun.mybatis.milu;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;

import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

import com.yuehuanghun.mybatis.milu.db.DbEnum;
import com.yuehuanghun.mybatis.milu.db.DbMeta;
import com.yuehuanghun.mybatis.milu.dialect.Dialect;
import com.yuehuanghun.mybatis.milu.dialect.PageDialectManager;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderSql;
import com.yuehuanghun.mybatis.milu.generic.impl.GenericBatchInsertProviderSql;
import com.yuehuanghun.mybatis.milu.generic.impl.GenericCountByCriteriaProviderSql;
import com.yuehuanghun.mybatis.milu.generic.impl.GenericCountByExampleProviderSql;
import com.yuehuanghun.mybatis.milu.generic.impl.GenericCountByLambdaCriteriaProviderSql;
import com.yuehuanghun.mybatis.milu.generic.impl.GenericDeleteByCriteriaProviderSql;
import com.yuehuanghun.mybatis.milu.generic.impl.GenericDeleteByIdProviderSql;
import com.yuehuanghun.mybatis.milu.generic.impl.GenericDeleteByIdsProviderSql;
import com.yuehuanghun.mybatis.milu.generic.impl.GenericDeleteByLambdaCriteriaProviderSql;
import com.yuehuanghun.mybatis.milu.generic.impl.GenericFindAllAndSortProviderSql;
import com.yuehuanghun.mybatis.milu.generic.impl.GenericFindAllProviderSql;
import com.yuehuanghun.mybatis.milu.generic.impl.GenericFindByCriteriaProviderSql;
import com.yuehuanghun.mybatis.milu.generic.impl.GenericFindByExampleAndSortProviderSql;
import com.yuehuanghun.mybatis.milu.generic.impl.GenericFindByExampleProviderSql;
import com.yuehuanghun.mybatis.milu.generic.impl.GenericFindByIdProviderSql;
import com.yuehuanghun.mybatis.milu.generic.impl.GenericFindByIdsProviderSql;
import com.yuehuanghun.mybatis.milu.generic.impl.GenericFindByLambdaCriteriaProviderSql;
import com.yuehuanghun.mybatis.milu.generic.impl.GenericFlushProviderSql;
import com.yuehuanghun.mybatis.milu.generic.impl.GenericInsertProviderSql;
import com.yuehuanghun.mybatis.milu.generic.impl.GenericStatisticByCriteriaProviderSql;
import com.yuehuanghun.mybatis.milu.generic.impl.GenericStatisticByLambdaCriteriaProviderSql;
import com.yuehuanghun.mybatis.milu.generic.impl.GenericUpdateByCriteriaProviderSql;
import com.yuehuanghun.mybatis.milu.generic.impl.GenericUpdateByIdProviderSql;
import com.yuehuanghun.mybatis.milu.generic.impl.GenericUpdateByLambdaCriteriaProviderSql;
import com.yuehuanghun.mybatis.milu.id.IdentifierGenerator;
import com.yuehuanghun.mybatis.milu.id.impl.UUIDIdentifierGenerator;
import com.yuehuanghun.mybatis.milu.id.impl.snowflake.SnowflakeIdentifierGenerator;
import com.yuehuanghun.mybatis.milu.metamodel.MetaModel;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

import lombok.Getter;
import lombok.Setter;

public class MiluConfiguration extends Configuration {

	private final MiluMapperRegistry miluMapperRegistry = new MiluMapperRegistry(this);
	private final Map<String, GenericProviderSql> genericProviderSqlMaps = new StrictMap<>("GenericProviderSql collection");
	private final Map<String, IdentifierGenerator> identifierGeneratorMaps = new StrictMap<>("AssignKeyGenerator collection");

	@Getter
	private DbMeta dbMeta;
	@Getter
	@Setter
	private Dialect dialect;
	@Getter
	private final MetaModel metaModel = new MetaModel();
	@Getter
	@Setter
	private boolean identifierWrapQuote = true; //标识符（表名、字段名）是否使用引号

	public MiluConfiguration() {
		super();
		registerGenericProviderSql();
		registerDefaultIdentifierGenerator();
	}
	
	public MiluConfiguration(Environment environment) {
	    this();
	    this.environment = environment;
	    buildDbMeta();
	  }
	
	private void registerGenericProviderSql() {
		this.addGenericProviderSql(new GenericCountByExampleProviderSql());
		this.addGenericProviderSql(new GenericDeleteByIdProviderSql());
		this.addGenericProviderSql(new GenericDeleteByIdsProviderSql());
		this.addGenericProviderSql(new GenericFindByExampleAndSortProviderSql());
		this.addGenericProviderSql(new GenericFindByExampleProviderSql());
		this.addGenericProviderSql(new GenericFindAllAndSortProviderSql());
		this.addGenericProviderSql(new GenericFindAllProviderSql());
		this.addGenericProviderSql(new GenericFindByIdProviderSql());
		this.addGenericProviderSql(new GenericFindByIdsProviderSql());
		this.addGenericProviderSql(new GenericInsertProviderSql());
		this.addGenericProviderSql(new GenericUpdateByIdProviderSql());
		this.addGenericProviderSql(new GenericFlushProviderSql());
		this.addGenericProviderSql(new GenericBatchInsertProviderSql());
		this.addGenericProviderSql(new GenericFindByCriteriaProviderSql());
		this.addGenericProviderSql(new GenericUpdateByCriteriaProviderSql());
		this.addGenericProviderSql(new GenericDeleteByCriteriaProviderSql());
		this.addGenericProviderSql(new GenericFindByLambdaCriteriaProviderSql());
		this.addGenericProviderSql(new GenericUpdateByLambdaCriteriaProviderSql());
		this.addGenericProviderSql(new GenericDeleteByLambdaCriteriaProviderSql());
		this.addGenericProviderSql(new GenericCountByCriteriaProviderSql());
		this.addGenericProviderSql(new GenericCountByLambdaCriteriaProviderSql());
		this.addGenericProviderSql(new GenericStatisticByCriteriaProviderSql());
		this.addGenericProviderSql(new GenericStatisticByLambdaCriteriaProviderSql());
	}
	
	private void registerDefaultIdentifierGenerator() {
		this.addIdentifierGenerator(new UUIDIdentifierGenerator());
		this.addIdentifierGenerator(new SnowflakeIdentifierGenerator());
	}

	@Override
	public MapperRegistry getMapperRegistry() {
		return miluMapperRegistry;
	}

	@Override
	public void addMappers(String packageName, Class<?> superType) {
		miluMapperRegistry.addMappers(packageName, superType);
	}

	@Override
	public void addMappers(String packageName) {
		miluMapperRegistry.addMappers(packageName);
	}

	@Override
	public <T> void addMapper(Class<T> type) {
		miluMapperRegistry.addMapper(type);
	}

	@Override
	public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
		return miluMapperRegistry.getMapper(type, sqlSession);
	}

	@Override
	public boolean hasMapper(Class<?> type) {
		return miluMapperRegistry.hasMapper(type);
	}

	@Override
	public void setEnvironment(Environment environment) {
		super.setEnvironment(environment);
		buildDbMeta();
	}
	
	private void addGenericProviderSql(GenericProviderSql genericProviderSql) {
		genericProviderSqlMaps.put(genericProviderSql.getMethodName(), genericProviderSql);
	}
	
	public GenericProviderSql getGenericProviderSql(String methodName) {
		return genericProviderSqlMaps.get(methodName);
	}
	
	public void addIdentifierGenerator(IdentifierGenerator assignKeyGenerator) {
		identifierGeneratorMaps.put(assignKeyGenerator.getName(), assignKeyGenerator);
	}
	
	public IdentifierGenerator getIdentifierGenerator(String identifierGeneratorName) {
		return identifierGeneratorMaps.get(identifierGeneratorName);
	}
	
	public boolean hasIdentifierGenerator(String identifierGeneratorName) {
		return identifierGeneratorMaps.containsKey(identifierGeneratorName);
	}

	public void buildDbMeta() {
		if(this.environment == null) {
			return;
		}
		
		dbMeta = new DbMeta();
		Connection conn = null;
		
		try {
			conn = environment.getDataSource().getConnection();
			
			DatabaseMetaData metaData = conn.getMetaData();
			dbMeta.setIdentifierQuoteString(StringUtils.defaultBlank(metaData.getIdentifierQuoteString()));
			
			dbMeta.setDbName(metaData.getDatabaseProductName());
			dbMeta.setDbVersion(metaData.getDatabaseProductVersion());
        	
			DbEnum cur = DbEnum.OTHER;
			String connUrl = metaData.getURL();
			for(DbEnum dbEnum : DbEnum.values()) {
				if(connUrl.contains(":" + dbEnum.getDbName() + ":")) {
					cur = dbEnum;
					break;
				}
			}
			dbMeta.setDbEnum(cur);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// 
				}
			}
		}
		
		this.dialect = PageDialectManager.getDialect(dbMeta.getDbEnum().getDbName());
		if(this.dialect == null) {
			throw new RuntimeException(String.format("未知的数据库：%s", dbMeta.getDbName()));
		}
	}
}
