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
package com.yuehuanghun.mybatis.milu.filler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.invoker.Invoker;

import com.yuehuanghun.mybatis.milu.annotation.Filler.FillMode;

import lombok.Getter;

public class Filler {
	private static final Object[] EMPTY_ARRAY = new Object[0];
	@Getter
	private Invoker getter;
	@Getter
	private Invoker setter;
	
	private final AttributeValueSupplier<?> attributeValueSupplier;
	
	private final Class<?> fieldClass;
	
	private final FillMode fillMode;
	
	public Filler(MetaClass metaClass, Field field, AttributeValueSupplier<?> attributeValueSupplier, FillMode fillMode) {
		String fieldName = field.getName();
		if(metaClass.hasGetter(fieldName)) {
			getter = metaClass.getGetInvoker(fieldName);
		}
		if(metaClass.hasSetter(fieldName)) {
			setter = metaClass.getSetInvoker(fieldName);
		}
		this.attributeValueSupplier = attributeValueSupplier;
		this.fieldClass = field.getType();
		this.fillMode = fillMode;
	}
	
	private boolean shouldFill(Object target) {
		if(this.fillMode == FillMode.ANY) {
			return true;
		}
		
		if(getter == null) {
			return true;
		}
		try {
			return getter.invoke(target, EMPTY_ARRAY) == null;
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void setValue(Object target, boolean insert) {
		if(setter == null) {
			return;
		}
		if(!shouldFill(target)) {
			return;
		}
		
		Object value;
		if(insert) {
			value = attributeValueSupplier.getValueOnInsert(fieldClass);
		} else {
			value = attributeValueSupplier.getValueOnUpdate(fieldClass);
		}
		
		try {
			setter.invoke(target, new Object[] { value });
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
