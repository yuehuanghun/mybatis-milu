/*
 * Copyright 2020-2022 the original author or authors.
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.ReflectorFactory;

import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.annotation.AttributeOptions;
import com.yuehuanghun.mybatis.milu.annotation.ExampleQuery;
import com.yuehuanghun.mybatis.milu.annotation.LogicDelete;
import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.data.Part.Type;
import com.yuehuanghun.mybatis.milu.exception.OrmBuildingException;
import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.filler.Filler;
import com.yuehuanghun.mybatis.milu.filler.SupplierHelper;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.AssociationAttribute;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.IdAttribute;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.LogicDeleteAttribute;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.PluralAttribute;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.RangeCondition;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.VersionAttribute;
import com.yuehuanghun.mybatis.milu.metamodel.ref.ManyToManyReference;
import com.yuehuanghun.mybatis.milu.metamodel.ref.MappedReference;
import com.yuehuanghun.mybatis.milu.metamodel.ref.Reference;
import com.yuehuanghun.mybatis.milu.metamodel.ref.Reference.JoinCondition;
import com.yuehuanghun.mybatis.milu.tool.InstanceUtils;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;
import com.yuehuanghun.mybatis.milu.tool.converter.ExampleQueryConverter;
import com.yuehuanghun.mybatis.milu.tool.converter.ExampleQueryConverter.AutoConverter;
import com.yuehuanghun.mybatis.milu.tool.logicdel.LogicDeleteProvider;

/**
 * 实体类构建器
 * @author yuehuanghun
 *
 */
public class EntityBuilder {
	private static Log log = LogFactory.getLog(EntityBuilder.class);
	
	private final MiluConfiguration configuration;
	private final Class<?> entityClass;
	
	private Set<Class<?>> referenceEntityClassSet = new HashSet<>();
	private static final ReflectorFactory REFLECTOR_FACTORY = new DefaultReflectorFactory();
	
	private EntityBuilder(Class<?> entityClass, MiluConfiguration configuration) {
		this.entityClass = entityClass;
		this.configuration = configuration;
	}
	
	public static EntityBuilder instance(Class<?> entityClass, MiluConfiguration configuration) {
		return new EntityBuilder(entityClass, configuration);
	}

	public void build(){
		if(configuration.getMetaModel().hasEntity(entityClass)) {
			return;
		}
		
		Entity entity = forClass(entityClass);
		
		configuration.getMetaModel().addEntity(entity);		
		
		for(Attribute attr : entity.getAttributes()) {
			Reference reference = buildReference(attr, entity);
			if(reference != null) {
				entity.addReference(attr.getName(), reference);
			}
		}
		
		for(Class<?> referenceEntityClass : referenceEntityClassSet) {
			EntityBuilder.instance(referenceEntityClass, configuration).build();
		}	
	}
	
	private Entity forClass(Class<?> entityClass) {
		Entity entity = new Entity();
		entity.setJavaType(entityClass);
		
		if(entityClass.isAnnotationPresent(Table.class)) {
			Table table = entityClass.getAnnotation(Table.class);
			entity.setTableName(table.name());
		}
		if(StringUtils.isBlank(entity.getTableName())) {
			entity.setTableName(StringUtils.camel2Underline(entityClass.getSimpleName(), true));
		}
		
		buildAttribute(entity, entityClass);
		
		return entity;
	}
	
