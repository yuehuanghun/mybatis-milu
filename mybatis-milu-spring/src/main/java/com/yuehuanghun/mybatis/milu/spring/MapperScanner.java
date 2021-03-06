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

package com.yuehuanghun.mybatis.milu.spring;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public class MapperScanner {
	private final ApplicationContext context;
	
	public MapperScanner(ApplicationContext context) {
		Assert.notNull(context, "Context must not be null");
		this.context = context;
	}
	
	@SafeVarargs
	public final Set<Class<?>> scan(Class<? extends Annotation>... annotationTypes) throws ClassNotFoundException {
		List<String> packages = getPackages();
		if (packages.isEmpty()) {
			return Collections.emptySet();
		}
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false) {
			@Override
			  protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
			    return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
			  }
		};
		scanner.setEnvironment(this.context.getEnvironment());
		scanner.setResourceLoader(this.context);
		for (Class<? extends Annotation> annotationType : annotationTypes) {
			scanner.addIncludeFilter(new AnnotationTypeFilter(annotationType));
		}
		Set<Class<?>> entitySet = new HashSet<>();
		for (String basePackage : packages) {
			if (StringUtils.hasText(basePackage)) {
				for (BeanDefinition candidate : scanner.findCandidateComponents(basePackage)) {
					entitySet.add(ClassUtils.forName(candidate.getBeanClassName(), this.context.getClassLoader()));
				}
			}
		}
		
		return entitySet;
	}

	private List<String> getPackages() {
		List<String> packages = MapperScanPackages.get(this.context).getPackageNames();
		return packages == null ? Collections.emptyList() : packages;
	}
}
