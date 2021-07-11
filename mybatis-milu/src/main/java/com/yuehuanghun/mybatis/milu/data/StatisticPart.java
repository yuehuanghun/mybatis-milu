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
package com.yuehuanghun.mybatis.milu.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yuehuanghun.mybatis.milu.exception.IllegalMethodExpressException;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;


/**
 * 统计片段
 * @author yuehuanghun
 *
 */
public class StatisticPart {

    private static final Pattern TEMPLATE = Pattern.compile("^(sum|count|min|max|avg)(.*)$", Pattern.CASE_INSENSITIVE);

    private final String property;
    private final String function;
    
    public StatisticPart(String source, Class<?> clazz) {
    	Matcher matcher = TEMPLATE.matcher(source);
    	if(matcher.find()) {
    		if(matcher.groupCount() != 2) {
    			throw new IllegalMethodExpressException();
    		}
    	} else {
    		throw new IllegalMethodExpressException();
    	}
    	this.property = StringUtils.uncapitalize(matcher.group(2));
    	this.function = matcher.group(1).toLowerCase();
    }

    public String getProperty() {

        return property;
    }

    public String getFunction() {
    	return function;
    }
    
    @Override
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }

        if (obj == null || !getClass().equals(obj.getClass())) {
            return false;
        }

        StatisticPart that = (StatisticPart) obj;
        return this.property.equals(that.property) && this.function.equals(that.function);
    }

    @Override
    public int hashCode() {

        int result = 37;
        result += 17 * property.hashCode();
        result += 17 * function.hashCode();
        return result;
    }

    @Override
    public String toString() {

        return String.format("%s(%s)", function, property);
    }

}