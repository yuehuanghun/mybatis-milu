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

import java.util.LinkedHashSet;
import java.util.Set;

import com.yuehuanghun.mybatis.milu.data.Sort.NullHandling;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.tool.Segment;

public class SortImpl extends LinkedHashSet<com.yuehuanghun.mybatis.milu.criteria.Sort.Order> implements Sort {

	private static final long serialVersionUID = 1L;

	@Override
	public int renderSqlTemplate(GenericProviderContext context, StringBuilder expressionBuilder, Set<String> columns,
			int paramIndex) {
		if(this.isEmpty()) {
			return paramIndex;
		}
		
		expressionBuilder.append(Segment.ORDER_BY);
		
		this.forEach(order -> {
			columns.add(order.getAttributeName());
			
			if(order.getNullHandling() == NullHandling.NATIVE) {
				expressionBuilder.append(columnHolder(order.getAttributeName()));
				if(order.getDirection() != null) {
					expressionBuilder.append(Segment.SPACE).append(order.getDirection().name());
				}
			} else {
				String column = columnHolder(order.getAttributeName());
				String orderExpression = column;
				if(order.getDirection() != null) {
					orderExpression += Segment.SPACE + order.getDirection().name();
				}
				orderExpression = context.getConfiguration().getDialect().nullValueSort(orderExpression, column, order.getNullHandling());
				expressionBuilder.append(orderExpression);
			}
			
			expressionBuilder.append(Segment.COMMA_B);
		});
		
		expressionBuilder.setLength(expressionBuilder.length() - Segment.COMMA_B.length()); //去掉最后逗号
		
		return paramIndex;
	}

}
