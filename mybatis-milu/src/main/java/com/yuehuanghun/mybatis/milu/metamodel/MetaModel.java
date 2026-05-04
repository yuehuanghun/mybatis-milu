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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MetaModel {
	private final Map<Class<?>, Entity> entityMap = new ConcurrentHashMap<>();
	private final Map<String, Entity> idEntityMap = new ConcurrentHashMap<>();
	private final List<Entity> entities = new ArrayList<>();
	
	public boolean hasEntity(Class<?> entityClass) {
		return entityMap.containsKey(entityClass);
	}
	
	public boolean hasEntity(Entity entity) {
		return entities.contains(entity);
	}
	
	public Entity getEntity(Class<?> entityClass) {
		return entityMap.get(entityClass);
	}
	
	public Entity getEntity(String entityId) {
		return idEntityMap.get(entityId);
	}
	
	public void addEntity(Entity entity) {
		if(hasEntity(entity)) {
			return;
		}
		if(!Map.class.isAssignableFrom(entity.getJavaType())) {
			entityMap.put(entity.getJavaType(), entity);
		}
		idEntityMap.put(entity.getEntityId(), entity);
		entities.add(entity);
	}
	
	public Collection<Entity> getEntities(){
		return Collections.unmodifiableCollection(entities);
	}
	
	public void removeEntity(String entityId) {
		Entity removed = idEntityMap.remove(entityId);
		if(removed != null && !Map.class.isAssignableFrom(removed.getJavaType())) {
			entityMap.remove(removed.getJavaType());
		}
	}
}
