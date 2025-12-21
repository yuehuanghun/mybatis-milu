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

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 设置在插入数据时，在实体已设置了id值时是否不再自动生成和设置ID值<br>
 * 可以直接在实体类的ID属性上，或ID属性的其它注解的类注解上，或ID属性上的注释的属性值，或ID属性上的注释。
 */
@Retention(RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface IgnoreGenIdIfPresend {

	/**
	 * true时如果实体对象中id值不为null，则不自动创建并设置ID值
	 * @return true/false
	 */
	boolean value() default true;
}
