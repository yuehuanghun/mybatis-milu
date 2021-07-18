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
package com.yuehuanghun.mybatis.milu.filler.impl;

import java.util.ArrayList;
import java.util.List;

import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.filler.AttributeValueSupplier;

public class DelegateSupplier implements AttributeValueSupplier<Object> {
	private final List<AttributeValueSupplier<?>> attributeValueSuppliers = new ArrayList<>();

	public DelegateSupplier() {
		this.attributeValueSuppliers.add(new DateSupplier());
		this.attributeValueSuppliers.add(new LocalDateSupplier());
		this.attributeValueSuppliers.add(new LocalDateTimeSupplier());
		this.attributeValueSuppliers.add(new NumberSupplier());
		this.attributeValueSuppliers.add(new SqlDateSupplier());
	}

	@Override
	public Object getValueOnInsert(Class<?> clazz) {
		for(AttributeValueSupplier<?> supplier : attributeValueSuppliers) {
			if(supplier.getClass() == this.getClass()) {
				continue;
			}
			if(supplier.support(clazz)) {
				return supplier.getValueOnInsert(clazz);
			}
		}
		throw new SqlExpressionBuildingException("不支持的自动填充默认值的类型：" + clazz.getName());
	}

	@Override
	public Object getValueOnUpdate(Class<?> clazz) {
		for(AttributeValueSupplier<?> supplier : attributeValueSuppliers) {
			if(supplier.getClass() == this.getClass()) {
				continue;
			}
			if(supplier.support(clazz)) {
				return supplier.getValueOnUpdate(clazz);
			}
		}
		throw new SqlExpressionBuildingException("不支持的自动填充默认值的类型：" + clazz.getName());
	}

	@Override
	public boolean support(Class<?> clazz) {
		for(AttributeValueSupplier<?> supplier : attributeValueSuppliers) {
			if(supplier.getClass() == this.getClass()) {
				continue;
			}
			if(supplier.support(clazz)) {
				return true;
			}
		}
		return false;
	}

}
