package com.yuehuanghun.mybatis.milu.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 实体的配置项<br>
 * 用于实体类的注解
 * 
 * @author yuehuanghun
 *
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.TYPE)
public @interface EntityOptions {

	/**
	 * 设置需要获取的关联实体数据<br>
	 * 
	 * @return 实体引用属性名
	 */
	FetchRef[] fetchRefs() default {};
	
	/**
	 * 是否过滤逻辑删除的数据<br>
	 * 目前仅作用于example查询中<br>
	 * 在criteria查询中，可以使用undeleted()方法快捷过滤
	 * @return true：过滤已逻辑删除数据，false：不过滤
	 */
	boolean filterLogicDeletedData() default true;

	/**
	 * exampleQuery中，自定义需要返回的引用属性<br>
	 * 
	 * 引用属性是指实体中由@ManyToOne、@OneToOne、@OneToMany注解的属性
	 *
	 */
	@Documented
	@Retention(RUNTIME)
	@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
	public static @interface FetchRef {

		/**
		 * 分组，可在exampleQuery中指定配置分组
		 * @return 分组
		 */
		String group() default "default";
		
		/**
		 * 引用属性名
		 * @return 引用属性名
		 */
		String[] refAttrs();
		
		/**
		 * 设置关联查询模式
		 * @return 关联查询模式
		 */
		JoinMode joinMode() default JoinMode.LEFT_JOIN;
	}
}
