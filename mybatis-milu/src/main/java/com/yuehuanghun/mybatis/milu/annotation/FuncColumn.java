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

package com.yuehuanghun.mybatis.milu.annotation;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yuehuanghun.mybatis.milu.db.DbEnum;

/**
 * 函数列。
 * 函数表达式自定义，框架只对表达式做有限的安全性检测，开发者应当保证表达式内容的安全性，避免不安全操作。
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(FIELD)
public @interface FuncColumn {

	/**
	 * 表达式<br>
	 * 可以使用${属性/列名}指定实体内的属性或列名，例如IFNULL(${number}, 0)，在表关联查询时，会自动添加表别名<br>
     * 如果不使用${属性/列名}指定，则要注意是否可能出现表间重复列的问题<br>
     * 注：框架只对表达式做有限的安全性检测，开发者应当保证表达式内容的安全性，避免不安全操作。
	 * @return 函数列表达式
	 */
	String expression();
	
	/**
	 * 用于哪种数据库
	 * @return 指定的数据库枚举，默认为作用于任意数据库
	 */
	DbEnum forDb() default DbEnum.ANY;
}
