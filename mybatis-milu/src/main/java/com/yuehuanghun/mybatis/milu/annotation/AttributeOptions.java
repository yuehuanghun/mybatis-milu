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

/**
 * 字段配置
 * @author yuehuanghun
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AttributeOptions {

	/**
	 * 仅可设置一个元素
	 * @return
	 */
	ExampleQuery[] exampleQuery() default {};
	
	/**
	 * 仅可设置一个元素
	 * @return
	 */
	Filler[] filler() default {};
	
	/**
	 * 作为更新字段时，值为何种情况生效
	 * @return
	 */
	Mode updateMode() default Mode.AUTO;
	
	/**
	 * 作为查询条件时，值为何种情况时生效
	 * @return
	 */
	Mode conditionMode() default Mode.AUTO;
}
