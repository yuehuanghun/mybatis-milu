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
package com.yuehuanghun.mybatis.milu.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yuehuanghun.mybatis.milu.tool.StringUtils;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface StatementOptions {
	
	/**
	 * 选择的属性，多个用英文逗号隔开，如id,createTime，仅可为主实体类的属性<br>
	 * 不为空时exselects的设置无效
	 * @return 选择的属性
	 */
	String selects() default StringUtils.EMPTY;
	
	/**
	 * 不选择的属性，多个用英文逗号隔开，如id,createTime，仅可为主实体类的属性<br>
	 * @return 不选择的属性
	 */
	String exselects() default StringUtils.EMPTY;
	
	/**
	 * 覆盖Mapper的methodName作为查询表达式
	 * @return 查询表达式
	 */
	String asExpression() default StringUtils.EMPTY;
}
