/*
 * Copyright 2020-current the original author or authors.
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
import com.yuehuanghun.mybatis.milu.metamodel.Entity.FunctionAttribute;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Index;
import com.yuehuanghun.mybatis.milu.metamodel.ref.Reference;
import com.yuehuanghun.mybatis.milu.tool.Assert;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VEntity {
	/** 实体名。作标识使用 */
	private String name;
	/** 对应的数据库表名 */
	private String tableName;
	/** 数据表所在的数据库catalog，可空。 */
	private String catalog;
	/** 数据表所在的数据库schema，可空。 */
	private String schema;
	/** 属性与表字段的映射与配置，不可空 */
	private List<Attribute> attributes = new ArrayList<>();
	/** 外键引用，如果需要表间关联查询则需要设置 */
	private final Map<String, Reference> referenceMap = new HashMap<>();
	/** 结果集绑定的POJO类，即实体类，可空，默认为Map */
	private Class<?> javaType;
	/** 更新时自动填充配置，可空 */
	private final List<Filler> onUpdateFillers = new ArrayList<>();
	/** 新增时自动填充配置，可空 */
	private final List<Filler> onInsertFillers = new ArrayList<>();
	/** 索引信息，可空，使用batchMerge功能时需要。 */
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
		
		attributes.forEach(attr -> {
			Assert.notBlank(attr.getName(), "属性名name不能为空");
			if(attr instanceof FunctionAttribute) {
				if(StringUtils.isBlank(attr.getColumnName())) {
					attr.setColumnName(attr.getName());
				}
			} else {
				Assert.notBlank(attr.getColumnName(), "属性映射数据表字段名columnName值不能为空");
			}
			entity.addAttribute(attr);
		});
		referenceMap.forEach((attr, ref) -> entity.addReference(attr, ref));
		onUpdateFillers.forEach(filler -> entity.getOnUpdateFillers().add(filler));
		onInsertFillers.forEach(filler -> entity.getOnInsertFillers().add(filler));
		indexMap.forEach((name, index) -> entity.getIndexMap().put(name, index));
		
		return entity;
	}
}
