package com.yuehuanghun.mybatis.milu.annotation.alias.attr;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.yuehuanghun.mybatis.milu.annotation.AttributeOptions;
import com.yuehuanghun.mybatis.milu.annotation.Filler;

/**
 * 通用的 @AttributeOptions 的短设置
 * @author yuehuanghun
 *
 */
@Retention(RUNTIME)
@Target(FIELD)
@AttributeOptions(filler = @Filler(fillOnInsert = true, fillOnUpdate = true))
public @interface UpdateTime {

}
