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
package com.yuehuanghun.mybatis.milu.generic.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;

import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.db.DbEnum;
import com.yuehuanghun.mybatis.milu.generic.GenericCachingProviderSql;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.IdAttribute;

/**
 * 批量插入的SQL构建<br>
 * 除ID根据构建方式而定外，其它字段都会插入，对于非空的字段，需要先设值，否则会发生非空约束问题
 * @author yuehuanghun
 *
 */
public class GenericBatchInsertProviderSql extends GenericCachingProviderSql {

	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		setBatchId(context.getEntity(), params, context);
		SqlBuildingHelper.fill(params, true, context.getConfiguration());
		return super.provideSql(context, params);
	}
	
	@Override
	public String provideCachingSql(GenericProviderContext context, Object params) {
		DbEnum curDb = context.getConfiguration().getDbMeta().getDbEnum();
		if(COMMON_BATCH_INSERT_DBS.contains(curDb)) {
			return commonBatchSql(context);
		} else if(ORACLE_LIKE_BATCH_INSERT_DBS.contains(curDb)) {
			return oracleBatchSql(context);
		} else if(SQLSERVER_LIKE_BATCH_INSERT_DBS.contains(curDb)) {
			return sqlserverBatchSql(context);
		} else {
			throw new RuntimeException("当前数据未支持批量插入");
		}
	}
	
	private String commonBatchSql(GenericProviderContext context) {
		Entity entity = context.getEntity();		
		Collection<Attribute> attributes = entity.getAttributes();
		
		StringBuilder sqlBuilder = new StringBuilder(1024).append("<script>");
		sqlBuilder.append("INSERT INTO ").append(wrapTableName(entity, context));
		sqlBuilder.append(" (<trim suffixOverrides=\",\">");
		
		StringBuilder values = new StringBuilder(" VALUES <foreach collection=\"entityList\" item=\"item\" separator=\",\">(<trim suffixOverrides=\",\">");
		for(Attribute attr : attributes) {
			if(!attr.isInsertable()) {
				continue;
			}
	 
			if(attr.isId() && (context.getKeyGenerator() == NoKeyGenerator.INSTANCE || Jdbc3KeyGenerator.class.isInstance(context.getKeyGenerator()))) {
				sqlBuilder.append(" <if test=\"entityList[0].").append(attr.getName()).append(" != null\">").append(wrapIdentifier(attr.getColumnName(), context)).append(",</if> ");
				values.append(" <if test=\"entityList[0].").append(attr.getName()).append(" != null\">").append("#{item.").append(attr.toParameter()).append("},</if> ");
				continue;
			}
			sqlBuilder.append(wrapIdentifier(attr.getColumnName(), context)).append(", ");
			values.append("#{item.").append(attr.toParameter()).append("}, ");
		}
		sqlBuilder.append(" </trim>) ");
		values.append("</trim>)</foreach> ");
		sqlBuilder.append(values);
		sqlBuilder.append("</script>");
		return sqlBuilder.toString();
	}
	
	private String oracleBatchSql(GenericProviderContext context) {
		Entity entity = context.getEntity();		
		Collection<Attribute> attributes = entity.getAttributes();
		
		StringBuilder sqlBuilder = new StringBuilder(1024).append("<script>");
		sqlBuilder.append("INSERT INTO ").append(wrapTableName(entity, context));
		sqlBuilder.append(" (<trim suffixOverrides=\",\">");
		
		StringBuilder values = new StringBuilder(" <foreach collection=\"entityList\" item=\"item\" separator=\"UNION ALL\">");
		values.append("SELECT <trim suffixOverrides=\",\">");
		for(Attribute attr : attributes) {
			if(!attr.isInsertable()) {
				continue;
			}
	 
			if(attr.isId() && (context.getKeyGenerator() == NoKeyGenerator.INSTANCE || Jdbc3KeyGenerator.class.isInstance(context.getKeyGenerator()))) {
				continue;
			}
			sqlBuilder.append(wrapIdentifier(attr.getColumnName(), context)).append(", ");
			values.append("#{item.").append(attr.toParameter()).append("}, ");
		}
		sqlBuilder.append(" </trim>) ");
		values.append("</trim> FROM dual</foreach> ");
		sqlBuilder.append(values);
		sqlBuilder.append("</script>");
		return sqlBuilder.toString();
	}
	
	//至少从sqlserver2019起已经支持values (),()...的批量插入模式，为了兼容现依然使用旧模式
	private String sqlserverBatchSql(GenericProviderContext context) {
		Entity entity = context.getEntity();		
		Collection<Attribute> attributes = entity.getAttributes();
		
		StringBuilder sqlBuilder = new StringBuilder(1024).append("<script>");
		sqlBuilder.append("INSERT INTO ").append(wrapTableName(entity, context));
		sqlBuilder.append(" (<trim suffixOverrides=\",\">");
		
		StringBuilder values = new StringBuilder(" <foreach collection=\"entityList\" item=\"item\" separator=\"UNION ALL\">");
		values.append("SELECT <trim suffixOverrides=\",\">");
		for(Attribute attr : attributes) {
			if(!attr.isInsertable()) {
				continue;
			}
	 
			if(attr.isId() && (context.getKeyGenerator() == NoKeyGenerator.INSTANCE || Jdbc3KeyGenerator.class.isInstance(context.getKeyGenerator()))) {
				continue;
			}
			sqlBuilder.append(wrapIdentifier(attr.getColumnName(), context)).append(", ");
			values.append("#{item.").append(attr.toParameter()).append("}, ");
		}
		sqlBuilder.append(" </trim>) ");
		values.append("</trim></foreach> ");
		sqlBuilder.append(values);
		sqlBuilder.append("</script>");
		return sqlBuilder.toString();
	}
	
	//列表入参每行设置id值
	private void setBatchId(Entity entity, Object params, GenericProviderContext context) {
		KeyGenerator keyGenerator = context.getKeyGenerator();
		if(keyGenerator == NoKeyGenerator.INSTANCE || Jdbc3KeyGenerator.class.isInstance(keyGenerator)) {
			return;
		}
		
		IdAttribute idAttr = entity.getId();
		if(idAttr == null) {
			return;
		}

		MiluConfiguration configuration = context.getConfiguration();
		@SuppressWarnings("rawtypes")
		List<?> paramList = (List<?>) ((Map)params).get("entityList");
		
		Transaction transaction = configuration.getEnvironment().getTransactionFactory().newTransaction(configuration.getEnvironment().getDataSource(), TransactionIsolationLevel.READ_COMMITTED, true);
		Executor executor = configuration.newExecutor(transaction);
		MappedStatement mockMappedStatement = new MappedStatement.Builder(configuration, "mockId", MOCK_SQL_SOURCE, SqlCommandType.UNKNOWN).keyProperty(idAttr.getName()).build();
		
		for(Object row : paramList) {
			keyGenerator.processBefore(executor, mockMappedStatement, null, row);
		}
	}

	@Override
	public String getMethodName() {
		return "batchInsert";
	}

	private final static SqlSource MOCK_SQL_SOURCE = new SqlSource() {
		@Override
		public BoundSql getBoundSql(Object parameterObject) {
			return null;
		}
	};
	
	private static final List<DbEnum> COMMON_BATCH_INSERT_DBS = Arrays.asList(DbEnum.MYSQL, DbEnum.MARIADB, DbEnum.POSTGRES, DbEnum.SQLITE, DbEnum.OSCAR, DbEnum.CLICKHOUSE, DbEnum.DM);
	private static final List<DbEnum> ORACLE_LIKE_BATCH_INSERT_DBS =  Arrays.asList(DbEnum.ORCLE, DbEnum.EDB);
	private static final List<DbEnum> SQLSERVER_LIKE_BATCH_INSERT_DBS =  Arrays.asList(DbEnum.SQLSERVER);
}
