/*
 * Copyright 2020-2022 the original author or authors.
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
import com.yuehuanghun.mybatis.milu.tool.converter.ExampleQueryConverter;
import com.yuehuanghun.mybatis.milu.tool.converter.ExampleQueryConverter.AutoConverter;

/**
 * 应用于通用查询方法findByExample、findByExampleAndSort、countByExample中，标记属性作为查询条件的匹配方法
 * @author yuehuanghun
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD})
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
	 * 对范围查询有效，对startValue、endValue值的转换<br>
	 * 目前仅能对startKey、endKey为二级以上属性时并且属性所在对象为Map有效<br>
	 * 例如startKey = "a.b"，a为Map对象时，可对b进行转换<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;startKey = "a.b.c"， b为Map对象时，可对c进行转换<br>
	 * 转换后的值会覆盖原值<br>
	 * 该功能为了使用Map接收参数后，作为查询参数时，对象类型非对应字段类型的问题，而某些数据库无法做类型隐式转换时。<br>
	 * 如果不需要转换可以设置类为ExampleQueryConverter.NullConverter.class
	 * @return 转换器
	 */
	Class<? extends ExampleQueryConverter> valueConverter() default AutoConverter.class;
	
	/**
	 * 当前属性IN查询时传值的键名<br>
	 * 传值允许为数组、集合或半角逗号隔开的字符串
	 * @return 键名
	 */
	String inKeyName() default StringUtils.EMPTY;
	
	/**
	 * 是否自动设置范围查询的键<br>
	 * 例如被注解的属性名为status：<br>
	 * startKeyName = 属性名+Begin，即为statusBegin<br>
	 * endKeyName = 属性名+End，即为statusEnd<br>
	 * inKeyName = 属性名+List，即为statusList
	 * @return 默认 false
	 */
	boolean autoKeying() default false;
	
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
