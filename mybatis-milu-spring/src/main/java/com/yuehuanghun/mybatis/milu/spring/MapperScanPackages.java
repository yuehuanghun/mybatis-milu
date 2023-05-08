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

package com.yuehuanghun.mybatis.milu.spring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public class MapperScanPackages {
	private static final String BEAN = MapperScanPackages.class.getName();

	private static final MapperScanPackages NONE = new MapperScanPackages();

	private final List<String> packageNames;
	
	MapperScanPackages(String... packageNames) {
		List<String> packages = new ArrayList<>();
		for (String name : packageNames) {
			if (StringUtils.hasText(name)) {
				packages.add(name);
			}
		}
		this.packageNames = Collections.unmodifiableList(packages);
	}
	
	public List<String> getPackageNames() {
		return this.packageNames;
	}
	
	public static MapperScanPackages get(BeanFactory beanFactory) {
		try {
			return beanFactory.getBean(BEAN, MapperScanPackages.class);
		}
		catch (NoSuchBeanDefinitionException ex) {
			return NONE;
		}
	}
	
	public static void register(BeanDefinitionRegistry registry, Collection<String> packageNames) {
		Assert.notNull(registry, "Registry must not be null");
		Assert.notNull(packageNames, "PackageNames must not be null");
		if (registry.containsBeanDefinition(BEAN)) {
			BeanDefinition beanDefinition = registry.getBeanDefinition(BEAN);
			ConstructorArgumentValues constructorArguments = beanDefinition.getConstructorArgumentValues();
			constructorArguments.addIndexedArgumentValue(0, addPackageNames(constructorArguments, packageNames));
		}
		else {
			GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
			beanDefinition.setBeanClass(MapperScanPackages.class);
			beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0,
					StringUtils.toStringArray(packageNames));
			beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			registry.registerBeanDefinition(BEAN, beanDefinition);
		}
	}
	
	private static String[] addPackageNames(ConstructorArgumentValues constructorArguments,
			Collection<String> packageNames) {
		String[] existing = (String[]) constructorArguments.getIndexedArgumentValue(0, String[].class).getValue();
		Set<String> merged = new LinkedHashSet<>();
		merged.addAll(Arrays.asList(existing));
		merged.addAll(packageNames);
		return StringUtils.toStringArray(merged);
	}
	
	static class Registrar implements ImportBeanDefinitionRegistrar {

		@Override
		public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
			register(registry, getPackagesToScan(metadata));
		}

		private Set<String> getPackagesToScan(AnnotationMetadata metadata) {
			AnnotationAttributes attributes = AnnotationAttributes
					.fromMap(metadata.getAnnotationAttributes(MapperScan.class.getName()));
			if(attributes != null) {
				return getBasePackages(attributes, metadata);
			}
			
			attributes = AnnotationAttributes
					.fromMap(metadata.getAnnotationAttributes(MapperScans.class.getName()));
			
			if(attributes != null) {
				AnnotationAttributes[] annotationArray = attributes.getAnnotationArray("value");
				Set<String> basePackages = new HashSet<>();
				for(AnnotationAttributes annotationAttributes : annotationArray) {
					basePackages.addAll(getBasePackages(annotationAttributes, metadata));
				}
				return basePackages;
			}

			return Collections.emptySet();
		}

		private Set<String> getBasePackages(AnnotationAttributes attributes, AnnotationMetadata metadata) {
			String[] basePackages = attributes.getStringArray("basePackages");
			Class<?>[] basePackageClasses = attributes.getClassArray("basePackageClasses");
			Set<String> packagesToScan = new LinkedHashSet<>(Arrays.asList(basePackages));
			for (Class<?> basePackageClass : basePackageClasses) {
				packagesToScan.add(ClassUtils.getPackageName(basePackageClass));
			}
			if (packagesToScan.isEmpty()) {
				String packageName = ClassUtils.getPackageName(metadata.getClassName());
				Assert.state(!StringUtils.isEmpty(packageName), "@MapperScan cannot be used with the default package");
				return Collections.singleton(packageName);
			}
			return packagesToScan;
		}
	}
}
