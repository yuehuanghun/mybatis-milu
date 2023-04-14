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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yuehuanghun.mybatis.milu.data.Part.Type;
import com.yuehuanghun.mybatis.milu.data.PartTree.OrPart;
import com.yuehuanghun.mybatis.milu.tool.Assert;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

/**
 * 查询片断树
 * reference org.springframework.data.repository.query.parser.PartTree
 * 
 * @author yuehuanghun
 */
public class PartTree implements Iterable<OrPart> {

    /*
         *  搜索一种模式：关键字后跟一个大写字母，该字母有一个小写变体\p{Lu}或任何其他不在基本拉丁统一代码块\\p{InBASIC{LATIN}中的字母（如汉语、韩语、日语等）。
     * @see http://www.regular-expressions.info/unicode.html
     * @see http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html#ubc
     */
    private static final String KEYWORD_TEMPLATE = "(%s)(?=(\\p{Lu}|\\P{InBASIC_LATIN}))";
    private static final String QUERY_PATTERN = "find|read|get|query|stream";
    private static final String COUNT_PATTERN = "count";
    private static final String DELETE_PATTERN = "delete|remove";
    private static final String STATISTIC_PATTERN = "sum|count|min|max|avg";
    private static final Pattern PREFIX_TEMPLATE = Pattern.compile( //
            "^(" + QUERY_PATTERN + "|" + COUNT_PATTERN + "|" + DELETE_PATTERN + "|" + STATISTIC_PATTERN + ")((\\p{Lu}.*?))??By");
    private static final Pattern STATISTIC_TEMPLATE = Pattern.compile("(?=Sum|Count|Min|Max|Avg)(\\p{Lu}.*?)??");

	/**
	 * 目标, 例如 "findDistinctUserByNameOrderByAge" 将有 subject "DistinctUser".
	 */
    private final Subject subject;

	/**
	 * 过滤参数表达式, for example "findDistinctUserByNameOrderByAge" would have the predicate "NameOrderByAge".
	 */
    private final Predicate predicate;
    /**
     * 统计数据声明
     */
    private final List<StatisticPart> statisticParts =  new ArrayList<>();

    public PartTree(String source, Class<?> domainClass) {

        Assert.notNull(source, "Source must not be null");
        Assert.notNull(domainClass, "Domain class must not be null");

        Matcher matcher = PREFIX_TEMPLATE.matcher(source);
        String matchStr = null;
        if (!matcher.find()) {
            this.subject = new Subject(null);
            this.predicate = new Predicate(source, domainClass);
        } else {
        	matchStr = matcher.group(0);
            this.subject = new Subject(matchStr);
            this.predicate = new Predicate(source.substring(matcher.group().length()), domainClass);
        }
        
        if(subject.isStatistic() && matchStr != null) { //matcher一定能找到
        	buildStatisticParts(matchStr.substring(0, matchStr.length() - 2), domainClass); //去掉末尾By
        }
    }
    public Iterator<OrPart> iterator() {
        return predicate.iterator();
    }
    
    public Sort getSort() {

        OrderBySource orderBySource = predicate.getOrderBySource();
        return orderBySource == null ? null : orderBySource.toSort();
    }

    public boolean isDistinct() {
        return subject.isDistinct();
    }

    public Boolean isCountProjection() {
        return subject.isCountProjection();
    }

    public Boolean isDelete() {
        return subject.isDelete();
    }
    
    public Boolean isStatistic() {
    	return subject.isStatistic();
    }

    public boolean isLimiting() {
        return getMaxResults() != null;
    }

    public Integer getMaxResults() {
        return subject.getMaxResults();
    }
    
    public List<String> getGroupProperties(){
    	return predicate.groupBySource == null ? Collections.emptyList() : predicate.getGroupBySource().getGroupProperties();
    }
    
    public List<StatisticPart> getStatisticParts() {
		return statisticParts;
	}
    
	public Iterable<Part> getParts() {

        List<Part> result = new ArrayList<Part>();
        for (OrPart orPart : this) {
            for (Part part : orPart) {
                result.add(part);
            }
        }
        return result;
    }

    public Iterable<Part> getParts(Type type) {

        List<Part> result = new ArrayList<Part>();

        for (Part part : getParts()) {
            if (part.getType().equals(type)) {
                result.add(part);
            }
        }

        return result;
    }

    @Override
    public String toString() {

        OrderBySource orderBySource = predicate.getOrderBySource();
        GroupBySource groupBySource = predicate.getGroupBySource();
        return String.format("%s%s%s%s", StringUtils.collectionToDelimitedString(statisticParts, ", "), 
        		StringUtils.collectionToDelimitedString(predicate.nodes, " or "),
        		groupBySource == null ? "" : " " + groupBySource,
                orderBySource == null ? "" : " " + orderBySource);
    }

    private static String[] split(String text, String keyword) {

        Pattern pattern = Pattern.compile(String.format(KEYWORD_TEMPLATE, keyword));
        return pattern.split(text);
    }
    
