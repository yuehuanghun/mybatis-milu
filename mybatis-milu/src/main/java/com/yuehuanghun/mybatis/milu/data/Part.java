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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yuehuanghun.mybatis.milu.tool.Assert;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;


/**
 * 查询条件片段
 * reference org.springframework.data.repository.query.parser.Part
 * @author yuehuanghun
 *
 */
public class Part {

    private static final Pattern IGNORE_CASE = Pattern.compile("Ignor(ing|e)Case");

    private final String property;
    private final Part.Type type;

    private IgnoreCaseType ignoreCase = IgnoreCaseType.NEVER;

    public Part(String source, Class<?> clazz) {
        this(source, clazz, false);
    }

    public Part(String source, Class<?> clazz, boolean alwaysIgnoreCase) {

        Assert.notBlank(source, "Part source must not be null or emtpy!");
        Assert.notNull(clazz, "Type must not be null!");

        String partToUse = detectAndSetIgnoreCase(source);
        if (alwaysIgnoreCase && ignoreCase != IgnoreCaseType.ALWAYS) {
            this.ignoreCase = IgnoreCaseType.WHEN_POSSIBLE;
        }
        this.type = Type.fromProperty(partToUse);
        this.property = type.extractProperty(partToUse);
    }

    private String detectAndSetIgnoreCase(String part) {

        Matcher matcher = IGNORE_CASE.matcher(part);
        String result = part;

        if (matcher.find()) {
            ignoreCase = IgnoreCaseType.ALWAYS;
            result = part.substring(0, matcher.start()) + part.substring(matcher.end(), part.length());
        }

        return result;
    }

    public boolean getParameterRequired() {

        return getNumberOfArguments() > 0;
    }

    public int getNumberOfArguments() {

        return type.getNumberOfArguments();
    }

    public String getProperty() {

        return property;
    }

    public Part.Type getType() {

        return type;
    }

    public IgnoreCaseType shouldIgnoreCase() {

        return ignoreCase;
    }
    @Override
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }

        if (obj == null || !getClass().equals(obj.getClass())) {
            return false;
        }

        Part that = (Part) obj;
        return this.property.equals(that.property) && this.type.equals(that.type);
    }

    @Override
    public int hashCode() {

        int result = 37;
        result += 17 * property.hashCode();
        result += 17 * type.hashCode();
        return result;
    }

    @Override
    public String toString() {

        return String.format("%s %s", property, type);
    }

    public static enum Type {

        BETWEEN(2, "IsBetween", "Between"), IS_NOT_NULL(0, "IsNotNull", "NotNull"), IS_NULL(0, "IsNull", "Null"), LESS_THAN(
                "IsLessThan", "LessThan"), LESS_THAN_EQUAL("IsLessThanEqual", "LessThanEqual"), GREATER_THAN("IsGreaterThan",
                "GreaterThan"), GREATER_THAN_EQUAL("IsGreaterThanEqual", "GreaterThanEqual"), BEFORE("IsBefore", "Before"), AFTER(
                "IsAfter", "After"), NOT_LIKE("IsNotLike", "NotLike"), LIKE("IsLike", "Like"), STARTING_WITH("IsStartingWith",
                "StartingWith", "StartsWith"), ENDING_WITH("IsEndingWith", "EndingWith", "EndsWith"), NOT_CONTAINING(
                "IsNotContaining", "NotContaining", "NotContains"), CONTAINING("IsContaining", "Containing", "Contains"), NOT_IN(
                "IsNotIn", "NotIn"), IN("IsIn", "In"), NEAR("IsNear", "Near"), WITHIN("IsWithin", "Within"), REGEX(
                "MatchesRegex", "Matches", "Regex"), EXISTS(0, "Exists"), TRUE(0, "IsTrue", "True"), FALSE(0, "IsFalse",
                "False"), NEGATING_SIMPLE_PROPERTY("IsNot", "Not"), SIMPLE_PROPERTY("Is", "Equals");

        // Need to list them again explicitly as the order is important
        // (esp. for IS_NULL, IS_NOT_NULL)
        private static final List<Part.Type> ALL = Arrays.asList(IS_NOT_NULL, IS_NULL, BETWEEN, LESS_THAN, LESS_THAN_EQUAL,
                GREATER_THAN, GREATER_THAN_EQUAL, BEFORE, AFTER, NOT_LIKE, LIKE, STARTING_WITH, ENDING_WITH, NOT_CONTAINING,
                CONTAINING, NOT_IN, IN, NEAR, WITHIN, REGEX, EXISTS, TRUE, FALSE, NEGATING_SIMPLE_PROPERTY, SIMPLE_PROPERTY);

        public static final Collection<String> ALL_KEYWORDS;

        static {
            List<String> allKeywords = new ArrayList<String>();
            for (Type type : ALL) {
                allKeywords.addAll(type.keywords);
            }
            ALL_KEYWORDS = Collections.unmodifiableList(allKeywords);
        }

        private final List<String> keywords;
        private final int numberOfArguments;

        private Type(int numberOfArguments, String... keywords) {

            this.numberOfArguments = numberOfArguments;
            this.keywords = Arrays.asList(keywords);
        }

        private Type(String... keywords) {
            this(1, keywords);
        }

        public static Part.Type fromProperty(String rawProperty) {

            for (Part.Type type : ALL) {
                if (type.supports(rawProperty)) {
                    return type;
                }
            }

            return SIMPLE_PROPERTY;
        }

        public Collection<String> getKeywords() {
            return Collections.unmodifiableList(keywords);
        }

        protected boolean supports(String property) {

            if (keywords == null) {
                return true;
            }

            for (String keyword : keywords) {
                if (property.endsWith(keyword)) {
                    return true;
                }
            }

            return false;
        }

        public int getNumberOfArguments() {
            return numberOfArguments;
        }

        public String extractProperty(String part) {

            String candidate = StringUtils.uncapitalize(part);

            for (String keyword : keywords) {
                if (candidate.endsWith(keyword)) {
                    return candidate.substring(0, candidate.length() - keyword.length());
                }
            }

            return candidate;
        }

        @Override
        public String toString() {
            return String.format("%s (%s): %s", name(), getNumberOfArguments(), getKeywords());
        }
    }

    public enum IgnoreCaseType {

        NEVER,

        ALWAYS,
        
        WHEN_POSSIBLE
    }
}