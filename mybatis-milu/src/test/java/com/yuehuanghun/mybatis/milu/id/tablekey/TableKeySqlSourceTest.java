package com.yuehuanghun.mybatis.milu.id.tablekey;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Proxy;

import javax.persistence.TableGenerator;

import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Test;

import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;

public class TableKeySqlSourceTest {

	private TableGenerator tableGenerator(final String table, final String schema, final String pkColumnName,
			final String pkColumnValue, final String valueColumnName) {
		return (TableGenerator) Proxy.newProxyInstance(TableGenerator.class.getClassLoader(),
				new Class<?>[] { TableGenerator.class }, (proxy, method, args) -> {
					switch (method.getName()) {
					case "table":
						return table;
					case "schema":
						return schema;
					case "pkColumnName":
						return pkColumnName;
					case "pkColumnValue":
						return pkColumnValue;
					case "valueColumnName":
						return valueColumnName;
					case "annotationType":
						return TableGenerator.class;
					default:
						return method.getDefaultValue();
					}
				});
	}

	@Test
	public void selectSource_rejectsInjectionInValueColumnName() {
		TableGenerator tg = tableGenerator("id_gen", "", "pk", "user_pk", "value`; drop table id_gen--");
		assertThrows(SqlExpressionBuildingException.class, () -> new TableKeySelectSqlSource(new Configuration(), tg));
	}

	@Test
	public void selectSource_rejectsInjectionInSchema() {
		TableGenerator tg = tableGenerator("id_gen", "sec`; drop table x--", "pk", "user_pk", "value");
		assertThrows(SqlExpressionBuildingException.class, () -> new TableKeySelectSqlSource(new Configuration(), tg));
	}

	@Test
	public void updateSource_rejectsInjectionInSchema() {
		TableGenerator tg = tableGenerator("id_gen", "sec`; drop table x--", "pk", "user_pk", "value");
		assertThrows(SqlExpressionBuildingException.class, () -> new TableKeyUpdateSqlSource(new Configuration(), tg));
	}

	@Test
	public void escapesQuoteInPkColumnValue() {
		TableGenerator tg = tableGenerator("id_gen", "", "pk", "x' OR '1'='1", "value");
		TableKeySelectSqlSource source = new TableKeySelectSqlSource(new Configuration(), tg);
		assertTrue(source.getBoundSql(null).getSql().contains("x'' OR ''1''=''1"));
	}

	@Test
	public void acceptsValidValues() {
		TableGenerator tg = tableGenerator("id_gen", "sec1", "pk", "user_pk", "value");
		new TableKeySelectSqlSource(new Configuration(), tg);
		new TableKeyUpdateSqlSource(new Configuration(), tg);
	}
}
