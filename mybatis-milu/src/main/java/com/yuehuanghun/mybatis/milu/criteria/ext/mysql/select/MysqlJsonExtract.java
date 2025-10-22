package com.yuehuanghun.mybatis.milu.criteria.ext.mysql.select;

import java.util.Objects;
import java.util.Set;

import com.yuehuanghun.mybatis.milu.criteria.Select;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.tool.Assert;
import com.yuehuanghun.mybatis.milu.tool.DefendUtil;
import com.yuehuanghun.mybatis.milu.tool.Segment;

/**
 * Mysql JSON 数据提取<br>
 * 获取的是JSON对象，如果是对象是字符串，则返回值包括双引号，例如，$.name 值为"张三"<br>
 * 如果不需要双引号的，使用{@link MysqlJsonExtractStr}
 */
public class MysqlJsonExtract implements Select {

	private String attrName;
	
	private String jsonKey;
	
	private String alias;
	
	/**
	 * 构建一个新实例
	 * @param attrName JSON字段属性
	 * @param jsonKey 定位数据的json键路径，如$.name
	 * @param alias 查询列别名，为Null时则等于attrName的值
	 */
	public static MysqlJsonExtract of(String attrName, String jsonKey, String alias) {
		Assert.notBlank(attrName, "attrName不能为空");
		Assert.notBlank(jsonKey, "jsonKey不能为空");
		Assert.isTrue(alias == null || DefendUtil.testColumnAlias(alias), "alias值不符合规则");
		MysqlJsonExtract select = new MysqlJsonExtract();
		select.attrName = attrName;
		select.jsonKey = jsonKey;
		select.alias = alias == null ? attrName : alias;
		return select;
	}

	// 使用JSON_EXTRACT函数
	@Override
	public String getExpresion(GenericProviderContext context, Set<String> attrNames, Set<String> aliases) {
		attrNames.add(attrName);
		aliases.add(alias);
		return "JSON_EXTRACT(" + wrapAttrName(attrName) + Segment.COMMA_B + "'" + jsonKey + "') " + SqlBuildingHelper.wrapIdentifier(alias, context.getConfiguration());
	}


	@Override
	public int hashCode() {
		int result = 17;

		result = 31 * result + attrName.hashCode();
		result = 31 * result + jsonKey.hashCode();
		result = 31 * result + alias.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object that) {
		if(!this.getClass().isInstance(that)) {
			return false;
		}
		return Objects.equals(this.attrName, ((MysqlJsonExtract)that).attrName)
				&& Objects.equals(this.jsonKey, ((MysqlJsonExtract)that).jsonKey)
				&& Objects.equals(this.alias, ((MysqlJsonExtract)that).alias);
	}
}
