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
package com.yuehuanghun.mybatis.milu.metamodel.ref;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class MappedReference implements Reference {
	
	private String inverseTableName;
	
	private String attributeName;
	
	private List<JoinCondition> joinConditionList = new ArrayList<>(3);

	public MappedReference(String attributeName, String inverseTableName) {
		this.inverseTableName = inverseTableName;
		this.attributeName = attributeName;
	}
	
	public MappedReference(String attributeName, String inverseTableName, JoinCondition condition) {
		this(attributeName, inverseTableName);
		joinConditionList.add(condition);
	}
	
	public void addJoinCondition(JoinCondition condition) {
		joinConditionList.add(condition);
	}
}
