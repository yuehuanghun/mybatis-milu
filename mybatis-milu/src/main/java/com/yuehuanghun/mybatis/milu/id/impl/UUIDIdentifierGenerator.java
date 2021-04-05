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
package com.yuehuanghun.mybatis.milu.id.impl;

import java.io.Serializable;
import java.util.UUID;

import com.yuehuanghun.mybatis.milu.id.IdGenerateContext;
import com.yuehuanghun.mybatis.milu.id.IdentifierGenerator;
import com.yuehuanghun.mybatis.milu.tool.Constants;

public class UUIDIdentifierGenerator implements IdentifierGenerator {

	@Override
	public Serializable generate(IdGenerateContext context) {
		return UUID.randomUUID().toString().replace("-", "");
	}

	@Override
	public String getName() {
		return Constants.ID_GENERATOR_UUID;
	}

}
