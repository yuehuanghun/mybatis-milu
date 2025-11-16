package com.yuehuanghun.mybatis.milu.generic.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.apache.ibatis.cache.Cache;

import com.yuehuanghun.mybatis.milu.criteria.Predicate;
import com.yuehuanghun.mybatis.milu.criteria.PredicateImpl;
import com.yuehuanghun.mybatis.milu.criteria.builder.CountSqlTemplateBuilder;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderSql;
import com.yuehuanghun.mybatis.milu.tool.Constants;
import com.yuehuanghun.mybatis.milu.tool.cache.SynchronizedLruCache;

public class GenericCountByCriteriaProviderSql implements GenericProviderSql {

	private final Map<Class<?>, Cache> cache = new ConcurrentHashMap<>();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		Object criteria = ((Map)params).get(Constants.CRITERIA);
		Predicate predicate;
		
		if(Consumer.class.isInstance(criteria)) {
			predicate = new PredicateImpl();			
			((Consumer<Predicate>)criteria).accept(predicate);
		} else {
			predicate = (Predicate) criteria;
		}
		predicate.end();

		Map<String, Object> queryParams = new HashMap<>();
		predicate.renderParams(context, queryParams, 0);
		
		((Map)params).putAll(queryParams);

		Cache buildTemplateCache = cache.computeIfAbsent(context.getMapperType(), (clazz) -> {
			return new SynchronizedLruCache(getMethodName()); // 使用LRU缓存，最多存储1024个缓存数据
		});
		
		String sqlTemplate = (String)buildTemplateCache.getObject(predicate);
		
		if(sqlTemplate == null) {
			sqlTemplate = new CountSqlTemplateBuilder(context, predicate).build();
			buildTemplateCache.putObject(predicate, sqlTemplate);
		}
		
		return sqlTemplate;
	}

	@Override
	public String getMethodName() {
		return "countByCriteria";
	}

}
