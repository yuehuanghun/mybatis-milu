package com.yuehuanghun.mybatis.milu.criteria.ext.mysql.conds;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.yuehuanghun.mybatis.milu.criteria.Condition;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.tool.Assert;
import com.yuehuanghun.mybatis.milu.tool.Segment;

public class MysqlJsonLike implements Condition {

	private final String attrName;
	
	private final String jsonKey;
	
	private final String jsonVal;
	
	private final LikeMode likeMode;

	/**
	 * 
	 * @param attrName json属性
	 * @param jsonKey 定位数据的json键路径，如$.name
	 * @param jsonVal 搜索包含值
	 */
	public MysqlJsonLike(String attrName, String jsonKey, String jsonVal, LikeMode likeMode) {
		Assert.notBlank(attrName, "attrName不能为空");
		Assert.notBlank(jsonKey, "jsonKey不能为空");
		Assert.notBlank(jsonVal, "jsonVal不能为空");
		Assert.notNull(likeMode, "likeMode不能为空");
		
		this.attrName = attrName;
		this.jsonKey = jsonKey;
		this.jsonVal = jsonVal;
		this.likeMode = likeMode;
	}

	@Override
	public int renderSqlTemplate(GenericProviderContext context, StringBuilder expressionBuilder, Set<String> columns,
			int paramIndex) {
		SqlBuildingHelper.getAttribute(context.getConfiguration(), context.getEntity(), attrName, true);
		columns.add(attrName);
		String key = attrName + "_" + paramIndex;
		paramIndex ++;
		expressionBuilder.append(Segment.SPACE).append(Segment.DOLLAR).append(attrName).append(Segment.DOLLAR).append(" -&gt;&gt; ")
		  .append(Segment.SIGLE_QUOT).append(jsonKey).append(Segment.SIGLE_QUOT).append(" LIKE CONCAT(");
		if(likeMode == LikeMode.CONTAINS || likeMode == LikeMode.ENDS_WITH) {
			expressionBuilder.append("'%', ");
		}
		expressionBuilder.append(Segment.HASH_LEFT_BRACE).append(key).append(Segment.RIGHT_BRACE);
		if(likeMode == LikeMode.CONTAINS || likeMode == LikeMode.STARTS_WITH) {
			expressionBuilder.append(", '%'");
		}
		expressionBuilder.append(")");
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
		result = 31 * result + likeMode.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object that) {
		if(!this.getClass().isInstance(that)) {
			return false;
		}
		return Objects.equals(this.attrName, ((MysqlJsonLike)that).attrName)
				&& Objects.equals(this.jsonKey, ((MysqlJsonLike)that).jsonKey)
				&& Objects.equals(this.likeMode, ((MysqlJsonLike)that).likeMode);
	}

	public static enum LikeMode {
		CONTAINS, STARTS_WITH, ENDS_WITH;
	}
}
