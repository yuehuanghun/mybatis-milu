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

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.yuehuanghun.mybatis.milu.PlaceholderResolver;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

/**
 * 默认的点位符解析器
 * @author yuehuanghun
 *
 */
@Component
public class SpringPlaceholderResolver implements PlaceholderResolver, EnvironmentAware {
	private Environment environment;

	@Override
	public String resolvePlaceholder(String placeholderName) {
		if(StringUtils.isBlank(placeholderName)) {
			return placeholderName;
		}
		return environment.resolveRequiredPlaceholders(placeholderName);
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

}
