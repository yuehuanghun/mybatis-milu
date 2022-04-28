
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

package com.yuehuanghun.mybatis.milu.tool;

/**
 * MybatisProperties自动装配配置类中configurationProperties中可配置的键
 * @author yuehuanghun
 *
 */
public interface ConfigPropertyKeys {
    /** 全局默认主键构建器 */
	String ID_GENERATOR = "idGenerator";
	/** 全局默认Example查询的参数转换器 */
	String EXAMPLE_QUERY_CONVERTER = "exampleQueryConverter";
	/** 全局默认逻辑删除值提供器 */
	String LOGIC_DELETE_PROVIDER = "logicDeleteProvider";
}
