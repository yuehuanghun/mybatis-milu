package com.yuehuanghun.mybatis.milu.generic.impl;

import java.util.Map;
import java.util.function.Consumer;

import com.yuehuanghun.mybatis.milu.criteria.LambdaPredicate;
import com.yuehuanghun.mybatis.milu.criteria.LambdaPredicateImpl;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.tool.Constants;

public class GenericCountByLambdaCriteriaProviderSql extends GenericCountByCriteriaProviderSql {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String provideSql(GenericProviderContext context, Object params) {
		Object criteria = ((Map)params).get(Constants.CRITERIA);
		
		LambdaPredicateImpl<?> predicate = new LambdaPredicateImpl<>();
		((Consumer<LambdaPredicate>)criteria).accept(predicate);
		
		((Map)params).put(Constants.CRITERIA, predicate.getDelegate()); //覆盖
		
		return super.provideSql(context, params);
	}

	@Override
	public String getMethodName() {
		return "countByLambdaCriteria";
	}

}