	private void buildAttribute(Entity entity, Class<?> clazz) {
		if(clazz == Object.class) {
			return;
		}
		Field[] fields = clazz.getDeclaredFields();
		MetaClass metaClass = null;
		for(Field field : fields) {
			if(field.isAnnotationPresent(Transient.class) || entity.hasAttribute(field.getName())) {
				continue;
			}
			
			if(Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
				continue;
			}
			
			Attribute attr = forField(field);
			attr.setOwner(entity);
			entity.addAttribute(attr);

			List<RangeCondition> rangeList = null;
			AttributeOptions options = getAnnotation(field, AttributeOptions.class);
			if(options != null) {
				ExampleQuery[] exampleQuerys = options.exampleQuery();
				if(exampleQuerys.length > 0) {
					attr.setExampleMatchType(exampleQuerys[0].matchType());

					rangeList = new ArrayList<>();
					for(ExampleQuery exampleQuery : exampleQuerys) {
						Class<? extends ExampleQueryConverter> valueConverter = exampleQuery.valueConverter() == AutoConverter.class ? configuration.getDefaultExampleQueryConverter() : exampleQuery.valueConverter();
				
						String startKeyName = exampleQuery.startKeyName();
						if(StringUtils.isNotBlank(startKeyName)) {
							rangeList.add(new RangeCondition(startKeyName, exampleQuery.startValueContain() ? Type.GREATER_THAN_EQUAL : Type.GREATER_THAN, attr.getJavaType(), valueConverter, KeyType.START));
						}
						
						String endKeyName = exampleQuery.endKeyName();
						if(StringUtils.isNotBlank(endKeyName)) {
							rangeList.add(new RangeCondition(endKeyName, exampleQuery.endValueContain() ? Type.LESS_THAN_EQUAL : Type.LESS_THAN, attr.getJavaType(), valueConverter, KeyType.END));
						}
						
						String inKeyName = exampleQuery.inKeyName();
						if(StringUtils.isNotBlank(inKeyName)) {
							rangeList.add(new RangeCondition(inKeyName, Type.IN, attr.getJavaType(), valueConverter, KeyType.IN));
						}
					}
				}
				
				com.yuehuanghun.mybatis.milu.annotation.Filler[] fillers = options.filler();
				if(fillers.length > 0 && (fillers[0].fillOnInsert() || fillers[0].fillOnUpdate())) {
					if(metaClass == null) {
						metaClass = MetaClass.forClass(clazz, REFLECTOR_FACTORY);
					}
					Filler filler = new Filler(metaClass, field, SupplierHelper.getSupplier(fillers[0].attributeValueSupplier()), fillers[0].fillMode());
					if(fillers[0].fillOnInsert()) {
						entity.getOnInsertFillers().add(filler);
					}
					if(fillers[0].fillOnUpdate()) {
						entity.getOnUpdateFillers().add(filler);
					}
				}
				
				if(options.conditionMode() != Mode.AUTO) {
					attr.setConditionMode(options.conditionMode());
				}
				
				if(options.updateMode() != Mode.AUTO) {
					attr.setUpdateMode(options.updateMode());
				}
				
				if(options.typeHandler().length > 0) {
					attr.setTypeHandler(options.typeHandler()[0]);
				}
				
				if(options.jdbcType().length > 0) {
					attr.setJdbcType(options.jdbcType()[0]);
				}
			}		

			LogicDelete logicDelete = getLogicDelete(field);
			if(logicDelete != null && LogicDeleteAttribute.class.isInstance(attr)) {
				LogicDeleteAttribute logicDeleteAttribute = (LogicDeleteAttribute) attr;
				if(metaClass == null) {
					metaClass = MetaClass.forClass(clazz, REFLECTOR_FACTORY);
				}
				logicDeleteAttribute.setSetter(metaClass.getSetInvoker(attr.getName()));
				
				if(logicDelete.provider() != LogicDeleteProvider.AutoProvider.class) {
					logicDeleteAttribute.setProvider(InstanceUtils.getSigleton(logicDelete.provider()));
				} else if(configuration.getDefaultLogicDeleteProvider() != null) {
					logicDeleteAttribute.setProvider(InstanceUtils.getSigleton(configuration.getDefaultLogicDeleteProvider()));
				} else {
					logicDeleteAttribute.setProvider(new DefaultLogicDeleteProvider(logicDelete.value(), logicDelete.resumeValue(), attr.getJavaType()));
				}
			}

			attr.setRangeList(rangeList == null ? Collections.emptyList() : rangeList);
		}
		
		buildAttribute(entity, clazz.getSuperclass());
	}
	
