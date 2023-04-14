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
package com.yuehuanghun.mybatis.milu.annotation;

/**
 * 模式
 * @author yuehuanghun
 *
 */
public enum Mode {
	/**
	 * 非null时
	 */
	NOT_NULL, 
	/**
	 * 非空时，对于字符串
	 */
	NOT_EMPTY, 
	/**
	 * 全部，不建议作为更新模式
	 */
	ALL,
	/**
	 * 自动，默认模式
	 */
	AUTO
}
