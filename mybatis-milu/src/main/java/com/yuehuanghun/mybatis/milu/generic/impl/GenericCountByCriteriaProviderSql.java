package com.yuehuanghun.mybatis.milu.generic.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.ibatis.javassist.scopedpool.SoftValueHashMap;

import com.yuehuanghun.mybatis.milu.criteria.CriteriaSqlBuilder;
import com.yuehuanghun.mybatis.milu.criteria.CriteriaSqlBuilder.CriteriaType;
import com.yuehuanghun.mybatis.milu.criteria.Expression;
import com.yuehuanghun.mybatis.milu.criteria.Predicate;
import com.yuehuanghun.mybatis.milu.criteria.PredicateImpl;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderSql;
import com.yuehuanghun.mybatis.milu.tool.Constants;

public class GenericCountByCriteriaProviderSql implements GenericProviderSql {

	private final Map<Expression, String> cache = new SoftValueHashMap<>();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		Object criteria = ((Map)params).get(Constants.CRITERIA);
		Expression expression;
		
		if(Consumer.class.isInstance(criteria)) {
			Predicate predicate = new PredicateImpl();			
			((Consumer<Predicate>)criteria).accept(predicate);
			expression = predicate;
		} else {
			expression = (Expression)criteria;
		}

		Map<String, Object> queryParams = new HashMap<>();
		expression.renderParams(queryParams, 0);
		
		((Map)params).putAll(queryParams);

		String sqlExpression = cache.computeIfAbsent(expression, (key) -> {
			StringBuilder expressionBuilder = new StringBuilder(256);
			Set<String> columns = new HashSet<>();
			expression.renderSqlTemplate(context.getConfiguration(), expressionBuilder, columns, 0);
			CriteriaSqlBuilder builder = CriteriaSqlBuilder.instance(context.getEntity().getJavaType(), expressionBuilder.toString(), columns, context.getConfiguration()).setCriteriaType(CriteriaType.COUNT);
			return builder.build();
		});
		
		return sqlExpression;
	}

	@Override
	public String getMethodName() {
		return "countByCriteria";
	}

}
