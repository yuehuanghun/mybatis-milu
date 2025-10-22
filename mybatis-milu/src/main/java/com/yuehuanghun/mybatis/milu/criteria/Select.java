package com.yuehuanghun.mybatis.milu.criteria;

import java.util.Set;

import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;

public interface Select {

	/**
	 * 获取查询表达式
	 * @param context 构建上下文
	 * @param attrNames 表达式中涉及的实体属性，涉及的属性添加到这里
	 * @param aliases 表达式中涉及的别名，涉及的别名添加到这里，用于排除查询属性
	 * @return 查询表达式
	 */
	String getExpresion(GenericProviderContext context, Set<String> attrNames, Set<String> aliases);
	
	default String wrapAttrName(String attrName) {
		return SqlBuildingHelper.columnHolder(attrName);
	}
}
