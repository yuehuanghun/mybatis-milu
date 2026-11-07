package com.yuehuanghun.mybatis.milu.tool;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * sql防御工具
 */
public class DefendUtil {

	private static final Predicate<String> DEFAULT_COLUMN_ALIAS = Pattern.compile("^[a-zA-Z0-9_]+$").asPredicate();

	private static final Predicate<String> DEFAULT_UNSAFE_FUNC_COL_EXP = Pattern.compile("^.*((truncate\\s+.+)|(delete\\s+from)|(drop\\s+table)|(--)|(union\\s+select)|(;)).*$").asPredicate();

	private static final Predicate<String> DEFAULT_ORDER_COLUMN = Pattern.compile("^[a-zA-Z0-9_.]+$").asPredicate();

	private static final Predicate<String> DEFAULT_MYSQL_JSON_PATH = Pattern.compile("^\\$(\\*\\*|\\.\\*|\\.[\\w\\-]+|\\[\\*\\]|\\[\\d+\\])*$").asPredicate();

	private static final Predicate<String> DEFAULT_POSTGRE_JSON_KEY = Pattern.compile("^\\w[\\w\\-]*$").asPredicate();

	private static final Predicate<String> DEFAULT_POSTGRE_JSON_PATH = Pattern.compile("^\\{[\\w\\-]+(,[\\w\\-]+)*\\}$").asPredicate();

	private static Predicate<String> columnAliasDefend = DEFAULT_COLUMN_ALIAS;

	private static Predicate<String> unsafeFuncColExp = DEFAULT_UNSAFE_FUNC_COL_EXP;

	private static Predicate<String> orderColumnDefend = DEFAULT_ORDER_COLUMN;

	private static Predicate<String> mysqlJsonPathDefend = DEFAULT_MYSQL_JSON_PATH;

	private static Predicate<String> postgreJsonKeyDefend = DEFAULT_POSTGRE_JSON_KEY;

	private static Predicate<String> postgreJsonPathDefend = DEFAULT_POSTGRE_JSON_PATH;

	private static Predicate<String> predicateOf(Predicate<String> predicate, Predicate<String> defaultPredicate) {
		return predicate == null ? defaultPredicate : predicate;
	}

	/**
	 * 设置列别名白名单校验规则
	 * @param predicate 自定义校验规则；传 {@code null} 恢复默认规则 {@code ^[a-zA-Z0-9_]+$}
	 */
	public static void setColumnAliasDefendPredicate(Predicate<String> predicate) {
		DefendUtil.columnAliasDefend = predicateOf(predicate, DEFAULT_COLUMN_ALIAS);
	}

	/**
	 * 设置函数列表达式黑名单校验规则
	 * @param predicate 自定义校验规则；传 {@code null} 恢复默认黑名单（truncate / delete from / drop table / ; / -- / union select）
	 */
	public static void setUnsafeFuncColExpPredicate(Predicate<String> predicate) {
		DefendUtil.unsafeFuncColExp = predicateOf(predicate, DEFAULT_UNSAFE_FUNC_COL_EXP);
	}

	/**
	 * 设置排序属性白名单校验规则（PageHelper排序）
	 * @param predicate 自定义校验规则；传 {@code null} 恢复默认规则 {@code ^[a-zA-Z0-9_.]+$}
	 */
	public static void setOrderColumnDefendPredicate(Predicate<String> predicate) {
		DefendUtil.orderColumnDefend = predicateOf(predicate, DEFAULT_ORDER_COLUMN);
	}

	/**
	 * 设置MySQL JSON路径白名单校验规则
	 * @param predicate 自定义校验规则；传 {@code null} 恢复默认规则 {@code ^\$(\*\*|\.\*|\.[\w\-]+|\[\*\]|\[\d+\])*$}
	 */
	public static void setMysqlJsonPathDefendPredicate(Predicate<String> predicate) {
		DefendUtil.mysqlJsonPathDefend = predicateOf(predicate, DEFAULT_MYSQL_JSON_PATH);
	}

	/**
	 * 设置PostgreSQL JSON键白名单校验规则（->> 操作符右侧的单层键）
	 * @param predicate 自定义校验规则；传 {@code null} 恢复默认规则 {@code ^\w[\w\-]*$}
	 */
	public static void setPostgreJsonKeyDefendPredicate(Predicate<String> predicate) {
		DefendUtil.postgreJsonKeyDefend = predicateOf(predicate, DEFAULT_POSTGRE_JSON_KEY);
	}

	/**
	 * 设置PostgreSQL JSON路径白名单校验规则（#>> 操作符右侧的 {a,b} 形式）
	 * @param predicate 自定义校验规则；传 {@code null} 恢复默认规则 {@code ^\{[\w\-]+(,[\w\-]+)*\}$}
	 */
	public static void setPostgreJsonPathDefendPredicate(Predicate<String> predicate) {
		DefendUtil.postgreJsonPathDefend = predicateOf(predicate, DEFAULT_POSTGRE_JSON_PATH);
	}

	/**
	 * 测试列别名是否符合规则
	 * @param alias 列别名
	 * @return true 符合，false 不符合
	 */
	public static boolean testColumnAlias(String alias) {
		return columnAliasDefend.test(alias);
	}

	/**
	 * 测试标识符是否符合规则，空白值放行（由调用方决定是否必填）
	 * @param value 标识符
	 * @param paramName 参数名，用于异常信息
	 */
	public static void checkIdentifier(String value, String paramName) {
		Assert.isTrue(StringUtils.isBlank(value) || testColumnAlias(value), String.format("%s值不符合规则：%s", paramName, value));
	}

	/**
	 * 测试排序属性名是否符合规则
	 * @param orderColumn 排序属性名或列名（允许表别名前缀，如 t1.user_name）
	 * @return true 符合，false 不符合
	 */
	public static boolean testOrderColumn(String orderColumn) {
		return orderColumnDefend.test(orderColumn);
	}

	/**
	 * 测试MySQL JSON路径是否符合规则，如 $.name、$.a.b[0]、$[*].a、$**.a
	 * @param jsonPath JSON路径
	 * @return true 符合，false 不符合
	 */
	public static boolean testMysqlJsonPath(String jsonPath) {
		return mysqlJsonPathDefend.test(jsonPath);
	}

	/**
	 * 测试PostgreSQL JSON键是否符合规则（->> 操作符右侧的单层键），如 userName
	 * @param jsonKey JSON键
	 * @return true 符合，false 不符合
	 */
	public static boolean testPostgreJsonKey(String jsonKey) {
		return postgreJsonKeyDefend.test(jsonKey);
	}

	/**
	 * 测试PostgreSQL JSON路径是否符合规则（#>> 操作符右侧的 {a,b} 形式）
	 * @param jsonPath JSON路径
	 * @return true 符合，false 不符合
	 */
	public static boolean testPostgreJsonPath(String jsonPath) {
		return postgreJsonPathDefend.test(jsonPath);
	}

	/**
	 * 测试函数列表达式是否安全
	 * @param expression 表达式
	 * @return true 安全，false 不安全
	 */
	public static boolean testFuncColumnExp(String expression) {
		if(expression == null) {
			return true;
		}
		return !unsafeFuncColExp.test(expression);
	}

	/**
	 * 单引号双写转义，用于拼接进SQL字符串字面量的值
	 * @param value 原值
	 * @return 转义后的值
	 */
	public static String escapeSingleQuote(String value) {
		if(value == null) {
			return null;
		}
		return value.replace("'", "''");
	}
}
