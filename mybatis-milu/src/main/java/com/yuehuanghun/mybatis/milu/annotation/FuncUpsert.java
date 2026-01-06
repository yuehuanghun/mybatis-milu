package com.yuehuanghun.mybatis.milu.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface FuncUpsert {

	/**
	 * 列名设置，表字段。如果不指定，则为对应属性的小驼峰转下划线字段名
	 * @return 列名
	 */
	String columnName() default "";
	
	/**
	 * 数据插入/更新表达式，仅接收一个参数${value}，引用对应属性的值
	 * @return 表达式
	 */
	String expression();
}
