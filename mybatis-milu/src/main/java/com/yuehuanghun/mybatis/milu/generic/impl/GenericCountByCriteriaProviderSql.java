package com.yuehuanghun.mybatis.milu.generic.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.apache.ibatis.javassist.scopedpool.SoftValueHashMap;

import com.yuehuanghun.mybatis.milu.criteria.Expression;
import com.yuehuanghun.mybatis.milu.criteria.Predicate;
import com.yuehuanghun.mybatis.milu.criteria.PredicateImpl;
import com.yuehuanghun.mybatis.milu.criteria.builder.CountSqlTemplateBuilder;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderSql;
import com.yuehuanghun.mybatis.milu.tool.Constants;

public class GenericCountByCriteriaProviderSql implements GenericProviderSql {

	private final Map<Class<?>, Map<Expression, String>> cache = new SoftValueHashMap<>();
	
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

		Map<String, Object> queryParams = new HashMap<>();
		predicate.renderParams(queryParams, 0);
		
		((Map)params).putAll(queryParams);

		String sqlExpression = cache.computeIfAbsent(context.getMapperType(), (clazz) -> {
			return new ConcurrentHashMap<>();
		}).computeIfAbsent(predicate, (key) -> {
			return new CountSqlTemplateBuilder(context.getEntity(), context.getConfiguration(), predicate).build();
		});
		
		return sqlExpression;
	}

	@Override
	public String getMethodName() {
		return "countByCriteria";
	}

}
