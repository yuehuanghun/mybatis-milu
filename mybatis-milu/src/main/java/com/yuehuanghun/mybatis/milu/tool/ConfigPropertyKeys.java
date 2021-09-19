package com.yuehuanghun.mybatis.milu.tool;

/**
 * MybatisProperties自动装配配置类中configurationProperties中可配置的键
 * @author yuehuanghun
 *
 */
public interface ConfigPropertyKeys {
    /** 全局默认主键构建器 */
	String ID_GENERATOR = "idGenerator";
	/** 全局默认Example查询的参数转换器 */
	String EXAMPLE_QUERY_CONVERTER = "exampleQueryConverter";
}
