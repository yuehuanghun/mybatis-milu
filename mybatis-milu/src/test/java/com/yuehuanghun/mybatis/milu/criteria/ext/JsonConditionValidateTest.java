package com.yuehuanghun.mybatis.milu.criteria.ext;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.yuehuanghun.mybatis.milu.criteria.ext.mysql.conds.MysqlJsonCompare;
import com.yuehuanghun.mybatis.milu.criteria.ext.mysql.conds.MysqlJsonLike;
import com.yuehuanghun.mybatis.milu.criteria.ext.mysql.conds.MysqlJsonLike.LikeMode;
import com.yuehuanghun.mybatis.milu.criteria.ext.mysql.select.MysqlJsonExtract;
import com.yuehuanghun.mybatis.milu.criteria.ext.mysql.select.MysqlJsonExtractStr;
import com.yuehuanghun.mybatis.milu.criteria.ext.postgre.conds.PostgreJsonCompare;
import com.yuehuanghun.mybatis.milu.criteria.ext.postgre.conds.PostgreJsonPathCompare;
import com.yuehuanghun.mybatis.milu.criteria.ext.postgre.select.PostgreJsonExtract;
import com.yuehuanghun.mybatis.milu.criteria.ext.postgre.select.PostgreJsonExtractPath;
import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;

public class JsonConditionValidateTest {

	private static final String INJECTION = "$.name' OR '1'='1";

	@Test
	public void mysqlJsonCompare_rejectsInjection() {
		assertThrows(SqlExpressionBuildingException.class, () -> new MysqlJsonCompare("extAttr", INJECTION, "v", CompareMode.EQUALS));
	}

	@Test
	public void mysqlJsonCompare_acceptsValidPath() {
		new MysqlJsonCompare("extAttr", "$.name", "v", CompareMode.EQUALS);
	}

	@Test
	public void mysqlJsonLike_rejectsInjection() {
		assertThrows(SqlExpressionBuildingException.class, () -> new MysqlJsonLike("extAttr", INJECTION, "v", LikeMode.CONTAINS));
	}

	@Test
	public void mysqlJsonLike_acceptsValidPath() {
		new MysqlJsonLike("extAttr", "$.name", "v", LikeMode.CONTAINS);
	}

	@Test
	public void mysqlJsonExtract_rejectsInjection() {
		assertThrows(SqlExpressionBuildingException.class, () -> MysqlJsonExtract.of("extAttr", INJECTION, null));
	}

	@Test
	public void mysqlJsonExtract_acceptsValidPath() {
		MysqlJsonExtract.of("extAttr", "$.name", null);
	}

	@Test
	public void mysqlJsonExtractStr_rejectsInjection() {
		assertThrows(SqlExpressionBuildingException.class, () -> MysqlJsonExtractStr.of("extAttr", INJECTION, null));
	}

	@Test
	public void mysqlJsonExtractStr_acceptsValidPath() {
		MysqlJsonExtractStr.of("extAttr", "$.name", null);
	}

	@Test
	public void postgreJsonCompare_rejectsInjection() {
		assertThrows(SqlExpressionBuildingException.class, () -> new PostgreJsonCompare("extAttr", "name' OR '1'='1", "v", CompareMode.EQUALS));
	}

	@Test
	public void postgreJsonCompare_acceptsValidKey() {
		new PostgreJsonCompare("extAttr", "name", "v", CompareMode.EQUALS);
	}

	@Test
	public void postgreJsonPathCompare_rejectsInjection() {
		assertThrows(SqlExpressionBuildingException.class, () -> new PostgreJsonPathCompare("extAttr", "{a,b}'; DROP TABLE x--", "v", CompareMode.EQUALS));
	}

	@Test
	public void postgreJsonPathCompare_acceptsValidPath() {
		new PostgreJsonPathCompare("extAttr", "{a,b}", "v", CompareMode.EQUALS);
	}

	@Test
	public void postgreJsonExtract_rejectsInjection() {
		assertThrows(SqlExpressionBuildingException.class, () -> PostgreJsonExtract.of("extAttr", "name' OR '1'='1", null));
	}

	@Test
	public void postgreJsonExtract_acceptsValidKey() {
		PostgreJsonExtract.of("extAttr", "name", null);
	}

	@Test
	public void postgreJsonExtractPath_rejectsInjection() {
		assertThrows(SqlExpressionBuildingException.class, () -> PostgreJsonExtractPath.of("extAttr", "{a,b}'; DROP TABLE x--", null));
	}

	@Test
	public void postgreJsonExtractPath_acceptsValidPath() {
		PostgreJsonExtractPath.of("extAttr", "{a,b}", null);
	}
}
