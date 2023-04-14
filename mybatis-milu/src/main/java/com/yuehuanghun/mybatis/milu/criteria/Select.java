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

import lombok.AllArgsConstructor;
import lombok.Data;

interface Select extends Expression {

	void add(String functionName, String property, String alias);
	
	void add(String property);
	
	@Data
	@AllArgsConstructor
	class Function {
		/**
		 * 函数名
		 */
		private String functionName;
		/**
		 * 属性名
		 */
		private String propertyName;
		/**
		 * 别名
		 */
		private String alias;
	}
}
