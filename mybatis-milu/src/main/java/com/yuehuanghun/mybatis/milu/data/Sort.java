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
package com.yuehuanghun.mybatis.milu.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import com.yuehuanghun.mybatis.milu.criteria.lambda.LambdaReflections;
import com.yuehuanghun.mybatis.milu.criteria.lambda.SerializableFunction;
import com.yuehuanghun.mybatis.milu.tool.Assert;
import com.yuehuanghun.mybatis.milu.tool.Streamable;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

/**
 * 排序
 * reference org.springframework.data.domain.Sort
 * @author yuehuanghun
 *
 */
public class Sort implements Streamable<com.yuehuanghun.mybatis.milu.data.Sort.Order>, Serializable {

	private static final long serialVersionUID = 5737186511678863905L;

	private static final Sort UNSORTED = Sort.by(new Order[0]);

	public static final Direction DEFAULT_DIRECTION = Direction.ASC;

	private final List<Order> orders;

	private Sort(Order... orders) {
		this(Arrays.asList(orders));
	}

	private Sort(List<Order> orders) {

		Assert.notNull(orders, "Orders不可为 null!");

		this.orders = orders;
	}

	private Sort(String... properties) {
		this(DEFAULT_DIRECTION, properties);
	}

	public Sort(Direction direction, String... properties) {
		this(direction, properties == null ? new ArrayList<>() : Arrays.asList(properties));
	}

	public Sort(Direction direction, List<String> properties) {

		if (properties == null || properties.isEmpty()) {
			throw new IllegalArgumentException("排序属性至少要有一个值!");
		}

		this.orders = new ArrayList<>(properties.size());

		for (String property : properties) {
			this.orders.add(new Order(direction, property));
		}
	}

	public static Sort by(String... properties) {

		Assert.notNull(properties, "排序属性不能为null!");

		return properties.length == 0 ? Sort.unsorted() : new Sort(properties);
	}
	
	@SuppressWarnings("unchecked")
	public static <T, M> Sort by(SerializableFunction<T, M>... propertyGetterFns) {
		Assert.notNull(propertyGetterFns, "排序属性不能为null!");
		
		String[] properties = new String[propertyGetterFns.length];
		for(int i = 0; i < properties.length; i++) {
			properties[i] = LambdaReflections.fnToFieldName(propertyGetterFns[i]);
		}
		return by(properties);
	}

	public static Sort by(List<Order> orders) {

		Assert.notNull(orders, "Orders不可为 null!");

		return orders.isEmpty() ? Sort.unsorted() : new Sort(orders);
	}

	public static Sort by(Order... orders) {

		Assert.notNull(orders, "Orders不可为 null!");

		return new Sort(orders);
	}
	
	/**
	 * 创建排序
	 * @param direction 排序方向，DESC/ASC
	 * @param properties 排序属性
	 * @return
	 */
	public static Sort by(String direction, String[] properties) {
		return by(Direction.fromString(direction), properties);
	}

	public static Sort by(Direction direction, String... properties) {

		Assert.notNull(direction, "排序方向不能为null!");
		Assert.notNull(properties, "排序属性不能为null!");
		Assert.isTrue(properties.length > 0, "排序属性至少有一个值!");

		return Sort.by(Arrays.stream(properties)//
				.map(it -> new Order(direction, it))//
				.collect(Collectors.toList()));
	}
	
	@SuppressWarnings("unchecked")
	public static <T, M> Sort by(Direction direction, SerializableFunction<T, M>... propertyGetterFns) {
		Assert.notNull(direction, "排序方向不能为null!");
		Assert.notNull(propertyGetterFns, "排序属性不能为null!");
		Assert.isTrue(propertyGetterFns.length > 0, "排序属性至少有一个值!");
		
		String[] properties = new String[propertyGetterFns.length];
		for(int i = 0; i < properties.length; i++) {
			properties[i] = LambdaReflections.fnToFieldName(propertyGetterFns[i]);
		}
		return by(direction, properties);
	}
	
	/**
	 * 增加排序
	 * @param direction 排序方向，DESC/ASC
	 * @param properties 排序属性
	 * @return
	 */
	public Sort and(String direction, String... properties) {
		return and(Direction.fromString(direction), properties);
	}
	
