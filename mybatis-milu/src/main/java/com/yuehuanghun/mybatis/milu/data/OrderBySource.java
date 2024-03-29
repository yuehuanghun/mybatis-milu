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
package com.yuehuanghun.mybatis.milu.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yuehuanghun.mybatis.milu.data.Sort.Direction;
import com.yuehuanghun.mybatis.milu.data.Sort.Order;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

/**
 * 排序创建帮助类<br>
 * reference org.springframework.data.repository.query.parser.OrderBySource
 * @author yuehuanghun
 *
 */
public class OrderBySource {

    private static final String BLOCK_SPLIT = "(?<=Asc|Desc)(?=\\p{Lu})";
    private static final Pattern DIRECTION_SPLIT = Pattern.compile("(.+?)(Asc|Desc)?$");
    private static final String INVALID_ORDER_SYNTAX = "Invalid order syntax for part %s!";
    private static final Set<String> DIRECTION_KEYWORDS = new HashSet<String>(Arrays.asList("Asc", "Desc"));

    private final List<Order> orders;

    public OrderBySource(String clause) {
        this(clause, null);
    }

    public OrderBySource(String clause, Class<?> domainClass) {

        this.orders = new ArrayList<Sort.Order>();

        for (String part : clause.split(BLOCK_SPLIT)) {

            Matcher matcher = DIRECTION_SPLIT.matcher(part);

            if (!matcher.find()) {
                throw new IllegalArgumentException(String.format(INVALID_ORDER_SYNTAX, part));
            }

            String propertyString = matcher.group(1);
            String directionString = matcher.group(2);

            // No property, but only a direction keyword
            if (DIRECTION_KEYWORDS.contains(propertyString) && directionString == null) {
                throw new IllegalArgumentException(String.format(INVALID_ORDER_SYNTAX, part));
            }

            Direction direction = StringUtils.isNotBlank(directionString) ? Direction.fromString(directionString) : null;
            this.orders.add(createOrder(propertyString, direction, domainClass));
        }
    }

    private Order createOrder(String propertySource, Direction direction, Class<?> domainClass) {
    	return new Order(direction, StringUtils.uncapitalize(propertySource));
    }

    public Sort toSort() {
        return this.orders.isEmpty() ? null : Sort.by(this.orders);
    }

    @Override
    public String toString() {
        return "Order By " + StringUtils.collectionToDelimitedString(orders, ", ");
    }
}