	private Attribute forField(Field field) {
		Attribute attribute;
		
		if(getAnnotation(field, Id.class) != null) {
			attribute = new IdAttribute();
			GeneratedValue generatedValue = getAnnotation(field, GeneratedValue.class);
			if(generatedValue != null) {
				((IdAttribute)attribute).setGenerationType(generatedValue.strategy());
				((IdAttribute)attribute).setGenerator(generatedValue.generator());
			}
		} else if(field.isAnnotationPresent(Version.class)) {
			attribute = new VersionAttribute();
		} else if(getLogicDelete(field) != null) {
			attribute = new LogicDeleteAttribute();
		} else if(field.isAnnotationPresent(OneToOne.class) || field.isAnnotationPresent(ManyToOne.class)) {
			attribute = new AssociationAttribute();
			attribute.setSelectable(false);
			attribute.setInsertable(false);
			attribute.setUpdateable(false);
			attribute.setReference(true);
			
			Class<?> targetEntity = field.isAnnotationPresent(OneToOne.class) ? field.getAnnotation(OneToOne.class).targetEntity() : field.getAnnotation(ManyToOne.class).targetEntity();			
			if(targetEntity == void.class) {
				attribute.setEntityClass(field.getType());
			} else {
				attribute.setEntityClass(targetEntity);
			}
		} else if(Collection.class.isAssignableFrom(field.getType())) {
			attribute = new PluralAttribute();
			ParameterizedType genericType = (ParameterizedType) field.getGenericType();
			Class<?> elementClass = (Class<?>) genericType.getActualTypeArguments()[0];
			((PluralAttribute)attribute).setElementClass(elementClass);
			if(field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(ManyToMany.class)) {
				attribute.setSelectable(false);
				attribute.setInsertable(false);
				attribute.setUpdateable(false);
				attribute.setReference(true);
				
				Class<?> targetEntity = field.isAnnotationPresent(OneToMany.class) ? field.getAnnotation(OneToMany.class).targetEntity() : field.getAnnotation(ManyToMany.class).targetEntity();			
				if(targetEntity == void.class) {
					attribute.setEntityClass(elementClass);
				} else {
					attribute.setEntityClass(targetEntity);
				}
			}
		} else if(field.getType().isArray()) {
			attribute = new PluralAttribute();
			((PluralAttribute)attribute).setElementClass(field.getType().getComponentType());
		} else {
			attribute = new Attribute();
		}
		attribute.setField(field);
		attribute.setJavaType(field.getType());
		
		Column column = getAnnotation(field, Column.class);
		if(column != null) {
			attribute.setColumnName(column.name());
			if(attribute.isSelectable()) attribute.setInsertable(column.insertable());
			attribute.setNullable(column.nullable());
			if(attribute.isUpdateable()) attribute.setUpdateable(column.updatable());
		}
		
		if(StringUtils.isBlank(attribute.getColumnName())) {
			attribute.setColumnName(StringUtils.camel2Underline(field.getName(), true));
		}
		
		attribute.setName(field.getName());
		attribute.setOptional(Optional.class.equals(field.getType()));
		
		return attribute;
	}
	
	private <T extends Annotation> T getAnnotation(Field field, Class<T> annoClass) {
		T anno = field.getAnnotation(annoClass);
		if(anno != null) {
			return anno;
		}
		
		Annotation[] annotations = field.getAnnotations();
		for(Annotation an : annotations) {
			Method[] methods = an.annotationType().getMethods();
			for(Method method : methods) {
				anno = method.getAnnotation(annoClass);
				if(anno != null) {
					return anno;
				}
			}
			anno = an.annotationType().getAnnotation(annoClass);
			if(anno != null) {
				return anno;
			}
		}
		
		return null;
	}
	
