/*
 * Copyright 2020-2023 the original author or authors.
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

import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.tool.Segment;

public class ExistsJoin implements Condition {

	private final String attrName;
	
	private final String refAttrName;

	public ExistsJoin(String attrName, String refAttrName) {
		this.attrName = attrName;
		this.refAttrName = refAttrName;
	}

	@Override
	public int renderSqlTemplate(GenericProviderContext context, StringBuilder expressionBuilder, Set<String> columns,
			int paramIndex) {
		expressionBuilder.append(Segment.DOLLAR).append(attrName).append(Segment.DOLLAR);
		expressionBuilder.append(Segment.EQUALS_B);
		expressionBuilder.append(Segment.AT).append(refAttrName).append(Segment.AT);
		columns.add(attrName);
		return paramIndex;
	}
	
	@Override
	public boolean equals(Object that) {
		if(!this.getClass().isInstance(that)) {
			return false;
		}
		return Objects.equals(this.attrName, ((ExistsJoin)that).attrName) && Objects.equals(this.refAttrName, ((ExistsJoin)that).refAttrName);
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 31 * result + attrName.hashCode();
		result = 31 * result + refAttrName.hashCode();
		return result;
	}
}
