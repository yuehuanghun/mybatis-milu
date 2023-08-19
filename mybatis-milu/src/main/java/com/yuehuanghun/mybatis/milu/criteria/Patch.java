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

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.invoker.Invoker;

import com.yuehuanghun.mybatis.milu.criteria.lambda.LambdaReflections;
import com.yuehuanghun.mybatis.milu.criteria.lambda.SerializableFunction;
import com.yuehuanghun.mybatis.milu.exception.OrmBuildingException;
import com.yuehuanghun.mybatis.milu.metamodel.EntityBuilder;

import lombok.Getter;

public class Patch {
	@Getter
	private final Map<String, Object> container = new LinkedHashMap<>();
	private final static Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	
	public static Patch newInstance() {
		return new Patch();
	}
	
	public Patch add(String attrName, Object attrValue) {
		container.put(attrName, attrValue);
		return this;
	}
	
	public <T, M> Patch add(SerializableFunction<T, M> attrGetter, Object attrValue) {
		container.put(LambdaReflections.fnToFieldName(attrGetter), attrValue);
		return this;
	}
	
	/**
	 * 从实体类实例中提取的属性及其值
	 * @param entity 实体类实例或Map实例
	 * @param includeAttrs 需提取的属性
	 * @return 当前对象
	 */
	@SuppressWarnings("rawtypes")
	public Patch from(Object entity, String... includeAttrs) {
		if(entity == null) {
			return this;
		}
		
		if(entity instanceof Map) {
			for(String attr : includeAttrs) {
				if(((Map) entity).containsKey(attr)) {
					container.put(attr, ((Map) entity).get(attr));
				}
			}
			return this;
		}
		
		if(includeAttrs.length > 0) {
			MetaClass metaClass = MetaClass.forClass(entity.getClass(), EntityBuilder.REFLECTOR_FACTORY);
			for(String attr : includeAttrs) {
				Invoker invoker = metaClass.getGetInvoker(attr);
				try {
					add(attr, invoker.invoke(entity, EMPTY_OBJECT_ARRAY));
				} catch (IllegalAccessException | InvocationTargetException e) {
					throw new OrmBuildingException(e);
				}
			}
		}
		return this;
	}
	
	public Patch from(Map<String, Object> params) {
		if(params == null) {
			return this;
		}
		
		container.putAll(params);
		
		return this;
	}
	
	public boolean isEmpty() {
		return container.isEmpty();
	}
	
	/**
	 * 从实体类实例中提取的属性及其值以创建一个Patch实例
	 * @param entity 实体类实例
	 * @param includeAttrs 需提取的属性
	 * @return 新实例
	 */
	public static Patch of(Object entity, String... includeAttrs) {
		return newInstance().from(entity, includeAttrs);
	}
	
	public void forEach(BiConsumer<String, Object> action) {
		container.forEach((key, value) -> action.accept(key, value));
	}
	
	public Set<String> getAttrNames() {
		return container.keySet();
	}
	
	public Object getAttrValue(String attrName) {
		return container.get(attrName);
	}
}
