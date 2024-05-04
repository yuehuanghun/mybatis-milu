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

import java.util.ArrayList;
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
import com.yuehuanghun.mybatis.milu.exception.OrmRuntimeException;
import com.yuehuanghun.mybatis.milu.generic.GenericCachingProviderSql;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.IdAttribute;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Index;
import com.yuehuanghun.mybatis.milu.tool.Constants;
import com.yuehuanghun.mybatis.milu.tool.Segment;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

/**
 * 批量合并的SQL构建<br>
 * 除ID根据构建方式而定外，其它字段都会插入，对于非空的字段，需要先设值，否则会发生非空约束问题
 * @author yuehuanghun
 *
 */
public class GenericBatchMergeProviderSql extends GenericCachingProviderSql {

	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		setBatchId(context.getEntity(), params, context);
		SqlBuildingHelper.fill(params, true, context.getConfiguration());
		return super.provideSql(context, params);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public String provideCachingSql(GenericProviderContext context, Object params) {
		String indexName = (String) ((Map)params).remove(Constants.INDEX);
		List<Attribute> conflictIndexAttrs;
		if(StringUtils.isBlank(indexName)) {
			conflictIndexAttrs = new ArrayList<>();
			IdAttribute idAttr = context.getEntity().getId();
			if(idAttr != null) {
				conflictIndexAttrs.add(idAttr);
			}
		} else {
			Index index = context.getEntity().getIndex(indexName);
			if(index == null) {
				throw new OrmRuntimeException(String.format("索引“%s”未定义，请使用AttributeOptions注解加以声明索引。", indexName));
			}
			conflictIndexAttrs = index.getAttrs();
		}
		
		DbEnum curDb = context.getConfiguration().getDbMeta().getDbEnum();
		if(MYSQL_LIKE_BATCH_INSERT_DBS.contains(curDb)) {
			return mysqlBatchSql(context, conflictIndexAttrs);
		} else if(ORACLE_LIKE_BATCH_INSERT_DBS.contains(curDb)) {
			return oracleBatchSql(context, conflictIndexAttrs);
		} else if(POSTGRE_LIKE_BATCH_INSERT_DBS.contains(curDb)) {
			return postgreBatchSql(context, conflictIndexAttrs);
		} else {
			throw new OrmRuntimeException("当前数据未支持批量合并");
		}
	}
	
	private String mysqlBatchSql(GenericProviderContext context, List<Attribute> conflictIndexAttrs) {
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
		sqlBuilder.append(" ON DUPLICATE KEY UPDATE <trim suffixOverrides=\",\">");
		for(Attribute attr : entity.getAttributes()) {
			if(attr.isUpdateable() && !conflictIndexAttrs.contains(attr)) {
				String columnName = wrapIdentifier(attr.getColumnName(), context);
				sqlBuilder.append(columnName).append(Segment.EQUALS_B).append("VALUES(").append(columnName).append("), ");
			}
		}
		sqlBuilder.append(" </trim>");
		sqlBuilder.append("</script>");
		return sqlBuilder.toString();
	}

	// PostgreSQL
	private String postgreBatchSql(GenericProviderContext context, List<Attribute> conflictIndexAttrs) {
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
		sqlBuilder.append("</trim>) ");
		values.append("</trim>)</foreach> ");
		sqlBuilder.append(values);
		
		sqlBuilder.append(" ON CONFLICT (").append("<trim suffixOverrides=\",\">");
		for(Attribute attr : conflictIndexAttrs) {
			sqlBuilder.append(wrapIdentifier(attr.getColumnName(), context))
			  .append(Segment.COMMA_B);
		}
		sqlBuilder.append("</trim>)");
		
		sqlBuilder.append(" DO UPDATE <set suffixOverrides=\",\">");
		for(Attribute attr : entity.getAttributes()) {
			if(attr.isUpdateable() && !conflictIndexAttrs.contains(attr)) {
				String columnName = wrapIdentifier(attr.getColumnName(), context);
				sqlBuilder.append(columnName).append(Segment.EQUALS_B).append("EXCLUDED.").append(columnName).append(Segment.COMMA_B);
			}
		}
		sqlBuilder.append("</set>");
		sqlBuilder.append("</script>");
		return sqlBuilder.toString();
	}
	
