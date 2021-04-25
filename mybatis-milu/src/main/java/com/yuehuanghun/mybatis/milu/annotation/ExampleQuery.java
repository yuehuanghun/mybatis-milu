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

/**
 * 应用于通用查询方法findByExample、findByExampleAndSort、countByExample中，标记属性作为查询条件的匹配方法
 * @author yuehuanghun
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ExampleQuery {

	/**
	 * 匹配方式
	 * @return 匹配方式
	 */
	MatchType matchType() default MatchType.EQUAL;
	
	/**
	 * 当前属性范围查询时的传值开始键名
	 * @return 键名
	 */
	String startKeyName() default StringUtils.EMPTY;
	
	/**
	 * 范围查询时，是否包括开始值，默认true
	 * @return true/false
	 */
	boolean startValueContain() default true;
	
	/**
	 * 当前属性范围查询时的传值结束键名
	 * @return 键名
	 */
	String endKeyName() default StringUtils.EMPTY;
	
	/**
	 * 范围查询时，是否包括结束值，默认true
	 * @return true/false
	 */
	boolean endValueContain() default true;
	
	/**
	 * 当前属性IN查询时传值的键名<br>
	 * 传值允许为数组、集合或半角逗号隔开的字符串
	 * @return 键名
	 */
	String inKeyName() default StringUtils.EMPTY;
	
	public enum MatchType {
		/**
		 * 等，即 = 查询值
		 */
		EQUAL, 
		/**
		 * 以查询值开始，即LIKE CONCAT(查询值, '%')
		 */
		START_WITH, 
		/**
		 * 以查询值结束，即LIKE CONCAT('%', 查询值,)
		 */
		END_WITH,
		/**
		 * 包含，即LIKE CONCAT('%', 查询值, '%')
		 */
		CONTAIN,
		/**
		 * 匹配，即LIKE 查询值，查询值自身已带匹配符
		 */
		LIKE 
	}
}
