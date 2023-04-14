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
package com.yuehuanghun.mybatis.milu.filler.impl;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.yuehuanghun.mybatis.milu.filler.AttributeValueSupplier;

public class NumberSupplier implements AttributeValueSupplier<Number> {

	@Override
	public Number getValueOnInsert(Class<?> clazz) {
		return getDefultValue(clazz);
	}

	@Override
	public Number getValueOnUpdate(Class<?> clazz) {
		return getDefultValue(clazz);
	}
	
	private Number getDefultValue(Class<?> clazz) {
		if(Byte.class == clazz) {
			return new Byte((byte) 0);
		} else if(Short.class == clazz) {
			return new Short((short) 0);
		} else if(Integer.class == clazz) {
			return new Integer(0);
		} else if(Long.class == clazz) {
			return new Long(0);
		} else if(Float.class == clazz) {
			return new Float(0);
		} else if(Double.class == clazz) {
			return new Double(0);
		} else if(BigDecimal.class == clazz) {
			return BigDecimal.ZERO;
		} else if(BigInteger.class == clazz) {
			return BigInteger.ZERO;
		} 
		throw new RuntimeException(String.format("未支持默认值的数字类型：%s", clazz.getName()));
	}

	@Override
	public boolean support(Class<?> clazz) {
		return Number.class.isAssignableFrom(clazz);
	}

}
