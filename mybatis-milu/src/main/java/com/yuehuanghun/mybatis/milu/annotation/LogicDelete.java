package com.yuehuanghun.mybatis.milu.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 逻辑删除声明<br>
 * 作为AttributeOptions的属性注解在实体类属性上<br>
 * 允许声明在实体类的多个属性上
 * 
 * @see com.yuehuanghun.mybatis.milu.annotation.AttributeOptions
 * @see com.yuehuanghun.mybatis.milu.BaseMapper#logicDeleteById
 * @author yuehuanghun
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface LogicDelete {

	/**
	 * 逻辑删除时设置的值
	 * @see com.yuehuanghun.mybatis.milu.BaseMapper#logicDeleteById
	 * @return 值
	 */
	String value() default "1";
	
	/**
	 * 恢复正常状态时的值<br>
	 * @see com.yuehuanghun.mybatis.milu.BaseMapper#resumeLogicDeletedById
	 * @return 值
	 */
	String resumeValue() default "0";
}
