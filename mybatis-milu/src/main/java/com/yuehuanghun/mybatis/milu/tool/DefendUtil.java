package com.yuehuanghun.mybatis.milu.tool;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * sql防御工具
 */
public class DefendUtil {

	private static Predicate<String> columnAliasDefend = Pattern.compile("^[a-zA-Z0-9_]+$").asPredicate();
	
	private static Predicate<String> unsafeFuncColExp = Pattern.compile("^.*((truncate\\s+.+)|(delete\\s+from)|(drop\\s+table)).*$").asPredicate();
	
	public static void setColumnAliasDefendPredicate(Predicate<String> predicate) {
		DefendUtil.columnAliasDefend = predicate;
	}
	
	public static void setUnsafeFuncColExpPredicate(Predicate<String> predicate) {
		DefendUtil.unsafeFuncColExp = predicate;
	}
	
	/**
	 * 测试列别名是否符合规则
	 * @param alias 列别名
	 * @return true 符合，false 不符合
	 */
	public static boolean testColumnAlias(String alias) {
		if(columnAliasDefend == null || columnAliasDefend == null) {
			return true;
		}
		return columnAliasDefend.test(alias);
	}
	
	/**
	 * 测试函数列表达式是否安全
	 * @param expression
	 * @return true 安全，false 不安全
	 */
	public static boolean testFuncColumnExp(String expression) {
		if(expression == null || unsafeFuncColExp == null) {
			return true;
		}
		
		return !unsafeFuncColExp.test(expression);
	}
}
