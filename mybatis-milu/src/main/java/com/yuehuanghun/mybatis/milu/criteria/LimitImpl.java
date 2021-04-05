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
import com.yuehuanghun.mybatis.milu.pagehelper.Pageable;
import com.yuehuanghun.mybatis.milu.tool.Assert;
import com.yuehuanghun.mybatis.milu.tool.Constants;

import lombok.Getter;

/**
 * 分页设定，最终使用PageHelper进行分页操作
 * @author yuehuanghun
 *
 */
@Getter
public class LimitImpl implements Limit, Pageable {
	
	private final int pageNum;
	
	private final int pageSize;
	
	private final boolean count;
	
	public LimitImpl(int pageSize) {
		this(1, pageSize, true);
	}
	
	public LimitImpl(int pageSize, boolean count) {
		this(1, pageSize, count);
	}
	
	public LimitImpl(int pageNum, int pageSize, boolean count) {
		Assert.isTrue(pageNum >= 1, "当前页码pageNum不能小于1");
		Assert.isTrue(pageSize >= 1, "每页行数pageSize不能小于1");
		this.pageNum = pageNum;
		this.pageSize = pageSize;
		this.count = count;
	}

	@Override
	public int render(MiluConfiguration configuration, StringBuilder expressionBuilder, Map<String, Object> params, Set<String> columns, int paramIndex) {
		params.put(Constants.PAGE_KEY, this);
		return paramIndex;
	}

}
