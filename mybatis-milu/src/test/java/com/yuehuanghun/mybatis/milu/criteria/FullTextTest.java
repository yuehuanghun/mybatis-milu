package com.yuehuanghun.mybatis.milu.criteria;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;

public class FullTextTest {

	@Test
	public void rejectsIllegalAttrName() {
		assertThrows(SqlExpressionBuildingException.class, () -> new FullText(Arrays.asList("name; drop table user"), "kw"));
	}

	@Test
	public void rejectsBlankAttrName() {
		assertThrows(SqlExpressionBuildingException.class, () -> new FullText(Arrays.asList("name", ""), "kw"));
	}

	@Test
	public void acceptsValidAttrNames() {
		assertNotNull(new FullText(Arrays.asList("userName", "user_name"), "kw"));
	}

	@Test
	public void keywordExpressionIsParameterValueAndAcceptsAnything() {
		assertDoesNotThrow(() -> new FullText(Arrays.asList("name"), "x' OR '1'='1"));
	}
}