	public Sort and(Direction direction, String... properties) {
		for(String prop : properties) {
			orders.add(new Order(direction, prop));
		}
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <T, M> Sort and(Direction direction, SerializableFunction<T, M>... propertyGetterFns) {
		for(SerializableFunction<T, M> fn : propertyGetterFns) {
			orders.add(new Order(direction, LambdaReflections.fnToFieldName(fn)));
		}
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public static <T, M> Sort desc(SerializableFunction<T, M>... propertyGetterFns) {
		return by(Direction.DESC, propertyGetterFns);
	}
	
	@SuppressWarnings("unchecked")
	public <T, M> Sort andDesc(SerializableFunction<T, M>... propertyGetterFns) {
		return and(Direction.DESC, propertyGetterFns);
	}
	
	@SuppressWarnings("unchecked")
	public static <T, M> Sort asc(SerializableFunction<T, M>... propertyGetterFns) {
		return by(Direction.ASC, propertyGetterFns);
	}
	
	@SuppressWarnings("unchecked")
	public <T, M> Sort andAsc(SerializableFunction<T, M>... propertyGetterFns) {
		return and(Direction.ASC, propertyGetterFns);
	}
	
	public static Sort desc(String... properties) {
		return by(Direction.DESC, properties);
	}
	
	public Sort andDesc(String... properties) {
		return and(Direction.DESC, properties);
	}
	
	public static Sort asc(String... properties) {
		return by(Direction.ASC, properties);
	}
	
	public Sort andAsc(String... properties) {
		return and(Direction.ASC, properties);
	}

	public static Sort unsorted() {
		return UNSORTED;
	}

	public Sort descending() {
		return withDirection(Direction.DESC);
	}

	public Sort ascending() {
		return withDirection(Direction.ASC);
	}

	public boolean isSorted() {
		return !orders.isEmpty();
	}

	public boolean isUnsorted() {
		return !isSorted();
	}

	public Sort and(Sort sort) {
		Assert.notNull(sort, "Sort must not be null!");
		for (Order order : sort) {
			orders.add(order);
		}

		return this;
	}

	public Order getOrderFor(String property) {

		for (Order order : this) {
			if (order.getProperty().equals(property)) {
				return order;
			}
		}

		return null;
	}

	public Iterator<Order> iterator() {
		return this.orders.iterator();
	}

	@Override
	public boolean equals( Object obj) {

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Sort)) {
			return false;
		}

		Sort that = (Sort) obj;

		return this.orders.equals(that.orders);
	}

	@Override
	public int hashCode() {

		int result = 17;
		result = 31 * result + orders.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return orders.isEmpty() ? "UNSORTED" : StringUtils.collectionToCommaDelimitedString(orders);
	}

	private Sort withDirection(Direction direction) {

		return Sort.by(orders.stream().map(it -> new Order(direction, it.getProperty())).collect(Collectors.toList()));
	}

	/**
	 * 排序方向枚举
	 */
	public static enum Direction {

		ASC, DESC;

		public boolean isAscending() {
			return this.equals(ASC);
		}

		public boolean isDescending() {
			return this.equals(DESC);
		}

		public static Direction fromString(String value) {

			try {
				return Direction.valueOf(value.toUpperCase(Locale.US));
			} catch (Exception e) {
				throw new IllegalArgumentException(String.format(
						"Invalid value '%s' for orders given! Has to be either 'desc' or 'asc' (case insensitive).", value), e);
			}
		}

		public static Optional<Direction> fromOptionalString(String value) {

			try {
				return Optional.of(fromString(value));
			} catch (IllegalArgumentException e) {
				return Optional.empty();
			}
		}
	}

	/**
	 * null值排序方式枚举
	 */
	public static enum NullHandling {

		/**
		 * 让数据存储决定如何处理空值
		 */
		NATIVE,

		/**
		 * 对已用数据存储区的提示，用于将具有空值的条目排序在非空条目之前
		 */
		NULLS_FIRST,

		/**
		 * 对已用数据存储区的提示，用于将具有空值的条目排序在非空条目之后
		 */
		NULLS_LAST;
	}

	public static class Order implements Serializable {

