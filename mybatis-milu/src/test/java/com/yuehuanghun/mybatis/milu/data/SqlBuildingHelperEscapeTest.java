package com.yuehuanghun.mybatis.milu.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class SqlBuildingHelperEscapeTest {

	@Test
	public void escapesBacktick() {
		assertEquals("a``b", SqlBuildingHelper.escapeIdentifier("a`b", "`"));
	}

	@Test
	public void escapesDoubleQuote() {
		assertEquals("a\"\"b", SqlBuildingHelper.escapeIdentifier("a\"b", "\""));
	}

	@Test
	public void noQuoteCharsUnchanged() {
		assertEquals("userName", SqlBuildingHelper.escapeIdentifier("userName", "`"));
		assertEquals("t1.user_name", SqlBuildingHelper.escapeIdentifier("t1.user_name", "\""));
	}
}