	private LogicDelete getLogicDelete(Field field) {
		LogicDelete anno = field.getAnnotation(LogicDelete.class);
		if(anno != null) {
			return anno;
		}
		AttributeOptions options = field.getAnnotation(AttributeOptions.class);
		if(options == null || options.logicDelete().length == 0) {
			return null;
		}
		return options.logicDelete()[0];
	}
	
	private Reference buildReference(Attribute attr, Entity ownerEntity) {
		Annotation referenceAnnotation = getReferenceAnnotation(attr.getField());
		if(referenceAnnotation == null) {
			return null;
		}
		
		Class<?> targetEntity = getTargetEntity(referenceAnnotation);
		
		Class<?> refEntityClass;
		if(targetEntity != void.class) {
			refEntityClass = targetEntity;
		} else if(PluralAttribute.class.isInstance(attr)) {
			refEntityClass = ((PluralAttribute)attr).getElementClass();
		} else {
			refEntityClass = attr.getJavaType();
		}
		if(!refEntityClass.isAnnotationPresent(javax.persistence.Entity.class)) {
			throw new OrmBuildingException("外联对象或集合元素必须为实体类，可通过关系注解的targetEntity指定实体类");
		}
		
		referenceEntityClassSet.add(refEntityClass);
		
		Entity inverseEntity = forClass(refEntityClass);
		Field field = attr.getField();

		if(field.isAnnotationPresent(OneToOne.class) || field.isAnnotationPresent(OneToMany.class)) {
			String mappedBy = field.isAnnotationPresent(OneToOne.class) ? field.getAnnotation(OneToOne.class).mappedBy() : field.getAnnotation(OneToMany.class).mappedBy();
			if(StringUtils.isNotBlank(mappedBy)) {
				Attribute inverseAttr = inverseEntity.getAttribute(mappedBy);
				if(inverseAttr == null) {
					throw new SqlExpressionBuildingException(String.format("类%s中未找到属性%s", refEntityClass.getName(), mappedBy));
				}
				
				List<JoinColumn> joinColumns = getJoinColumns(inverseAttr.getField());
				if(!joinColumns.isEmpty()) {
					return buildMappedReference(attr, inverseEntity, joinColumns, inverseAttr);
				}
//				if(inverseAttr.getField().isAnnotationPresent(JoinColumn.class)) {
//					JoinColumn joinColumn = inverseAttr.getField().getAnnotation(JoinColumn.class);
//					String name = StringUtils.isNotBlank(joinColumn.name()) ? joinColumn.name() : inverseAttr.getColumnName(); //处理默认值，下同
//					String referencedColumnName = StringUtils.isNotBlank(joinColumn.referencedColumnName()) ? joinColumn.referencedColumnName() : idAttr != null ? idAttr.getColumnName() : name;
//					return new MappedReference(referencedColumnName, field.getName(), inverseEntity.getTableName(), name); //对方的join与本方join字段方向相反
//				}
			} 
			
			//通过mappedBy无法获得关联时，即单向关联
			List<JoinColumn> joinColumns = getJoinColumns(field);
			if(!joinColumns.isEmpty()) {
				return buildMappedReference(attr, inverseEntity, joinColumns, null);
			}
			log.warn(String.format("类%s的属性%s中未找到JoinColumn声明", ownerEntity.getName(), attr.getName()));
			return null; 
			
		} else if(field.isAnnotationPresent(ManyToOne.class)) {
			
			List<JoinColumn> joinColumns = getJoinColumns(field);
			if(!joinColumns.isEmpty()) {
				return buildMappedReference(attr, inverseEntity, joinColumns, null);
			}
			
			String columnName = attr.getColumnName();
			return new MappedReference(attr.getName(), inverseEntity.getTableName(), new JoinCondition(columnName, columnName)); 
			 
		} else if(field.isAnnotationPresent(ManyToMany.class)) {
			ManyToMany refAnnon = field.getAnnotation(ManyToMany.class);
			String mappedBy = refAnnon.mappedBy();
			String joinTableName = null;
			
			if(StringUtils.isNotBlank(mappedBy)) {
				Attribute inverseAttr = inverseEntity.getAttribute(mappedBy);
				if(inverseAttr == null) {
					throw new SqlExpressionBuildingException(String.format("类%s中未找到属性%s", refEntityClass.getName(), mappedBy));
				}
				
				if(inverseAttr.getField().isAnnotationPresent(javax.persistence.JoinTable.class)) { //理论上有mappedBy时，对方属性应当有JoinTable注解，现允许无注解
					javax.persistence.JoinTable joinTableAnno = inverseAttr.getField().getAnnotation(javax.persistence.JoinTable.class);
					joinTableName = joinTableAnno.name();
					
					JoinColumn[] joinColumns = joinTableAnno.joinColumns();
					if(joinColumns.length == 0) {
						throw new SqlExpressionBuildingException(String.format("@JoinTable中joinColumns期望至少一个@JoinColumn值。类%s的属性%s。", refEntityClass.getName(), inverseAttr.getName()));
					}
					JoinColumn[] inverseJoinColumns = joinTableAnno.inverseJoinColumns();
					if(inverseJoinColumns.length == 0) {
						throw new SqlExpressionBuildingException(String.format("@JoinTable中inverseJoinColumns期望至少一个@JoinColumn值。类%s的属性%s。", refEntityClass.getName(), inverseAttr.getName()));
					}
					return buildManyToManyReference(attr, joinTableName, inverseEntity, joinColumns, inverseJoinColumns, inverseAttr);
				}
			}
			
			if(field.isAnnotationPresent(javax.persistence.JoinTable.class)) {
				javax.persistence.JoinTable joinTableAnno = field.getAnnotation(javax.persistence.JoinTable.class);
				joinTableName = joinTableAnno.name();
				
				JoinColumn[] joinColumns = joinTableAnno.joinColumns();
				if(joinColumns.length == 0) {
					throw new SqlExpressionBuildingException(String.format("@JoinTable中joinColumns期望至少一个@JoinColumn值。类%s的属性%s。", refEntityClass.getName(), field.getName()));
				}
				JoinColumn[] inverseJoinColumns = joinTableAnno.inverseJoinColumns();
				if(inverseJoinColumns.length == 0) {
					throw new SqlExpressionBuildingException(String.format("@JoinTable中inverseJoinColumns期望至少一个@JoinColumn值。类%s的属性%s。", refEntityClass.getName(), field.getName()));
				}
				
				return buildManyToManyReference(attr, joinTableName, inverseEntity, joinColumns, inverseJoinColumns, null);
			}
			
			log.warn(String.format("类%s的属性%s中未找到JoinTable声明", ownerEntity.getName(), attr.getName()));
		}
		
		return null;
	}
	
