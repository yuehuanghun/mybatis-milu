package com.yuehuanghun.mybatis.milu.criteria;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.yuehuanghun.mybatis.milu.data.Part.Type;
import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.LogicDeleteAttribute;
import com.yuehuanghun.mybatis.milu.tool.Segment;
import com.yuehuanghun.mybatis.milu.tool.logicdel.LogicDeleteProvider;

public class Deleted implements Condition {

	private final boolean deleted;
	
	public Deleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public int renderSqlTemplate(GenericProviderContext context, StringBuilder expressionBuilder, Set<String> columns,
			int paramIndex) {
		List<LogicDeleteAttribute> mainLogicDeleteAttrs = context.getEntity().getLogicDeleteAttributes().stream().filter(LogicDeleteAttribute::isMain).collect(Collectors.toList());
		if(mainLogicDeleteAttrs.isEmpty()) {
			throw new SqlExpressionBuildingException("缺少主逻辑删除属性");
		}
		
		String express = context.getConfiguration().getDialect().getPartTypeExpression(Type.SIMPLE_PROPERTY);
		
		String partTypeExpression = mainLogicDeleteAttrs.stream().map(attr -> {
			columns.add(attr.getName());
			String key = Segment.HASH_LEFT_BRACE +  attr.getName() + Segment.UNDER_LINE + deleted + Segment.RIGHT_BRACE;
			return Segment.DOLLAR + attr.getName() + Segment.DOLLAR + String.format(express, key);
		}).collect(Collectors.joining(Segment.AND_B));
		
		expressionBuilder.append(partTypeExpression);

		return paramIndex;
	}

	@Override
	public int renderParams(GenericProviderContext context, Map<String, Object> params, int paramIndex) {
		context.getEntity().getLogicDeleteAttributes().stream().filter(LogicDeleteAttribute::isMain).forEach(attr -> {
			String key = attr.getName() + Segment.UNDER_LINE + deleted;
			if(!params.containsKey(key)) {
				LogicDeleteProvider.Context logicContext = new LogicDeleteProvider.Context(attr.getEntityClass(), attr.getJavaType(), attr.getName());
				params.put(key, deleted ? attr.getProvider().value(logicContext) : attr.getProvider().resumeValue(logicContext));
			}
		});
		return paramIndex;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Deleted;
	}
	
}
