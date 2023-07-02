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

import java.util.Map;

import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.tool.Assert;
import com.yuehuanghun.mybatis.milu.tool.Constants;

import lombok.Getter;

public class LimitOffset implements Limit {

	@Getter
	private final int offset;
	@Getter
	private final int size;
	@Getter
	private final boolean count;
	
	public LimitOffset(int offset, int size, boolean count) {
		Assert.isTrue(offset >= 0, "查询行起始偏移量offset不能小于0");
		Assert.isTrue(size >= 1, "查询行数size不能小于1");
		this.offset = offset;
		this.size = size;
		this.count = count;
	}

	public LimitOffset(int offset, int size) {
		this(offset, size, true);
	}
	
	@Override
	public int renderParams(GenericProviderContext context, Map<String, Object> params, int paramIndex) {
		params.put(Constants.PAGE_KEY, this);
		return paramIndex;
	}
}
