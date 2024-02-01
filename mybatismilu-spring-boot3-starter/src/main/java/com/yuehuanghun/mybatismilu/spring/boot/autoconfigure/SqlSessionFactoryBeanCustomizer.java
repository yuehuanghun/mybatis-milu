package com.yuehuanghun.mybatismilu.spring.boot.autoconfigure;

import org.mybatis.spring.SqlSessionFactoryBean;

@FunctionalInterface
public interface SqlSessionFactoryBeanCustomizer {

	void customize(SqlSessionFactoryBean factoryBean);

}
