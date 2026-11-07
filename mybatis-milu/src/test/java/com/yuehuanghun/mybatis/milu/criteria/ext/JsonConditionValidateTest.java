package com.yuehuanghun.mybatis.milu.criteria.ext;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.yuehuanghun.mybatis.milu.criteria.ext.mysql.conds.MysqlJsonCompare;
import com.yuehuanghun.mybatis.milu.criteria.ext.mysql.conds.MysqlJsonLike;
import com.yuehuanghun.mybatis.milu.criteria.ext.mysql.conds.MysqlJsonLike.LikeMode;
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
}
