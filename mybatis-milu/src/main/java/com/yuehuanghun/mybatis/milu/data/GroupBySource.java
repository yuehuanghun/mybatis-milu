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
package com.yuehuanghun.mybatis.milu.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.yuehuanghun.mybatis.milu.tool.StringUtils;

/**
 * 分组创建帮助类<br>
 * @author yuehuanghun
 *
 */
public class GroupBySource {

    private static final String BLOCK_SPLIT = "And";

    private final List<String> groupProperties;

    public GroupBySource(String clause) {
        this(clause, null);
    }

    public GroupBySource(String clause, Class<?> domainClass) {
    	if(StringUtils.isBlank(clause)) {
    		groupProperties = Collections.emptyList();
    		return;
    	}
    	String[] properties = clause.split(BLOCK_SPLIT);
    	this.groupProperties = new ArrayList<>();
    	for(String property : properties) {
    		this.groupProperties.add(StringUtils.uncapitalize(property));
    	}
    }
    
    public List<String> getGroupProperties(){
    	return this.groupProperties;
    }

    @Override
    public String toString() {
        return "Group By " + StringUtils.collectionToDelimitedString(groupProperties, ", ");
    }
}