		private static final long serialVersionUID = 1522511010900108987L;
		private static final boolean DEFAULT_IGNORE_CASE = false;
		private static final NullHandling DEFAULT_NULL_HANDLING = NullHandling.NATIVE;

		private final Direction direction;
		private final String property;
		private final boolean ignoreCase;
		private final NullHandling nullHandling;

		public Order(Direction direction, String property) {
			this(direction, property, DEFAULT_IGNORE_CASE, DEFAULT_NULL_HANDLING);
		}

		public Order(Direction direction, String property, NullHandling nullHandlingHint) {
			this(direction, property, DEFAULT_IGNORE_CASE, nullHandlingHint);
		}

		public static Order by(String property) {
			return new Order(DEFAULT_DIRECTION, property);
		}
		
		public static <T, M>  Order by(SerializableFunction<T, M> getterFn) {
			return new Order(DEFAULT_DIRECTION, LambdaReflections.fnToFieldName(getterFn));
		}

		public static Order asc(String property) {
			return new Order(Direction.ASC, property, DEFAULT_NULL_HANDLING);
		}
		
		public static <T, M> Order asc(SerializableFunction<T, M> getterFn) {
			return new Order(Direction.ASC, LambdaReflections.fnToFieldName(getterFn), DEFAULT_NULL_HANDLING);
		}

		public static Order desc(String property) {
			return new Order(Direction.DESC, property, DEFAULT_NULL_HANDLING);
		}
		
		public static <T, M> Order desc(SerializableFunction<T, M> getterFn) {
			return new Order(Direction.DESC, LambdaReflections.fnToFieldName(getterFn), DEFAULT_NULL_HANDLING);
		}

		private Order(Direction direction, String property, boolean ignoreCase, NullHandling nullHandling) {

			if (StringUtils.isBlank(property)) {
				throw new IllegalArgumentException("排序属性不能为空!");
			}

			this.direction = direction == null ? DEFAULT_DIRECTION : direction;
			this.property = property;
			this.ignoreCase = ignoreCase;
			this.nullHandling = nullHandling;
		}

		public Direction getDirection() {
			return direction;
		}

		public String getProperty() {
			return property;
		}

		public boolean isAscending() {
			return this.direction.isAscending();
		}

		public boolean isDescending() {
			return this.direction.isDescending();
		}

		public boolean isIgnoreCase() {
			return ignoreCase;
		}

		public Order with(Direction direction) {
			return new Order(direction, this.property, this.ignoreCase, this.nullHandling);
		}

		public Order withProperty(String property) {
			return new Order(this.direction, property, this.ignoreCase, this.nullHandling);
		}

		public Sort withProperties(String... properties) {
			return Sort.by(this.direction, properties);
		}

		public Order ignoreCase() {
			return new Order(direction, property, true, nullHandling);
		}

		public Order with(NullHandling nullHandling) {
			return new Order(direction, this.property, ignoreCase, nullHandling);
		}

		public Order nullsFirst() {
			return with(NullHandling.NULLS_FIRST);
		}

		public Order nullsLast() {
			return with(NullHandling.NULLS_LAST);
		}

		public Order nullsNative() {
			return with(NullHandling.NATIVE);
		}

		public NullHandling getNullHandling() {
			return nullHandling;
		}

		@Override
		public int hashCode() {

			int result = 17;

			result = 31 * result + direction.hashCode();
			result = 31 * result + property.hashCode();
			result = 31 * result + (ignoreCase ? 1 : 0);
			result = 31 * result + nullHandling.hashCode();

			return result;
		}

		@Override
		public boolean equals( Object obj) {

			if (this == obj) {
				return true;
			}

			if (!(obj instanceof Order)) {
				return false;
			}

			Order that = (Order) obj;

			return this.direction.equals(that.direction) && this.property.equals(that.property)
					&& this.ignoreCase == that.ignoreCase && this.nullHandling.equals(that.nullHandling);
		}

		@Override
		public String toString() {

			String result = String.format("%s: %s", property, direction);

			if (!NullHandling.NATIVE.equals(nullHandling)) {
				result += ", " + nullHandling;
			}

			if (ignoreCase) {
				result += ", ignoring case";
			}

			return result;
		}
	}
}
