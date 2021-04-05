package com.yuehuanghun.mybatis.milu.generic.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.yuehuanghun.mybatis.milu.criteria.CriteriaSqlBuilder;
import com.yuehuanghun.mybatis.milu.criteria.CriteriaSqlBuilder.CriteriaType;
import com.yuehuanghun.mybatis.milu.criteria.Expression;
import com.yuehuanghun.mybatis.milu.criteria.Predicate;
import com.yuehuanghun.mybatis.milu.criteria.PredicateImpl;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderSql;

public class GenericCountByCriteriaProviderSql implements GenericProviderSql {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		Object criteria = ((Map)params).get("criteria");
		Expression expression;
		
		if(Consumer.class.isInstance(criteria)) {
			Predicate predicate = new PredicateImpl();			
			((Consumer<Predicate>)criteria).accept(predicate);
			expression = predicate;
		} else {
			expression = (Expression)criteria;
		}
		
		StringBuilder expressionBuilder = new StringBuilder(256);
		Map<String, Object> queryParams = new HashMap<>();
		Set<String> queryProperties = new HashSet<>();
		
		expression.render(context.getConfiguration(), expressionBuilder, queryParams, queryProperties, 0);
		
		((Map)params).putAll(queryParams);
		
		CriteriaSqlBuilder builder = CriteriaSqlBuilder.instance(context.getEntity().getJavaType(), expressionBuilder.toString(), queryProperties, context.getConfiguration()).setCriteriaType(CriteriaType.COUNT);
		String sqlExpression = builder.build();
		
		return sqlExpression;
	}

	@Override
	public String getMethodName() {
		return "countByCriteria";
	}

}
