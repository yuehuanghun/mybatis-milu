package com.yuehuanghun.mybatis.milu.criteria.ext;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.yuehuanghun.mybatis.milu.criteria.ext.mysql.conds.MysqlJsonCompare;
import com.yuehuanghun.mybatis.milu.criteria.ext.mysql.conds.MysqlJsonLike;
import com.yuehuanghun.mybatis.milu.criteria.ext.mysql.conds.MysqlJsonLike.LikeMode;
import com.yuehuanghun.mybatis.milu.criteria.ext.mysql.select.MysqlJsonExtract;
import com.yuehuanghun.mybatis.milu.criteria.ext.mysql.select.MysqlJsonExtractStr;
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
}
