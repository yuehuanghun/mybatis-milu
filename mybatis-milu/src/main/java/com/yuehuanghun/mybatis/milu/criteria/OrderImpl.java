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
package com.yuehuanghun.mybatis.milu.criteria;

import java.util.Map;
import java.util.Set;

import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.data.Sort.Direction;
import com.yuehuanghun.mybatis.milu.tool.Segment;

import lombok.Getter;

public class OrderImpl implements Order {

	@Getter
	private Direction direction = null;
	
	@Getter
	private String attributeName;
	
	private OrderImpl(String attrName, Direction direction) {
		this.attributeName = attrName;
		this.direction = direction;
	}
	
	public static OrderImpl order(String attrName, Direction direction) {
		return new OrderImpl(attrName, direction);
	}
	
	public static OrderImpl order(String attrName) {
		return new OrderImpl(attrName, null);
	}

	@Override
	public int render(MiluConfiguration configuration, StringBuilder expressionBuilder, Map<String, Object> params, Set<String> columns, int paramIndex) {
		expressionBuilder.append(Segment.SPACE).append(columnHolder(attributeName));
		columns.add(attributeName);
		if(direction != null) {
			expressionBuilder.append(Segment.SPACE).append(direction.name());
		}
		return paramIndex;
	}
}
