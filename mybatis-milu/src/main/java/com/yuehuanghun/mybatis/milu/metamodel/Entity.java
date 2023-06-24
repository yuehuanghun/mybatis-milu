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
package com.yuehuanghun.mybatis.milu.metamodel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.GenerationType;

import org.apache.ibatis.reflection.invoker.Invoker;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import com.yuehuanghun.mybatis.milu.annotation.ExampleQuery.MatchType;
import com.yuehuanghun.mybatis.milu.annotation.JoinMode;
import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.data.Part;
import com.yuehuanghun.mybatis.milu.filler.Filler;
import com.yuehuanghun.mybatis.milu.metamodel.ref.Reference;
import com.yuehuanghun.mybatis.milu.tool.converter.ExampleQueryConverter;
import com.yuehuanghun.mybatis.milu.tool.logicdel.LogicDeleteProvider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Entity {
	
	private String name;
	
	private String tableName;
	
	private String catalog;
	
	private String schema;
	
	private final Map<String, Attribute> attributeMap = new LinkedHashMap<>();

	private boolean hasId;
	
	private boolean hasVersion;
	
	private VersionAttribute version;
	
	private List<LogicDeleteAttribute> logicDeleteAttributes = new ArrayList<>(2);
	
	private IdAttribute id;
	
	private final Map<String, Reference> referenceMap = new HashMap<>();
	
	private Class<?> javaType;
	
	private final List<Filler> onUpdateFillers = new ArrayList<>();

	private final List<Filler> onInsertFillers = new ArrayList<>();
	
	private final Map<String, FetchRef> exampleQueryFetchRefs = new HashMap<>();
	
	private boolean filterLogicDeletedData = true;
	
	public void addAttribute(Attribute attribute) {
		attributeMap.put(attribute.getName(), attribute);
		if(IdAttribute.class.isInstance(attribute)) {
			id = (IdAttribute)attribute;
			hasId = true;
		}
		
		if(VersionAttribute.class.isInstance(attribute)) {
			version = (VersionAttribute)attribute;
			hasVersion = true;
		}
		
		if(LogicDeleteAttribute.class.isInstance(attribute)) {
			logicDeleteAttributes.add((LogicDeleteAttribute) attribute);
		}
	}
	
	public boolean hasAttribute(String name) {
		return attributeMap.containsKey(name);
	}
	
	public Attribute getAttribute(String name) {
		return attributeMap.get(name);
	}
	
	public Collection<Attribute> getAttributes(){
		return Collections.unmodifiableCollection(attributeMap.values());
	}
	
	public void addReference(String attributeName, Reference reference) {
		referenceMap.put(attributeName, reference);
	}
	
	public Reference getReference(String fieldName) {
		return referenceMap.get(fieldName);
	}
	
	public void addFetchRef(String group, String[] refAttrs, JoinMode joinMode) {
		exampleQueryFetchRefs.put(group, new FetchRef(refAttrs, joinMode));
	}
	
	public FetchRef getFetchRefs(String group) {
		return exampleQueryFetchRefs.get(group);
	}
	
	public boolean isFilterLogicDeletedData() {
		return filterLogicDeletedData && !logicDeleteAttributes.isEmpty();
	}
	
	@Data
	public static class Attribute {
		
		private String name;
		
		private String columnName;
		//所属实体
		private Entity owner;
		
		private Field field;
		
		private Class<?> javaType;
		
		private Class<? extends TypeHandler<?>> typeHandler;
		
		private JdbcType jdbcType;
		
		private boolean nullable = true;
		
		private boolean updateable = true;
		
		private boolean insertable = true;
		
		private boolean selectable = true;
		//目前没有什么用
		private boolean optional;
		//关联属性，外键属性
		private boolean reference;
		//实体类，仅reference为true有值，表示外键的实体类
		private Class<?> entityClass;
		
		private MatchType exampleMatchType = MatchType.EQUAL;
		
		private Mode updateMode = Mode.NOT_NULL;
		
		private Mode conditionMode = Mode.NOT_EMPTY;
		
		private List<RangeCondition> rangeList;
		
		public boolean isId() {
			return false;
		}
		
		public boolean isVersion() {
			return false;
		}
		
		public boolean isAssociation() {
			return false;
		}
		
		public boolean isCollection() {
			return false;
		}
		
		public boolean isSelectable() {
			return selectable;
		}
		
		public boolean isConditionable() {
			return isSelectable();
		}
		
		public boolean isInsertable() {
			return insertable;
		}
		
		public boolean isUpdateable() {
			return updateable;
		}
		
		public String toParameter() {
			return toParameter(getName());
		}
		
		public String toParameter(String name) {
			String param = name;
			if(getJdbcType() != null) {
				param += ",jdbcType=" + getJdbcType().name();
			}
			if(getTypeHandler() != null) {
				param += ",typeHandler=" + getTypeHandler().getName();
			}
			return param;
		}
		
		public String formatParameterExpression(String expression) {
			if(getJdbcType() == null && getTypeHandler() == null) {
				return expression;
			}
			
			String tail = "";
			if(getJdbcType() != null) {
				tail += ",jdbcType=" + getJdbcType().name();
			}
			if(getTypeHandler() != null) {
				tail += ",typeHandler=" + getTypeHandler().getName();
			}
			
			Pattern pattern = Pattern.compile("#\\{.+?}");
			Matcher matcher = pattern.matcher(expression);
			
			Set<String> paramSet = new HashSet<>();
			while(matcher.find()) { // 提取#{xxx}的参数
				paramSet.add(matcher.group());
			}
			
			for(String param : paramSet) {
				String formatParam = param.substring(0, param.length() - 1) + tail + "}";
				expression = expression.replace(param, formatParam);
			}
			return expression;
		}
	}
	
	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class IdAttribute extends Attribute {
		
		private GenerationType generationType;
		
		private String generator;

		@Override
		public boolean isId() {
			return true;
		}

		@Override
		public boolean isUpdateable() {
			return false;
		}
	}
	
	public static class VersionAttribute extends Attribute {
		@Override
		public boolean isVersion() {
			return true;
		}
	}
	
	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class PluralAttribute extends Attribute {
		private Class<?> elementClass;

		@Override
		public boolean isCollection() {
			return true;
		}
	}
	
	public static class AssociationAttribute extends Attribute {
		@Override
		public boolean isAssociation() {
			return true;
		}
	}
	
	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class LogicDeleteAttribute extends Attribute {
		/** 属性设置器 */
		@Getter
		private Invoker setter;
		/** 逻辑删除值提供器 */
		private LogicDeleteProvider provider;
		/**
		 * 主逻辑删除属性<br>
		 * 在Criteria查询中，使用undeleted()或deleted()设定查询条件时，当为主删除属性时才会被用于查询条件中<br>
		 * 举例：获取未被逻辑删除的数据：someMapper.findByCriteria(p -> p.undeleted());
		 * 
		 */
		private boolean main;
	}
	
	@Data
	@AllArgsConstructor
	public static class RangeCondition {
		//键名表达式
		private String keyName;
		
		private Part.Type type;
		//对应属性的类型
		private Class<?> attrJavaType;
		//传转换器
		private Class<? extends ExampleQueryConverter> valueConverter;
		
		private KeyType keyType;
	}
}
