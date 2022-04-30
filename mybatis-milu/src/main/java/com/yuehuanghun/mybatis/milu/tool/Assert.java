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
package com.yuehuanghun.mybatis.milu.tool;

import java.util.Objects;

import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;

public abstract class Assert {
	private Assert() {
		
	}

	public static void notNull(Object target, String error) {
		if(Objects.isNull(target)) {
			throw new SqlExpressionBuildingException(error);
		}
	}
	
	public static void isTrue(boolean flag, String error) {
		if(!flag) {
			throw new SqlExpressionBuildingException(error);
		}
	}
	
	public static void notBlank(CharSequence sequence, String error) {
		if(sequence == null || sequence.length() == 0) {
			throw new SqlExpressionBuildingException(error);
		}
	}
}
