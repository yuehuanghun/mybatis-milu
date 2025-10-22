package com.yuehuanghun.mybatis.milu.criteria.ext.mysql.select;

import java.util.Objects;
import java.util.Set;

import com.yuehuanghun.mybatis.milu.criteria.Select;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.tool.Assert;
import com.yuehuanghun.mybatis.milu.tool.DefendUtil;
import com.yuehuanghun.mybatis.milu.tool.Segment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MysqlIfNull implements Select {
	private String attrName;
	
	private String defaultAttrName;
	
	private Object defaultValue;
	
	private String alias;

	/**
	 * 构建一个新实例
	 * @param attrName 被判null的实体属性
	 * @param defaultAttrName 当attrName对应字段的值为null时，查询的默认实体属性
	 * @param alias 查询列别名，为Null时则等于attrName的值
	 */
	public static MysqlIfNull of(String attrName, String defaultAttrName, String alias) {
		Assert.notBlank(attrName, "attrName不能为空");
		Assert.notBlank(defaultAttrName, "defaultAttrName不能为空");
		Assert.isTrue(alias == null || DefendUtil.testColumnAlias(alias), "alias值不符合规则");
		MysqlIfNull select = new MysqlIfNull();
		select.attrName = attrName;
		select.defaultAttrName = defaultAttrName;
		select.alias = alias == null ? attrName : alias;
		return select;
	}
	
	/**
	 * 构建一个新实例
	 * @param attrName 被判null的实体属性
	 * @param defaultAttrName 当attrName对应字段的值为null时，查询的默认实体属性
	 * @param alias 查询列别名，为Null时则等于attrName的值
	 */
	public static MysqlIfNull ofValue(String attrName, String defaultValue, String alias) {
		Assert.notBlank(attrName, "attrName不能为空");
		Assert.notNull(defaultValue, "defaultValue不能为空");
		Assert.isTrue(alias == null || DefendUtil.testColumnAlias(alias), "alias值不符合规则");
		MysqlIfNull select = new MysqlIfNull();
		select.attrName = attrName;
		select.defaultValue = defaultValue;
		select.alias = alias == null ? attrName : alias;
		return select;
	}
	
	/**
	 * 构建一个新实例
	 * @param attrName 被判null的实体属性
	 * @param defaultValue 当attrName对应字段的值为null时，查询的默认值
	 * @param alias 查询列别名，为Null时则等于attrName的值
	 */
	public static MysqlIfNull ofValue(String attrName, Number defaultValue, String alias) {
		Assert.notBlank(attrName, "attrName不能为空");
		Assert.notNull(defaultValue, "defaultValue不能为空");
		Assert.isTrue(alias == null || DefendUtil.testColumnAlias(alias), "alias值不符合规则");
		MysqlIfNull select = new MysqlIfNull();
		select.attrName = attrName;
		select.defaultValue = defaultValue;
		select.alias = alias == null ? attrName : alias;
		return select;
	}

	@Override
	public String getExpresion(GenericProviderContext context, Set<String> columns, Set<String> aliases) {
		columns.add(attrName);
		aliases.add(alias);
		if(defaultAttrName != null) {
			columns.add(defaultAttrName);
			return "IFNULL(" + wrapAttrName(attrName) + ", " + wrapAttrName(defaultAttrName) + ")" + Segment.SPACE + SqlBuildingHelper.wrapIdentifier(alias, context.getConfiguration());
		}
		
		if(defaultValue instanceof String) {
			return "IFNULL(" + wrapAttrName(attrName) + ", '" + defaultValue + "')" + Segment.SPACE + SqlBuildingHelper.wrapIdentifier(alias, context.getConfiguration());
		}
		return "IFNULL(" + wrapAttrName(attrName) + ", " + defaultValue + ")" + Segment.SPACE + SqlBuildingHelper.wrapIdentifier(alias, context.getConfiguration());
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 31 * result + attrName.hashCode();
		if (defaultAttrName != null) result = 31 * result + defaultAttrName.hashCode();
		if (defaultValue != null) result = 31 * result + defaultValue.hashCode();
		result = 31 * result + alias.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object that) {
		if(!this.getClass().isInstance(that)) {
			return false;
		}
		return Objects.equals(this.attrName, ((MysqlIfNull)that).attrName)
				&& Objects.equals(this.defaultAttrName, ((MysqlIfNull)that).defaultAttrName)
				&& Objects.equals(this.defaultValue, ((MysqlIfNull)that).defaultValue)
				&& Objects.equals(this.alias, ((MysqlIfNull)that).alias);
	}
	
}
