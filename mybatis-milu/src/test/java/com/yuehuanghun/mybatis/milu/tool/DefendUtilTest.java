package com.yuehuanghun.mybatis.milu.tool;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class DefendUtilTest {

	@AfterEach
	public void restoreDefaults() {
		DefendUtil.setColumnAliasDefendPredicate(null);
		DefendUtil.setUnsafeFuncColExpPredicate(null);
		DefendUtil.setOrderColumnDefendPredicate(null);
		DefendUtil.setMysqlJsonPathDefendPredicate(null);
		DefendUtil.setPostgreJsonKeyDefendPredicate(null);
		DefendUtil.setPostgreJsonPathDefendPredicate(null);
	}

	// ---------- MySQL JSON 路径 ----------

	@Test
	public void mysqlJsonPath_acceptsValidPaths() {
		assertTrue(DefendUtil.testMysqlJsonPath("$"));
		assertTrue(DefendUtil.testMysqlJsonPath("$.name"));
		assertTrue(DefendUtil.testMysqlJsonPath("$.a.b"));
		assertTrue(DefendUtil.testMysqlJsonPath("$.user_name"));
		assertTrue(DefendUtil.testMysqlJsonPath("$.a[0].b"));
		assertTrue(DefendUtil.testMysqlJsonPath("$[*].a"));
		assertTrue(DefendUtil.testMysqlJsonPath("$**.a"));
		assertTrue(DefendUtil.testMysqlJsonPath("$.*"));
	}

	@Test
	public void mysqlJsonPath_rejectsInjection() {
		assertFalse(DefendUtil.testMysqlJsonPath("$.name' OR '1'='1"));
		assertFalse(DefendUtil.testMysqlJsonPath("$.name'; DROP TABLE user--"));
		assertFalse(DefendUtil.testMysqlJsonPath("$' OR '1'='1"));
		assertFalse(DefendUtil.testMysqlJsonPath("$.name UNION SELECT 1"));
		assertFalse(DefendUtil.testMysqlJsonPath(""));
	}

	// ---------- PostgreSQL JSON 键（->> 右侧单层键）----------

	@Test
	public void postgreJsonKey_acceptsValidKeys() {
		assertTrue(DefendUtil.testPostgreJsonKey("userName"));
		assertTrue(DefendUtil.testPostgreJsonKey("user_name"));
		assertTrue(DefendUtil.testPostgreJsonKey("user-name"));
	}

	@Test
	public void postgreJsonKey_rejectsInjection() {
		assertFalse(DefendUtil.testPostgreJsonKey("userName' OR '1'='1"));
		assertFalse(DefendUtil.testPostgreJsonKey("userName'; DROP TABLE user--"));
		assertFalse(DefendUtil.testPostgreJsonKey("{a,b}"));
		assertFalse(DefendUtil.testPostgreJsonKey(""));
	}

	// ---------- PostgreSQL JSON 路径（#>> 右侧 {a,b}）----------

	@Test
	public void postgreJsonPath_acceptsValidPaths() {
		assertTrue(DefendUtil.testPostgreJsonPath("{a}"));
		assertTrue(DefendUtil.testPostgreJsonPath("{a,b}"));
		assertTrue(DefendUtil.testPostgreJsonPath("{a,b,c}"));
		assertTrue(DefendUtil.testPostgreJsonPath("{user_name,role_id}"));
	}

	@Test
	public void postgreJsonPath_rejectsInjection() {
		assertFalse(DefendUtil.testPostgreJsonPath("{a,b}'; DROP TABLE x--"));
		assertFalse(DefendUtil.testPostgreJsonPath("{a,b}' OR '1'='1"));
		assertFalse(DefendUtil.testPostgreJsonPath("{}"));
		assertFalse(DefendUtil.testPostgreJsonPath("{a,}"));
	}

	// ---------- PageHelper 排序属性 ----------

	@Test
	public void orderColumn_acceptsValidNames() {
		assertTrue(DefendUtil.testOrderColumn("userName"));
		assertTrue(DefendUtil.testOrderColumn("user_name"));
		assertTrue(DefendUtil.testOrderColumn("t1.user_name"));
		assertTrue(DefendUtil.testOrderColumn("user_name2"));
	}

	@Test
	public void orderColumn_rejectsInjection() {
		assertFalse(DefendUtil.testOrderColumn("name; drop table user"));
		assertFalse(DefendUtil.testOrderColumn("(select 1)"));
		assertFalse(DefendUtil.testOrderColumn("name desc--"));
		assertFalse(DefendUtil.testOrderColumn("name union select id"));
	}

	// ---------- 函数列表达式黑名单（含本次增强）----------

	@Test
	public void funcColExp_rejectsDangerousSql() {
		assertFalse(DefendUtil.testFuncColumnExp("truncate table user"));
		assertFalse(DefendUtil.testFuncColumnExp("delete from user"));
		assertFalse(DefendUtil.testFuncColumnExp("drop table user"));
		assertFalse(DefendUtil.testFuncColumnExp("select 1; drop table user"));
		assertFalse(DefendUtil.testFuncColumnExp("a -- comment"));
		assertFalse(DefendUtil.testFuncColumnExp("a union select b"));
	}

	@Test
	public void funcColExp_acceptsNormalExpression() {
		assertTrue(DefendUtil.testFuncColumnExp("IFNULL(${name}, '')"));
		assertTrue(DefendUtil.testFuncColumnExp("DATE_FORMAT(${createTime}, '%Y-%m-%d')"));
	}

	// ---------- 扩展点：放宽与恢复 ----------

	@Test
	public void relaxSwitch_allowsAnything_thenRestoreDefault() {
		DefendUtil.setMysqlJsonPathDefendPredicate(path -> true);
		assertTrue(DefendUtil.testMysqlJsonPath("anything ' OR 1=1"));
		DefendUtil.setMysqlJsonPathDefendPredicate(null);
		assertFalse(DefendUtil.testMysqlJsonPath("anything ' OR 1=1"));
	}

	@Test
	public void relaxSwitch_postgreJsonKey() {
		DefendUtil.setPostgreJsonKeyDefendPredicate(key -> true);
		assertTrue(DefendUtil.testPostgreJsonKey("key with space"));
		DefendUtil.setPostgreJsonKeyDefendPredicate(null);
		assertFalse(DefendUtil.testPostgreJsonKey("key with space"));
	}
}
