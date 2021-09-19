package com.yuehuanghun.mybatis.milu.annotation.alias.id;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.yuehuanghun.mybatis.milu.tool.Constants;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

@Retention(RUNTIME)
@Target(FIELD)
public @interface SnowflakeId {

	@Id
	@GeneratedValue(generator = Constants.ID_GENERATOR_SNOWFLAKE)
	String value() default StringUtils.EMPTY;
}
