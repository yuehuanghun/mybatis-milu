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
package com.yuehuanghun.mybatis.milu.pagehelper;

import com.yuehuanghun.mybatis.milu.tool.Assert;

import lombok.Getter;

/**
 * 分页参数默认实现
 * @author yuehuanghun
 *
 */
@Getter
public class PageRequest implements Pageable {
	
	private int pageNum;
	
	private int pageSize;
	
	private boolean count;

	/**
	 * 分页信息
	 * @param pageNum 当前页码，从1开始
	 * @param pageSize 每页行数
	 * @param count 是否查询符合条件的总行数
	 */
	public PageRequest(int pageNum, int pageSize, boolean count) {
		Assert.isTrue(pageNum >= 1, "当前页码pageNum不能小于1");
		Assert.isTrue(pageSize >= 1, "每页行数pageSize不能小于1");
		this.pageNum = pageNum;
		this.pageSize = pageSize;
		this.count = count;
	}

	/**
	 * 分页信息
	 * @param pageNum 当前页码，从1开始
	 * @param pageSize 每页行数
	 */
	public PageRequest(int pageNum, int pageSize) {
		this(pageNum, pageSize, true);
	}

	/**
	 * 分页信息，第一页的pageSize条数据
	 * @param pageSize 每页行数
	 * @param count 是否查询符合条件的总行数
	 */
	public PageRequest(int pageSize, boolean count) {
		this(1, pageSize, count);
	}

	/**
	 * 分页信息，第一页的pageSize条数据，查询符合条件总行数
	 * @param pageSize 每页行数
	 */
	public PageRequest(int pageSize) {
		this(1, pageSize, true);
	}
}