	private MappedReference buildMappedReference(Attribute attr, Entity inverseEntity, List<JoinColumn> joinColumns, Attribute inverseMappedByAttr) {
		MappedReference ref = new MappedReference(attr.getName(), inverseEntity.getTableName());
		for(JoinColumn joinColumn : joinColumns) {
			if(inverseMappedByAttr != null) {
				String name = StringUtils.defaultIfBlank(joinColumn.name(), inverseMappedByAttr.getColumnName());
				String inverseName = StringUtils.defaultIfBlank(joinColumn.referencedColumnName(), name);
				ref.addJoinCondition(new JoinCondition(inverseName, name));
			} else {
				String name = StringUtils.defaultIfBlank(joinColumn.name(), attr.getColumnName());
				String inverseName = StringUtils.defaultIfBlank(joinColumn.referencedColumnName(), name);
				ref.addJoinCondition(new JoinCondition(name, inverseName));
			}
		}
		return ref;
	}
	
	private ManyToManyReference buildManyToManyReference(Attribute attr, String joinTableName, Entity inverseEntity, JoinColumn[] joinColumns, JoinColumn[] inverseJoinColumns, Attribute inverseMappedByAttr) {
		ManyToManyReference ref = new ManyToManyReference(attr.getName(), joinTableName, inverseEntity.getTableName());
		for(JoinColumn joinColumn : joinColumns) {
			if(inverseMappedByAttr != null) {
				String name = StringUtils.defaultIfBlank(joinColumn.name(), inverseMappedByAttr.getColumnName());
				String inverseName = StringUtils.defaultIfBlank(joinColumn.referencedColumnName(), name);
				ref.addInverseJoinCondition(new JoinCondition(name, inverseName));
			} else {
				String name = StringUtils.defaultIfBlank(joinColumn.name(), attr.getColumnName());
				String inverseName = StringUtils.defaultIfBlank(joinColumn.referencedColumnName(), name);
				ref.addJoinCondition(new JoinCondition(name, inverseName));
			}
		}
		
		for(JoinColumn joinColumn : inverseJoinColumns) {
			if(inverseMappedByAttr != null) {
				String name = StringUtils.defaultIfBlank(joinColumn.name(), inverseMappedByAttr.getColumnName());
				String inverseName = StringUtils.defaultIfBlank(joinColumn.referencedColumnName(), name);
				ref.addJoinCondition(new JoinCondition(name, inverseName));
			} else {
				String name = StringUtils.defaultIfBlank(joinColumn.name(), attr.getColumnName());
				String inverseName = StringUtils.defaultIfBlank(joinColumn.referencedColumnName(), name);
				ref.addInverseJoinCondition(new JoinCondition(name, inverseName));
			}
		}
		return ref;
	}
	
