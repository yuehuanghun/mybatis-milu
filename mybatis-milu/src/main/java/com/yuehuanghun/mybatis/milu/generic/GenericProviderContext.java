/*
 * Copyright 2020-current the original author or authors.
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
package com.yuehuanghun.mybatis.milu.generic;

import java.lang.reflect.Method;

import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.session.Configuration;

import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.tool.Segment;

import lombok.Getter;

public class GenericProviderContext {
	@Getter
	private final Class<?> mapperType;
	@Getter
	private final Method mapperMethod;
	@Getter
	private final MiluConfiguration configuration;
	@Getter
	private final KeyGenerator keyGenerator;
	@Getter
	private final Entity entity;
	
	public final Tool tool = new Tool();
	
	public GenericProviderContext(Class<?> mapperType, Method mapperMethod, Configuration configuration, KeyGenerator keyGenerator, Entity entity) {
		super();
		this.mapperType = mapperType;
		this.mapperMethod = mapperMethod;
		this.configuration = (MiluConfiguration) configuration;
		this.keyGenerator = keyGenerator;
		this.entity = entity;
	}
	
	public class Tool {
		
		/**
		 * 通过属性名构建在SQL模板中的列占位符<br>
		 * $attrName$
		 * @param attrName 实体属性名
		 * @return SQL模板中列占位符，格式为$attrName$
		 */
		public String columnPlaceholder(String attrName) {
			return Segment.DOLLAR + attrName + Segment.DOLLAR;
		}
		
		/**
		 * 获取脚本参数占位符
		 * @param columnKey 列参数键
		 * @return 脚本查询参数占位符，格式为：${columnKey}
		 */
		public String columnScriptParam(String columnKey) {
			return Segment.HASH_LEFT_BRACE + columnKey + Segment.RIGHT_BRACE;
		}
	}
}
