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

package com.yuehuanghun.mybatis.milu.ext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.binding.MapperMethod.MethodSignature;
import org.apache.ibatis.binding.MapperMethod.SqlCommand;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;

import com.yuehuanghun.mybatis.milu.BaseMapper;

class Supports {
	private static final Map<BaseMapper<?,?>, SqlSessionFactory> BATCH_FACTORY_MAP = new ConcurrentHashMap<>();
	private static final Map<BaseMapper<?,?>, Map<String, MapperMethod>> MAPPER_METHOD_MAP = new ConcurrentHashMap<>();
	
	public static SqlSessionFactory getSqlSessionFactory(BaseMapper<?,?> domainMapper) {
		return BATCH_FACTORY_MAP.computeIfAbsent(domainMapper, key -> {
			SqlSession sqlSession = getSqlSession(domainMapper);
			if (sqlSession instanceof SqlSessionTemplate) {
				return ((SqlSessionTemplate) sqlSession).getSqlSessionFactory();
			}

			throw new RuntimeException("当前Mapper的sqlSession非SqlSessionTemplate");
		});
	}

	private static SqlSession getSqlSession(BaseMapper<?,?> domainMapper) {
		try {
			Field h = domainMapper.getClass().getSuperclass().getDeclaredField("h");
			h.setAccessible(true);
			Object proxy = h.get(domainMapper);
			Field sqlSessionField = proxy.getClass().getDeclaredField("sqlSession");
			sqlSessionField.setAccessible(true);
			return (SqlSession) sqlSessionField.get(proxy);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static MapperMethod getMapperMethod(BaseMapper<?,?> domainMapper, String methodName) {
		return MAPPER_METHOD_MAP.computeIfAbsent(domainMapper, mapper -> {
			return new ConcurrentHashMap<>();
		}).computeIfAbsent(methodName, name -> {
			Method method = getBaseMapperMethod(methodName);
			SqlSession sqlSession = getSqlSession(domainMapper);
			Class<?> mapperInterface = domainMapper.getClass().getInterfaces()[0];
			return new MapperMethod(mapperInterface, method, sqlSession.getConfiguration());
		});
	}
	
	private static Method getBaseMapperMethod(String methodName) {
		Method[] methods = BaseMapper.class.getDeclaredMethods();
		for(Method method : methods) {
			if(method.getName().equals(methodName)) {
				return method;
			}
		}
		throw new IllegalArgumentException("BaseMapper找不到方法：" + methodName);
	}
	
	static class MapperMethod {
		private final SqlCommand command;
		private final MethodSignature method;

		public MapperMethod(Class<?> mapperInterface, Method method, Configuration config) {
			this.command = new SqlCommand(config, mapperInterface, method);
			this.method = new MethodSignature(config, mapperInterface, method);
		}

		public SqlCommand getCommand() {
			return command;
		}

		public MethodSignature getMethod() {
			return method;
		}
	}
}
