/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yuehuanghun.mybatis.milu.criteria;

import java.util.Objects;
import java.util.Set;

import javax.persistence.LockModeType;

import com.yuehuanghun.mybatis.milu.MiluConfiguration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 执行的数据库锁表达式
 * @author yuehuanghun
 *
 */
@AllArgsConstructor
public class Lock implements Expression {
	@Getter
	@Setter
	private LockModeType lockModeType;

	@Override
	public int renderSqlTemplate(MiluConfiguration configuration, StringBuilder expressionBuilder, Set<String> columns,
			int paramIndex) {
		if(lockModeType != null && LockModeType.NONE != lockModeType) {
			String lockSql = configuration.getDialect().getLockSql(expressionBuilder.toString(), lockModeType);
			expressionBuilder.setLength(0);
			expressionBuilder.append(lockSql);
		}
		return paramIndex;
	}

	@Override
	public int hashCode() {
		return lockModeType.hashCode();
	}

	@Override
	public boolean equals(Object that) {
		if(!this.getClass().isInstance(that)) {
			return false;
		}
		return Objects.equals(lockModeType, ((Lock)that).getLockModeType());
	}
}
