package com.yuehuanghun.mybatismilu.spring.boot.autoconfigure;

import java.util.Collections;
import java.util.Set;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDependsOnDatabaseInitializationDetector;

class MybatisDependsOnDatabaseInitializationDetector
    extends AbstractBeansOfTypeDependsOnDatabaseInitializationDetector {

  @Override
  protected Set<Class<?>> getDependsOnDatabaseInitializationBeanTypes() {
    return Collections.singleton(SqlSessionTemplate.class);
  }

}
