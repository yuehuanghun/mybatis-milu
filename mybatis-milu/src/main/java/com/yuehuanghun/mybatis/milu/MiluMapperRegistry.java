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
package com.yuehuanghun.mybatis.milu;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.ibatis.binding.MapperRegistry;

import com.yuehuanghun.mybatis.milu.metamodel.EntityBuilder;

public class MiluMapperRegistry extends MapperRegistry {
	private final MiluConfiguration config;

	public MiluMapperRegistry(MiluConfiguration config) {
		super(config);
		this.config = config;
	}

	@Override
	public <T> void addMapper(Class<T> type) {
		if(BaseMapper.class.isAssignableFrom(type)) {
			if(config.isCreateEntityResultMap()) {
				Class<?> entityClass = getGenericEntity(type);
	    		EntityBuilder.instance(entityClass, config).buildEntityDefaultResultMap(type);
			}
			MapperNamingQueryBuilder parser = new MapperNamingQueryBuilder(config, type);
			parser.parse();
		}
		super.addMapper(type);
	}

	private Class<?> getGenericEntity(Class<?> mapperClass) {
		Type[] interfaces = mapperClass.getGenericInterfaces();
		for (Type type : interfaces) {
			if (!(type instanceof ParameterizedType)) {
				continue;
			}
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Type rawType = parameterizedType.getRawType();
			if (rawType.equals(BaseMapper.class)) {
				return (Class<?>) parameterizedType.getActualTypeArguments()[0];
			}
		}
		return null;
	}
}
