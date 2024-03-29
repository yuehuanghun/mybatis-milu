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

import java.util.Collection;
import java.util.function.Consumer;

import com.yuehuanghun.mybatis.milu.annotation.LogicDelete;
import com.yuehuanghun.mybatis.milu.annotation.Mode;

/**
 * 查询逻辑条件
 * @see Predicates
 * @author yuehuanghun
 *
 */
public interface Predicate extends Condition {

	public static enum Logic {
		AND, OR, NOT
	}
	
	/**
	 * 设置默认的条件生效模式，默认为Mode.NOT_EMPTY
	 * @param conditionMode 条件生效模式
	 * @return 当前对象
	 */
	Predicate conditionMode(Mode conditionMode);
	
	/**
	 * 与一组查询条件：AND (condition1 AND condition2 [AND conditionN])
	 * @param conditions 条件，可通过{@link Conditions} 构建新条件
	 * @return 当前对象
	 */
	Predicate and(Condition... conditions);
	
	/**
	 * 给定一个Predicate进行设置
	 * @param predicate 条件
	 * @return 当前对象
	 */
	Predicate and(Consumer<Predicate> predicate);

	/**
	 * 或一组查询：OR (condition1 AND condition2 [AND conditionN])
	 * @param conditions 条件，可通过{@link Conditions} 构建新条件
	 * @return 当前对象
	 */
	Predicate or(Condition... conditions);
	
	/**
	 * 给定一个Predicate进行设置
	 * @param predicate 条件
	 * @return 当前对象
	 */
	Predicate or(Consumer<Predicate> predicate);
	
	/**
	 * 否一组查询：NOT (condition1 AND condition2 [AND conditionN])
	 * @param conditions 条件，可通过{@link Conditions} 构建新条件
	 * @return 当前对象
	 */
	Predicate not(Condition... conditions);
	
	/**
	 * 给定一个Predicate进行设置
	 * @param predicate 条件
	 * @return 当前对象
	 */
	Predicate not(Consumer<Predicate> predicate);

	/**
	 * 增加一个值相等查询条件，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return 当前对象
	 */
	Predicate eq(String attrName, Object value);
	
	/**
	 * 增加一个值相等查询条件
	 * @param accept 当值为true时，条件生效
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return 当前对象
	 */
	Predicate eq(boolean accept, String attrName, Object value);
	
	/**
	 * 增加一个值不等查询条件，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return 当前对象
	 */
	Predicate neq(String attrName, Object value);
	
	/**
	 * 增加一个值不等查询条件
	 * @param accept 当值为true时，条件生效
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return 当前对象
	 */
	Predicate neq(boolean accept, String attrName, Object value);

	/**
	 * 增加一个值小于查询条件，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return 当前对象
	 */
	Predicate lt(String attrName, Object value);
	
	/**
	 * 增加一个值小于查询条件
	 * @param accept 当值为true时，条件生效
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return 当前对象
	 */
	Predicate lt(boolean accept, String attrName, Object value);

	/**
	 * 增加一个值小于等于查询条件，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return 当前对象
	 */
	Predicate lte(String attrName, Object value);
	
	/**
	 * 增加一个值小于等于查询条件
	 * @param accept 当值为true时，条件生效
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return 当前对象
	 */
	Predicate lte(boolean accept, String attrName, Object value);

	/**
	 * 增加一个值大于查询条件，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return 当前对象
	 */
	Predicate gt(String attrName, Object value);
	
	/**
	 * 增加一个值大于查询条件
	 * @param accept 当值为true时，条件生效
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return 当前对象
	 */
	Predicate gt(boolean accept, String attrName, Object value);

	/**
	 * 增加一个值大于等于查询条件，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return 当前对象
	 */
	Predicate gte(String attrName, Object value);
	
	/**
	 * 增加一个值大于等于查询条件
	 * @param accept 当值为true时，条件生效
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return 当前对象
	 */
	Predicate gte(boolean accept, String attrName, Object value);

	/**
	 * 增加一个匹配查询条件，注：该查询值不会自动加匹配符，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param attrName 查询属性名
	 * @param value 值应包括匹配符
	 * @return 当前对象
	 */
	Predicate like(String attrName, Object value);
	
	/**
	 * 增加一个匹配查询条件，注：该查询值不会自动加匹配符
	 * @param accept 当值为true时，条件生效
	 * @param attrName 查询属性名
	 * @param value 值应包括匹配符
	 * @return 当前对象
	 */
	Predicate like(boolean accept, String attrName, Object value);

	/**
	 * 增加一个非匹配查询条件，注：该查询值不会自动加匹配符，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param attrName 查询属性名
	 * @param value 值应包括匹配符
	 * @return 当前对象
	 */
	Predicate notLike(String attrName, Object value);

