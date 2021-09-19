package com.yuehuanghun.mybatis.milu.annotation.alias.attr;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.yuehuanghun.mybatis.milu.annotation.AttributeOptions;
import com.yuehuanghun.mybatis.milu.annotation.ExampleQuery;
import com.yuehuanghun.mybatis.milu.annotation.ExampleQuery.MatchType;

/**
 * 
 * 通用的 @AttributeOptions 的短设置，在Example查询使用包含查询值的查询方法，即column_name LIKE %value%
 * @author yuehuanghun
 *
 */
@Retention(RUNTIME)
@Target(FIELD)
@AttributeOptions(exampleQuery = @ExampleQuery(matchType = MatchType.CONTAIN))
public @interface Contains {

}
