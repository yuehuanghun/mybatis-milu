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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.tool.Segment;

/**
 * 分组表达式实现
 * @author yuehuanghun
 *
 */
public class GroupImpl extends HashSet<String> implements Group {
	private static final long serialVersionUID = 1L;

	@Override
	public int render(MiluConfiguration configuration, StringBuilder expressionBuilder, Map<String, Object> params,
			Set<String> columns, int paramIndex) {
		if(this.isEmpty()) {
			return paramIndex;
		}
		
		columns.addAll(this);
		
		expressionBuilder.append(Segment.GROUP_BY_B);
		this.forEach(attrName -> expressionBuilder.append(columnHolder(attrName)).append(Segment.COMMA_B));
		
		expressionBuilder.setLength(expressionBuilder.length() - Segment.COMMA_B.length()); //去掉最后逗号
		
		return paramIndex;
	}

}
