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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yuehuanghun.mybatis.milu.filler.Filler;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Index;
import com.yuehuanghun.mybatis.milu.metamodel.ref.Reference;
import com.yuehuanghun.mybatis.milu.tool.Assert;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VEntity {
	
	private String name;
	
	private String tableName;
	
	private String catalog;
	
	private String schema;
	
	private List<Attribute> attributes = new ArrayList<>();
	
	private final Map<String, Reference> referenceMap = new HashMap<>();
	
	private Class<?> javaType;
	
	private final List<Filler> onUpdateFillers = new ArrayList<>();

	private final List<Filler> onInsertFillers = new ArrayList<>();
	
	private final Map<String, Index> indexMap = new HashMap<>();
	
	public void addAttribute(Attribute attribute) {
		attributes.add(attribute);
	}
	
	public void addReference(String attributeName, Reference reference) {
		referenceMap.put(attributeName, reference);
	}
	
	// 用于merge操作
	public void addIndex(String indexName, Attribute attr) {
		Index index = indexMap.computeIfAbsent(indexName, key -> new Index(indexName, new ArrayList<>()));
		if(!index.getAttrs().contains(attr)) {
			index.getAttrs().add(attr);
		}
	}
	
	public void addOnUpdateFillers(Filler filler) {
		onUpdateFillers.add(filler);
	}
	
	public void addOnInsertFillers(Filler filler) {
		onInsertFillers.add(filler);
	}
	
	public Entity toEntity() {
		Assert.notBlank(tableName, "实体表名不能为空");
		Assert.notEmpty(attributes, "实体属性不能为空");
		Entity entity = new Entity();
		
		entity.setName(name);
		entity.setTableName(tableName);
		entity.setCatalog(catalog);
		entity.setSchema(schema);
		entity.setJavaType(javaType);
		
		attributes.forEach(attr -> entity.addAttribute(attr));
		referenceMap.forEach((attr, ref) -> entity.addReference(attr, ref));
		onUpdateFillers.forEach(filler -> entity.getOnUpdateFillers().add(filler));
		onInsertFillers.forEach(filler -> entity.getOnInsertFillers().add(filler));
		indexMap.forEach((name, index) -> entity.getIndexMap().put(name, index));
		
		return entity;
	}
}
