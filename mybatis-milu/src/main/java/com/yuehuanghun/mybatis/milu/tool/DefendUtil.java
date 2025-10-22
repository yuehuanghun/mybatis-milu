package com.yuehuanghun.mybatis.milu.tool;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * sql防御工具
 */
public class DefendUtil {

	private static Predicate<String> columnAliasDefend = Pattern.compile("^[a-zA-Z0-9_]+$").asPredicate();
	
	public static void setColumnAliasDefendPredicate(Predicate<String> columnAliasDefend) {
		DefendUtil.columnAliasDefend = columnAliasDefend;
	}
	
	/**
	 * 测试列别名是否符合规则
	 * @param alias 列别名
	 * @return true 符合，false 不符合
	 */
	public static boolean testColumnAlias(String alias) {
		if(columnAliasDefend == null) {
			return true;
		}
		return columnAliasDefend.test(alias);
	}
}
