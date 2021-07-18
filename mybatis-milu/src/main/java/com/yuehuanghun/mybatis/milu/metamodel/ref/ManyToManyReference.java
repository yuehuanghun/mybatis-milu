/*
 * Copyright 2020-2021 the original author or authors.
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
package com.yuehuanghun.mybatis.milu.metamodel.ref;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ManyToManyReference extends MappedReference {
	@Getter
	@Setter
	private JoinTable joinTable;

	public ManyToManyReference(String columnName, String mappedBy, String inverseTableName, String inverseColumnName) {
		super(columnName, mappedBy, inverseTableName, inverseColumnName);
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class JoinTable {
		/**
		 * 中间表
		 */
		private String tableName;
		/**
		 * 映射主表的外键列
		 */
		private String columnName;
		/**
		 * 映射关联表的外键列
		 */
		private String inverseColumnName;
	}
}