    private void buildStatisticParts(String source, Class<?> domainClass) {
    	String[] splits = STATISTIC_TEMPLATE.split(source);
    	Arrays.stream(splits).forEach(part -> {
    		statisticParts.add(new StatisticPart(part, domainClass));
    	});
    }

    public static class OrPart implements Iterable<Part> {

        private final List<Part> children = new ArrayList<Part>();

        OrPart(String source, Class<?> domainClass, boolean alwaysIgnoreCase) {

            String[] split = split(source, "And");
            for (String part : split) {
                if (StringUtils.isNotBlank(part)) {
                    children.add(new Part(part, domainClass, alwaysIgnoreCase));
                }
            }
        }

        public Iterator<Part> iterator() {

            return children.iterator();
        }

        @Override
        public String toString() {

            return StringUtils.collectionToDelimitedString(children, " and ");
        }
    }

    private static class Subject {

        private static final String DISTINCT = "Distinct";
        private static final Pattern COUNT_BY_TEMPLATE = Pattern.compile("^countBy"); //原：^count(\\p{Lu}.*?)??By
        private static final Pattern DELETE_BY_TEMPLATE = Pattern.compile("^(" + DELETE_PATTERN + ")(\\p{Lu}.*?)??By");
        private static final Pattern STATISTIC_BY_TEMPLATE = Pattern.compile("^(" + STATISTIC_PATTERN + ")(\\p{Lu}.*?)+?By");
        private static final String LIMITING_QUERY_PATTERN = "(First|Top)(\\d*)?";
        private static final Pattern LIMITED_QUERY_TEMPLATE = Pattern.compile("^(" + QUERY_PATTERN + ")(" + DISTINCT + ")?"
                + LIMITING_QUERY_PATTERN + "(\\p{Lu}.*?)??By");

        private final boolean distinct;
        private final boolean count;
        private final boolean delete;
        private final boolean statistic;
        private final Integer maxResults;

        public Subject(String subject) {

            this.distinct = subject == null ? false : subject.contains(DISTINCT);
            this.count = matches(subject, COUNT_BY_TEMPLATE);
            this.delete = matches(subject, DELETE_BY_TEMPLATE);
            this.statistic = matches(subject, STATISTIC_BY_TEMPLATE);
            this.maxResults = returnMaxResultsIfFirstKSubjectOrNull(subject);
        }

        private Integer returnMaxResultsIfFirstKSubjectOrNull(String subject) {

            if (subject == null) {
                return null;
            }

            Matcher grp = LIMITED_QUERY_TEMPLATE.matcher(subject);

            if (!grp.find()) {
                return null;
            }

            return StringUtils.isNotBlank(grp.group(4)) ? Integer.valueOf(grp.group(4)) : 1;
        }

        public Boolean isDelete() {
            return delete;
        }

        public boolean isCountProjection() {
            return count;
        }

        public boolean isDistinct() {
            return distinct;
        }
        
        public boolean isStatistic() {
        	return statistic;
        }

        public Integer getMaxResults() {
            return maxResults;
        }

        private final boolean matches(String subject, Pattern pattern) {
            return subject == null ? false : pattern.matcher(subject).find();
        }
    }

    private static class Predicate {

        private static final Pattern ALL_IGNORE_CASE = Pattern.compile("AllIgnor(ing|e)Case");
        private static final String ORDER_BY = "OrderBy";
        private static final String GROUP_BY = "GroupBy";

        private final List<OrPart> nodes = new ArrayList<OrPart>();
        private final OrderBySource orderBySource;
        private final GroupBySource groupBySource;
        private boolean alwaysIgnoreCase;

        public Predicate(String predicate, Class<?> domainClass) {

            String[] parts = split(detectAndSetAllIgnoreCase(predicate), ORDER_BY);

            if (parts.length > 2) {
                throw new IllegalArgumentException("OrderBy must not be used more than once in a method name!");
            }

            String[] subParts = split(parts[0], GROUP_BY);
            if (subParts.length > 2) {
                throw new IllegalArgumentException("GroupBy must not be used more than once in a method name!");
            }
            buildTree(subParts[0], domainClass);
            this.orderBySource = parts.length == 2 ? new OrderBySource(parts[1], domainClass) : null;
            this.groupBySource = subParts.length == 2 ? new GroupBySource(subParts[1], domainClass) : null;
        }

        private String detectAndSetAllIgnoreCase(String predicate) {

            Matcher matcher = ALL_IGNORE_CASE.matcher(predicate);

            if (matcher.find()) {
                alwaysIgnoreCase = true;
                predicate = predicate.substring(0, matcher.start()) + predicate.substring(matcher.end(), predicate.length());
            }

            return predicate;
        }

        private void buildTree(String source, Class<?> domainClass) {
        	if(StringUtils.isBlank(source)) {
        		return;
        	}
            String[] split = split(source, "Or");
            for (String part : split) {
                nodes.add(new OrPart(part, domainClass, alwaysIgnoreCase));
            }
        }

        public Iterator<OrPart> iterator() {
            return nodes.iterator();
        }

        public OrderBySource getOrderBySource() {
            return orderBySource;
        }
        
        public GroupBySource getGroupBySource() {
        	return groupBySource;
        }
    }
}
