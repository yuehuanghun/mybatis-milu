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

import com.yuehuanghun.mybatis.milu.filler.AttributeValueSupplier;
import com.yuehuanghun.mybatis.milu.filler.impl.DelegateSupplier;

/**
 * 实体类对象属性值自动填充设置
 * @author yuehuanghun
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Filler {

	/**
	 * 属性值提供者<br>
	 * 已提供默认属性值提供者：Date、sql.Date、LocalDate、LocalDateTime默认为当前日期或日期时间<br>
	 * 常见Number子类被设置为0值
	 * @return 填充数据供应者
	 */
	Class<? extends AttributeValueSupplier<?>> attributeValueSupplier() default DelegateSupplier.class;
	
	/**
	 * 在插入时自动填充（属性值非null时）
	 * @return true/false
	 */
	boolean fillOnInsert() default false;
	
	/**
	 * 在更新时自动填充（属性值非null时）
	 * @return true/false
	 */
	boolean fillOnUpdate() default false;
	
	/**
	 * 填充模式，默认值为FillMode.NOT_NULL<br>
	 * FillMode.NOT_NULL 属性可以手动设值<br>
	 * FillMode.ANY 不管属性有没有值，都通过Supplier获取一个新的值
	 * @return FillMode.ANY：任何情况都填充，FillMode.NOT_NULL：属性值不为null时填充
	 */
	FillMode fillMode() default FillMode.NOT_NULL;
	
	
	public static enum FillMode {
		/**
		 * 任何情况都填充
		 */
		ANY,
		/**
		 * 属性值不为null时填充
		 */
		NOT_NULL
	}
}
