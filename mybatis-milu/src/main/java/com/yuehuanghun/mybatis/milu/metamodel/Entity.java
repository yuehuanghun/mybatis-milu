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
package com.yuehuanghun.mybatis.milu.metamodel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.GenerationType;

import com.yuehuanghun.mybatis.milu.annotation.ExampleQuery.MatchType;
import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.data.Part;
import com.yuehuanghun.mybatis.milu.filler.Filler;
import com.yuehuanghun.mybatis.milu.metamodel.ref.Reference;

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
	
	private final Map<String, Attribute> attributeMap = new LinkedHashMap<>();

	private boolean hasId;
	
	private boolean hasVersion;
	
	private VersionAttribute version;
	
	private IdAttribute id;
	
	private final Map<String, Reference> referenceMap = new HashMap<>();
	
	private Class<?> javaType;
	
	private final List<Filler> onUpdateFillers = new ArrayList<>();

	private final List<Filler> onInsertFillers = new ArrayList<>();
	
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
	
	@Data
	public static class Attribute {
		
		private String name;
		
		private String columnName;
		
		private Field field;
		
		private Class<?> javaType;
		
		private boolean nullable = true;
		
		private boolean updateable = true;
		
		private boolean insertable = true;
		
		private boolean selectable = true;
		
		private boolean optional;
		
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
			return selectable && !isAssociation() && !isCollection();
		}
		
		public boolean isConditionable() {
			return !isAssociation() && !isCollection();
		}
		
		public boolean isInsertable() {
			return insertable && !isAssociation() && !isCollection();
		}
		
		public boolean isUpdateable() {
			return updateable && !isAssociation() && !isCollection() && !isId();
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
	@AllArgsConstructor
	public static class RangeCondition {
		
		private String keyName;
		
		private Part.Type type;
	}
}