	/**
	 * 增加一个非匹配查询条件，注：该查询值不会自动加匹配符
	 * @param accept 当值为true时，条件生效
	 * @param attrName 查询属性名
	 * @param value 值应包括匹配符
	 * @return 当前对象
	 */
	Predicate notLike(boolean accept, String attrName, Object value);
	
	/**
	 * 增加一个包含值查询条件，在值前后增加%，column LIKE CONCAT('%', value, '%')，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return 当前对象
	 */
	Predicate contain(String attrName, Object value);
	
	/**
	 * 增加一个包含值查询条件，在值前后增加%，column LIKE CONCAT('%', value, '%')
	 * @param accept 当值为true时，条件生效
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return 当前对象
	 */
	Predicate contain(boolean accept, String attrName, Object value);

	/**
	 * 增加一个非包含值查询条件，在值前后增加%，column NOT LIKE CONCAT('%', value, '%')，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return 当前对象
	 */
	Predicate notContain(String attrName, Object value);
	
	/**
	 * 增加一个非包含值查询条件，在值前后增加%，column NOT LIKE CONCAT('%', value, '%')
	 * @param accept 当值为true时，条件生效
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return 当前对象
	 */
	Predicate notContain(boolean accept, String attrName, Object value);

	/**
	 * 增加一个左匹配查询条件，在值后面增加%，column LIKE CONCAT(value, '%')，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return 当前对象
	 */
	Predicate startWith(String attrName, Object value);

	/**
	 * 增加一个左匹配查询条件，在值后面增加%，column LIKE CONCAT(value, '%')
	 * @param accept 当值为true时，条件生效
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return 当前对象
	 */
	Predicate startWith(boolean accept, String attrName, Object value);
	
	/**
	 * 增加一个右匹配查询条件，在值前面增加%，column LIKE CONCAT('%', value)，默认当value不为空时条件生效，通过设置conditionMode变更生效方式。（为空指值为null，字符串时空串，集合/数组时为0个元素）
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return 当前对象
	 */
	Predicate endWith(String attrName, Object value);
	
	/**
	 * 增加一个右匹配查询条件，在值前面增加%，column LIKE CONCAT('%', value)
	 * @param accept 当值为true时，条件生效
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return 当前对象
	 */
	Predicate endWith(boolean accept, String attrName, Object value);

	/**
	 * 增加一个范围匹配查询条件， column BETWEEN startValue AND endValue<br>
	 * startValue和endValue不允许为null，如果可能为null值，可使用gte、lte替代
	 * @param attrName 查询属性名
	 * @param startValue 开始值
	 * @param endValue 结束值 
	 * @return 当前对象
	 */
	Predicate between(String attrName, Object startValue, Object endValue);
	
	/**
	 * 增加一个范围匹配查询条件， column BETWEEN startValue AND endValue。<br>
	 * startValue和endValue不允许为null，如果可能为null值，可使用gte、lte替代
	 * @param accept 当值为true时，条件生效
	 * @param attrName 查询属性名
	 * @param startValue 开始值
	 * @param endValue 结束值 
	 * @return 当前对象
	 */
	Predicate between(boolean accept, String attrName, Object startValue, Object endValue);

	/**
	 * 增加一个值非空查询条件
	 * @param attrName 查询属性名
	 * @return 当前对象
	 */
	Predicate isNull(String attrName);

	/**
	 * 增加一个值非空查询条件
	 * @param accept 当值为true时，条件生效
	 * @param attrName 查询属性名
	 * @return 当前对象
	 */
	Predicate isNull(boolean accept, String attrName);
	
	/**
	 * 增加一个值为空查询条件
	 * @param attrName 查询属性名
	 * @return 当前对象
	 */
	Predicate notNull(String attrName);

	/**
	 * 增加一个值为空查询条件
	 * @param accept 当值为true时，条件生效
	 * @param attrName 查询属性名
	 * @return 当前对象
	 */
	Predicate notNull(boolean accept, String attrName);
	
	/**
	 * 增加一个IN查询条件，column IN (value[0],value[1]...)，当value != null并且非空集合时，条件生效
	 * @param attrName 查询属性名
	 * @param value 条件值，非null生效。当为数组或集合时，元素个数大于0时条件生效；非数组或集合时，如为字符串，则以半角逗号分割为字符串集合，其它则转为单元素集合<br>
	 * ["1","2"] -> ["1","2"]<br>
	 * "1,2" -> ["1","2"]<br>
	 * 1 -> [1]<br>
	 * "1" -> ["1"]
	 * @return 当前对象
	 */
	Predicate in(String attrName, Object value);
	
