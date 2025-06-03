package com.yuehuanghun.mybatis.milu.criteria.ext.mysql.conds;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.yuehuanghun.mybatis.milu.criteria.Condition;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.tool.Assert;
import com.yuehuanghun.mybatis.milu.tool.Segment;

import lombok.Getter;

public class MysqlJsonCompare implements Condition {

	private final String attrName;
	
	private final String jsonKey;
	
	private final Object jsonVal;
	
	private final CompareMode compareMode;

	/**
	 * 
	 * @param attrName json属性
	 * @param jsonKey 定位数据的json键路径，如$.name
	 * @param jsonVal 搜索包含值
	 */
	public MysqlJsonCompare(String attrName, String jsonKey, Object jsonVal, CompareMode compareMode) {
		Assert.notBlank(attrName, "attrName不能为空");
		Assert.notBlank(jsonKey, "jsonKey不能为空");
		Assert.notNull(jsonVal, "jsonVal不能为空");
		Assert.notNull(compareMode, "compareMode不能为空");
		
		this.attrName = attrName;
		this.jsonKey = jsonKey;
		this.jsonVal = jsonVal;
		this.compareMode = compareMode;
	}

	@Override
	public int renderSqlTemplate(GenericProviderContext context, StringBuilder expressionBuilder, Set<String> columns,
			int paramIndex) {
		SqlBuildingHelper.getAttribute(context.getConfiguration(), context.getEntity(), attrName, true);
		columns.add(attrName);
		String key = attrName + "_" + paramIndex;
		paramIndex ++;
		expressionBuilder.append(Segment.SPACE).append(Segment.DOLLAR).append(attrName).append(Segment.DOLLAR).append(" -&gt;&gt; ")
		  .append(Segment.SIGLE_QUOT).append(jsonKey).append(Segment.SIGLE_QUOT)
		  .append(Segment.SPACE).append(compareMode.getExp()).append(Segment.SPACE)
		  .append(Segment.HASH_LEFT_BRACE).append(key).append(Segment.RIGHT_BRACE);
		return paramIndex;
	}

	@Override
	public int renderParams(GenericProviderContext context, Map<String, Object> params, int paramIndex) {
		String key = attrName + "_" + paramIndex;
		params.put(key, jsonVal);
		paramIndex ++;
		return paramIndex;
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 31 * result + attrName.hashCode();
		result = 31 * result + jsonKey.hashCode();
		result = 31 * result + compareMode.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object that) {
		if(!this.getClass().isInstance(that)) {
			return false;
		}
		return Objects.equals(this.attrName, ((MysqlJsonCompare)that).attrName)
				&& Objects.equals(this.jsonKey, ((MysqlJsonCompare)that).jsonKey)
				&& Objects.equals(this.compareMode, ((MysqlJsonCompare)that).compareMode);
	}

	
	public static enum CompareMode {
		EQUALS("="), NOT_EQUALS("!="), GREATER_THAN("&gt;"), LESS_THAN("&lt;");
		
		@Getter
		private String exp;
		
		CompareMode(String exp){
			this.exp = exp;
		}
	}
}