	private List<JoinColumn> getJoinColumns(Field field){
		JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
		if(joinColumn != null) {
			return Arrays.asList(joinColumn);
		}
		JoinColumns joinColumns = field.getAnnotation(JoinColumns.class);
		if(joinColumns != null) {
			return Arrays.asList(joinColumns.value());
		}
		return Collections.emptyList();
	}
	
	private static Annotation getReferenceAnnotation(Field field) {
		if(field.isAnnotationPresent(OneToOne.class)) {
			return field.getAnnotation(OneToOne.class);
		}
		if(field.isAnnotationPresent(OneToMany.class)) {
			return field.getAnnotation(OneToMany.class);
		}
		if(field.isAnnotationPresent(ManyToOne.class)) {
			return field.getAnnotation(ManyToOne.class);
		}
		if(field.isAnnotationPresent(ManyToMany.class)) {
			return field.getAnnotation(ManyToMany.class);
		}
		
		return null;
	}
	
	private static Class<?> getTargetEntity(Annotation referenceAnnotation) {
		try {
			return (Class<?>) referenceAnnotation.annotationType().getMethod("targetEntity").invoke(referenceAnnotation);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new SqlExpressionBuildingException(e);
		}
	}
	
	public void buildEntityDefaultResultMap(Class<?> mapperClass) {		
		String resultMapId = mapperClass.getName() + "." + entityClass.getSimpleName() + "Map";
		if(configuration.hasResultMap(resultMapId)) {
			return;
		}

		String resource = mapperClass.getName().replace('.', '/') + ".java (best guess)";
		MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, resource);
		assistant.setCurrentNamespace(mapperClass.getName());
		Collection<Attribute> attributes = forClass(entityClass).getAttributes();
		 List<ResultMapping> resultMappings = new ArrayList<>();
		for(Attribute attr : attributes) {
			if(!attr.isAssociation() && !attr.isCollection()) {
				ResultMapping resultMapping = assistant.buildResultMapping(entityClass, attr.getName(), attr.getColumnName(), attr.getJavaType(), null, null, null, null, null, null, null);
				resultMappings.add(resultMapping);
			}
		}
		assistant.addResultMap(resultMapId, entityClass, null, null, resultMappings, null);
	}
}
