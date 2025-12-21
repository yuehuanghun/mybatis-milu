package com.yuehuanghun.mybatis.milu.criteria;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.tool.Assert;
import com.yuehuanghun.mybatis.milu.tool.Segment;

public class FullText implements Condition {
	
	private Collection<String> attrNames;
	
	private String keywordExpression;
	
	private FulltextMode fulltextMode = FulltextMode.NATIVE;
	
	public FullText(Collection<String> attrNames, String keywordExpression) {
		this(attrNames, keywordExpression, null);
	}

	public FullText(Collection<String> attrNames, String keywordExpression, FulltextMode fulltextMode) {
		Assert.notEmpty(attrNames, "attrNames不能为空");
		Assert.notBlank(keywordExpression, "keywordExpression不能为空");
		this.attrNames = attrNames;
		this.keywordExpression = keywordExpression;
		if(fulltextMode != null) {
			this.fulltextMode = fulltextMode;
		}
	}

	@Override
	public int renderSqlTemplate(GenericProviderContext context, StringBuilder expressionBuilder, Set<String> columns,
			int paramIndex) {		
		attrNames.forEach(attrName -> columns.add(attrName));
		
		String key = attrNames.iterator().next() + "_" + paramIndex;
		String param = Segment.HASH_LEFT_BRACE + key + Segment.RIGHT_BRACE;
		
		String searchColumns = attrNames.stream().map(attrName -> Segment.DOLLAR + attrName + Segment.DOLLAR).collect(Collectors.joining(","));
		
		String mode = context.getConfiguration().getDialect().getFullTextModeExpression(fulltextMode);
		
		String fulltextExpression = context.getConfiguration().getDialect().getFulltextExpression(fulltextMode);
		
		fulltextExpression = replaceVar(fulltextExpression, "columns", searchColumns);
		fulltextExpression = replaceVar(fulltextExpression, "keyword", param);
		fulltextExpression = replaceVar(fulltextExpression, "mode", mode);
		
		expressionBuilder.append(Segment.SPACE).append(fulltextExpression);
		
		paramIndex++;
		return paramIndex;
	}
	
	private String replaceVar(String fulltextExpression, String varName, String varValue) {
		return fulltextExpression.replace("${" + varName + "}", varValue);
	}

	@Override
	public int renderParams(GenericProviderContext context, Map<String, Object> params, int paramIndex) {
		String key = attrNames.iterator().next() + "_" + paramIndex;
		params.put(key, keywordExpression);
		paramIndex++;
		return paramIndex;
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 31 * result + attrNames.hashCode();
		result = 31 * result + keywordExpression.hashCode();
		result = 31 * result + fulltextMode.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object that) {
		if (!this.getClass().isInstance(that)) {
			return false;
		}
		return Objects.equals(this.attrNames, ((FullText) that).attrNames)
				&& Objects.equals(this.keywordExpression, ((FullText) that).keywordExpression)
				&& Objects.equals(this.fulltextMode, ((FullText) that).fulltextMode);
	}

}