	/**
	 * 增加一个IN查询条件，column IN (value[0],value[1]...)
	 * @param accept 当值为true时，条件生效
	 * @param attrName 查询属性名
	 * @param value 条件值。值一般为数组或集合。非数组或集合时，如为字符串，则以半角逗号分割为字符串集合，其它则转为单元素集合<br>
	 * ["1","2"] -> ["1","2"]<br>
	 * "1,2" -> ["1","2"]<br>
	 * 1 -> [1]<br>
	 * "1" -> ["1"]
	 * @return 当前对象
	 */
	Predicate in(boolean accept, String attrName, Object value);
	
	/**
	 * 增加一个NOT IN查询条件，column NOT IN (value[0],value[1]...)
	 * @param attrName 查询属性名
	 * @param value 条件值，非null生效。当为数组或集合时，元素个数大于0时条件生效；非数组或集合时，如为字符串，则以半角逗号分割为字符串集合，其它则转为单元素集合<br>
	 * ["1","2"] -> ["1","2"]<br>
	 * "1,2" -> ["1","2"]<br>
	 * 1 -> [1]<br>
	 * "1" -> ["1"]
	 * @return 当前对象
	 */
	Predicate notIn(String attrName, Object value);
	
	/**
	 * 增加一个NOT IN查询条件，column NOT IN (value[0],value[1]...)
	 * @param accept 当值为true时，条件生效
	 * @param attrName 查询属性名
	 * @param value 条件值。值一般为数组或集合。非数组或集合时，如为字符串，则以半角逗号分割为字符串集合，其它则转为单元素集合<br>
	 * ["1","2"] -> ["1","2"]<br>
	 * "1,2" -> ["1","2"]<br>
	 * 1 -> [1]<br>
	 * "1" -> ["1"]
	 * @return 当前对象
	 */
	Predicate notIn(boolean accept, String attrName, Object value);
	
	/**
	 * 正则匹配
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return 当前对象
	 */
	Predicate regex(String attrName, Object value);
	
	/**
	 * 正则匹配，需要数据库支持
	 * @param accept 当值为true时，条件生效
	 * @param attrName 查询属性名
	 * @param value 值
	 * @return 当前对象
	 */
	Predicate regex(boolean accept, String attrName, Object value);
	
	/**
	 * 增加查询未被逻辑删除的数据的查询条件
	 * @see LogicDelete
	 * @return 当前对象
	 */
	Predicate undeleted();
	
	/**
	 * 增加查询已被逻辑删除的数据的查询条件
	 * @see LogicDelete
	 * @return 当前对象
	 */
	Predicate deleted();
	
	/**
	 * 条件集合是否为空
	 * @return true/false
	 */
	boolean isEmpty();

	/**
	 * 将Example样例条件将为Predicate动态条件<br>
	 * 继承example的匹配模式与范围查询条件
	 * @param example example必须为实体类的实例
	 * @return 当前对象
	 */
	Predicate byExample(Object example);
	
	/**
	 * 全文搜索<br>
	 * 对应数据库可能需要安装相应插件、启用插件、建立全文索引才可用<br>
	 * 由于不同的数据库全文搜索的表达方式差别大，所以如果切换到其它类型数据库，可能要调整查询参数
	 * 
	 * @param attrNames 全文索引属性（列）
	 * @param keywordExpression 关键字表达式，不同的数据库的表达式不尽相同，请注意区分
	 * @return 当前对象
	 */
	Predicate fulltext(Collection<String> attrNames, String keywordExpression);
	
	/**
	 * 全文搜索<br>
	 * 对应数据库可能需要安装相应插件、启用插件、建立全文索引才可用<br>
	 * 由于不同的数据库全文搜索的表达方式差别大，所以如果切换到其它类型数据库，可能要调整查询参数
	 * 
	 * @param attrNames 全文索引属性（列）
	 * @param keywordExpression 关键字表达式，不同的数据库的表达式不尽相同，请注意区分
	 * @param fulltextMode 全文索引模式
	 * @return 当前对象
	 */
	Predicate fulltext(Collection<String> attrNames, String keywordExpression, FulltextMode fulltextMode);

	/**
	 * 增加exist子查询条件
	 * @param exists exists字查询
	 * @return 当前对象
	 */
	Predicate exists(Exists<?> exists);

	/**
	 * 增加not exist子查询条件
	 * @param exists exists子查询
	 * @return 当前对象
	 */
	Predicate notExists(Exists<?> exists);
	
	/**
	 * 是否存在exists查询条件
	 * @return true/false
	 */
	boolean hasExistsCondition();
}
