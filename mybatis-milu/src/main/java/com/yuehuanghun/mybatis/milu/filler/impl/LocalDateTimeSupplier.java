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
package com.yuehuanghun.mybatis.milu.filler.impl;

import java.time.LocalDateTime;

import com.yuehuanghun.mybatis.milu.filler.AttributeValueSupplier;

public class LocalDateTimeSupplier implements AttributeValueSupplier<LocalDateTime> {

	@Override
	public LocalDateTime getValueOnInsert(Class<?> clazz) {
		return LocalDateTime.now();
	}

	@Override
	public LocalDateTime getValueOnUpdate(Class<?> clazz) {
		return LocalDateTime.now();
	}

	@Override
	public boolean support(Class<?> clazz) {
		return clazz == LocalDateTime.class;
	}

}
