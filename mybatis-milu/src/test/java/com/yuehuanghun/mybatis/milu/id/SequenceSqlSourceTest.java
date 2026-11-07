package com.yuehuanghun.mybatis.milu.id;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Proxy;

import javax.persistence.SequenceGenerator;

import org.junit.jupiter.api.Test;

import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;

public class SequenceSqlSourceTest {

	private SequenceGenerator sequenceGenerator(final String sequenceName) {
		return (SequenceGenerator) Proxy.newProxyInstance(SequenceGenerator.class.getClassLoader(),
				new Class<?>[] { SequenceGenerator.class }, (proxy, method, args) -> {
					switch (method.getName()) {
					case "sequenceName":
						return sequenceName;
					case "annotationType":
						return SequenceGenerator.class;
					default:
						return method.getDefaultValue();
					}
				});
	}

	@Test
	public void rejectsInjectionInSequenceName() {
		assertThrows(SqlExpressionBuildingException.class, () -> new SequenceSqlSource(null, sequenceGenerator("seq`; drop table x--")));
	}

	@Test
	public void acceptsValidSequenceName() {
		assertDoesNotThrow(() -> new SequenceSqlSource(null, sequenceGenerator("user_seq")));
	}
}
