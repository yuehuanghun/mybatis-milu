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

package com.yuehuanghun.mybatis.milu.criteria;

import java.util.Set;

import com.yuehuanghun.mybatis.milu.data.Sort.Direction;
import com.yuehuanghun.mybatis.milu.data.Sort.NullHandling;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 排序
 * @author yuehuanghun
 *
 */
interface Sort extends Expression, Set<com.yuehuanghun.mybatis.milu.criteria.Sort.Order> {
	default boolean add(String attrName, Direction direction, NullHandling nullHandling) {
		return add(Order.order(attrName, direction, nullHandling));
	}
	default boolean add(String attrName, NullHandling nullHandling) {
		return add(Order.order(attrName, null, nullHandling));
	}
	
	default boolean add(String attrName, Direction direction) {
		return add(Order.order(attrName, direction, null));
	}
	
	default boolean add(String attrName) {
		return add(Order.order(attrName, null, null));
	}

	@Getter
	@EqualsAndHashCode
	class Order {
		private Direction direction = null;
		
		private NullHandling nullHandling = NullHandling.NATIVE;
		
		private String attributeName;
		
		private Order(String attrName, Direction direction, NullHandling nullHandling) {
			this.attributeName = attrName;
			this.direction = direction;
			if(nullHandling != null) {
				this.nullHandling = nullHandling;
			}
		}
		
		public static Order order(String attrName, Direction direction, NullHandling nullHandling) {
			return new Order(attrName, direction, nullHandling);
		}
	}
}
