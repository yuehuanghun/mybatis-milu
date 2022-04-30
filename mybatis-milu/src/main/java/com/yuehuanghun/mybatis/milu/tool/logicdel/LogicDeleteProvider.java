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
package com.yuehuanghun.mybatis.milu.tool.logicdel;

import com.yuehuanghun.mybatis.milu.exception.OrmBuildingException;

import lombok.AllArgsConstructor;
import lombok.Data;

public interface LogicDeleteProvider {
	
	/**
	 * 自动选择<br>
	 * 当设置了全局默认provider时，则以全局provider为准，否则就等同于NoneProvider
	 * 
	 * @see com.yuehuanghun.mybatis.milu.tool.logicdel.LogicDeleteProvider.NoneProvider
	 *
	 */
	class AutoProvider implements LogicDeleteProvider {

		@Override
		public Object value(Context context) {
			throw new OrmBuildingException();
		}

		@Override
		public Object resumeValue(Context context) {
			throw new OrmBuildingException();
		}
	}
	
	/**
	 * 无数据提供者<br>
	 * 直接使用@LogicDelete的value
	 *
	 */
	class NoneProvider extends AutoProvider {
	}
	
	/**
	 * 逻辑删除的值
	 * @param context 上下文
	 * @return 值
	 */
	Object value(Context context);

	/**
	 * 恢复逻辑删除的正常状态的值
	 * @param context 上下文
	 * @return 值
	 */
	Object resumeValue(Context context);
	
	@Data
	@AllArgsConstructor
	class Context {
		/**
		 * 所属实体类
		 */
		private Class<?> entityType;
		
		/**
		 * 逻辑删除属性类型
		 */
		private Class<?> attrType;
		
		/**
		 * 逻辑删除属性名
		 */
		private String attrName;
	}
}