	private String oracleBatchSql(GenericProviderContext context, List<Attribute> conflictIndexAttrs) {
		Entity entity = context.getEntity();		
		Collection<Attribute> attributes = entity.getAttributes();
		
		String tableAlias = wrapIdentifier("tb", context);
		String tableAliasPrefix = tableAlias + Segment.DOT;
		String tempAlias = wrapIdentifier("tmp", context);
		String tempAliasPrefix = tempAlias + Segment.DOT;
		StringBuilder sqlBuilder = new StringBuilder(1024).append("<script>");
		sqlBuilder.append("MERGE INTO ").append(wrapTableName(entity, context))
		          .append(Segment.SPACE).append(tableAlias);
		sqlBuilder.append(" USING(<trim suffixOverrides=\",\">");
		
		StringBuilder updates = new StringBuilder(" WHEN MATCHED THEN UPDATE <set>");
		StringBuilder insertCols = new StringBuilder(" WHEN NOT MATCHED THEN INSERT <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
		StringBuilder insertVals = new StringBuilder(" <trim prefix=\"VALUES (\" suffix=\")\" suffixOverrides=\",\">");
		sqlBuilder.append(" <foreach collection=\"entityList\" item=\"item\" separator=\" UNION ALL \">")
		          .append("SELECT <trim suffixOverrides=\",\">");
		for(Attribute attr : attributes) {
//			sqlBuilder.append(wrapIdentifier(attr.getColumnName(), context)).append(", ");
			String wrapedColumnName = wrapIdentifier(attr.getColumnName(), context);
			sqlBuilder.append("#{item.").append(attr.toParameter()).append("} ").append(wrapedColumnName).append(Segment.COMMA_B);
			
			// 构建tb.columnName = tmp.columnName,
			if(!conflictIndexAttrs.contains(attr) && attr.isUpdateable()) {
				updates.append(tableAliasPrefix).append(wrapedColumnName).append(Segment.EQUALS_B).append(tempAliasPrefix).append(wrapedColumnName).append(Segment.COMMA_B);
			}
			
			if(!attr.isInsertable()) {
				continue;
			}
	 
			if(attr.isId() && (context.getKeyGenerator() == NoKeyGenerator.INSTANCE || Jdbc3KeyGenerator.class.isInstance(context.getKeyGenerator()))) {
				continue;
			}
			
			// 构建tb.columnName,
			insertCols.append(tableAliasPrefix).append(wrapedColumnName).append(Segment.COMMA_B);
			// 构建tmp.columnName,
			insertVals.append(tempAliasPrefix).append(wrapedColumnName).append(Segment.COMMA_B);
		}
		updates.append("</set>");
		insertCols.append("</trim>");
		insertVals.append("</trim>");
		
		sqlBuilder.append("</trim> FROM dual</foreach></trim>) ").append(tempAlias);
		// 构建条件连接 ON (tb.columnNmae = tmp.columName)
		sqlBuilder.append(" <trim prefix=\"ON (\" suffix=\")\" suffixOverrides=\"AND\">");
		for(Attribute attr : conflictIndexAttrs) {
			String wrapedColumnName = wrapIdentifier(attr.getColumnName(), context);
			sqlBuilder.append(tableAliasPrefix).append(wrapedColumnName).append(Segment.EQUALS_B).append(tempAliasPrefix).append(wrapedColumnName).append(Segment.AND_B);
		}
		sqlBuilder.append("</trim>");
		sqlBuilder.append(updates);
		sqlBuilder.append(insertCols).append(insertVals);
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
		List<?> paramList = (List<?>) ((Map)params).get(Constants.ENTITY_LIST);
		
		Transaction transaction = configuration.getEnvironment().getTransactionFactory().newTransaction(configuration.getEnvironment().getDataSource(), TransactionIsolationLevel.READ_COMMITTED, true);
		Executor executor = configuration.newExecutor(transaction);
		MappedStatement mockMappedStatement = new MappedStatement.Builder(configuration, "mockId", MOCK_SQL_SOURCE, SqlCommandType.UNKNOWN).keyProperty(idAttr.getName()).build();
		
		for(Object row : paramList) {
			keyGenerator.processBefore(executor, mockMappedStatement, null, row);
		}
	}

	@Override
	public String getMethodName() {
		return "batchMerge";
	}

	private final static SqlSource MOCK_SQL_SOURCE = new SqlSource() {
		@Override
		public BoundSql getBoundSql(Object parameterObject) {
			return null;
		}
	};
	
	private static final List<DbEnum> MYSQL_LIKE_BATCH_INSERT_DBS = Arrays.asList(DbEnum.MYSQL, DbEnum.MARIADB, DbEnum.OPENGAUSS);
	private static final List<DbEnum> POSTGRE_LIKE_BATCH_INSERT_DBS = Arrays.asList(DbEnum.POSTGRES);
	private static final List<DbEnum> ORACLE_LIKE_BATCH_INSERT_DBS =  Arrays.asList(DbEnum.ORCLE, DbEnum.DM);
}
