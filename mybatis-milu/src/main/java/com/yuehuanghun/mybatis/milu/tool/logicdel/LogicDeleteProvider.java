package com.yuehuanghun.mybatis.milu.tool.logicdel;

import com.yuehuanghun.mybatis.milu.exception.OrmBuildingException;

import lombok.Data;

public interface LogicDeleteProvider {
	
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
	class Context {
		/**
		 * 所属实体类
		 */
		Class<?> entityType;
		
		/**
		 * 逻辑删除属性类型
		 */
		Class<?> fieldType;
	}
}
