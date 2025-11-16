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

package com.yuehuanghun.mybatis.milu.criteria.ext.postgre.select;

import java.util.Objects;
import java.util.Set;

import com.yuehuanghun.mybatis.milu.criteria.Select;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.tool.Assert;
import com.yuehuanghun.mybatis.milu.tool.DefendUtil;

/**
 * PostgreSQL JSON 数据提取，使用KEY<br>
 * 使用 ->> 表达式<br>
 * 深度提取 {@link PostgreJsonExtractPath}
 */
public class PostgreJsonExtract implements Select {

	private String attrName;
	
	private String jsonKey;
	
	private String alias;
	
	/**
	 * 构建一个新实例
	 * @param attrName JSON字段属性
	 * @param jsonKey 定位数据的json键，例如{"userName":"zhangsan"}} ->> 'userName' 得到 zhangsan，传入userName字符串即可
	 * @param alias 查询列别名，为Null时则等于attrName的值
	 */
	public static PostgreJsonExtract of(String attrName, String jsonKey, String alias) {
		Assert.notBlank(attrName, "attrName不能为空");
		Assert.notBlank(jsonKey, "jsonKey不能为空");
		Assert.isTrue(alias == null || DefendUtil.testColumnAlias(alias), "alias值不符合规则");
		PostgreJsonExtract select = new PostgreJsonExtract();
		select.attrName = attrName;
		select.jsonKey = jsonKey;
		select.alias = alias == null ? attrName : alias;
		return select;
	}

	// 使用 ->> 表达式
	@Override
	public String getExpresion(GenericProviderContext context, Set<String> attrNames, Set<String> aliases) {
		attrNames.add(attrName);
		aliases.add(alias);
		return wrapAttrName(attrName) + " -&gt;&gt; '" + jsonKey + "' " + SqlBuildingHelper.wrapIdentifier(alias, context.getConfiguration());
	}


	@Override
	public int hashCode() {
		int result = 17;

		result = 31 * result + attrName.hashCode();
		result = 31 * result + jsonKey.hashCode();
		result = 31 * result + alias.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object that) {
		if(!this.getClass().isInstance(that)) {
			return false;
		}
		return Objects.equals(this.attrName, ((PostgreJsonExtract)that).attrName)
				&& Objects.equals(this.jsonKey, ((PostgreJsonExtract)that).jsonKey)
				&& Objects.equals(this.alias, ((PostgreJsonExtract)that).alias);
	}
}
