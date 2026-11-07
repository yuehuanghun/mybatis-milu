package com.yuehuanghun.mybatis.milu.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;

public class SqlBuildingHelperOrderTest {

	@Test
	public void acceptsPropertyOnly() {
		assertArrayEquals(new String[] {"userName", ""}, SqlBuildingHelper.validateOrderSegment("userName"));
	}

	@Test
	public void acceptsPropertyWithDirectionAndNormalizesCase() {
		assertArrayEquals(new String[] {"userName", "DESC"}, SqlBuildingHelper.validateOrderSegment("userName desc"));
		assertArrayEquals(new String[] {"userName", "ASC"}, SqlBuildingHelper.validateOrderSegment("  userName   Asc  "));
	}

	@Test
	public void rejectsSqlInjectionInDirection() {
		assertThrows(SqlExpressionBuildingException.class, () -> SqlBuildingHelper.validateOrderSegment("userName desc; drop table user"));
	}

	@Test
	public void rejectsUnknownDirection() {
		assertThrows(SqlExpressionBuildingException.class, () -> SqlBuildingHelper.validateOrderSegment("userName limit 1"));
	}

	@Test
	public void rejectsTooManyTokens() {
		assertThrows(SqlExpressionBuildingException.class, () -> SqlBuildingHelper.validateOrderSegment("userName desc extra"));
	}
}
