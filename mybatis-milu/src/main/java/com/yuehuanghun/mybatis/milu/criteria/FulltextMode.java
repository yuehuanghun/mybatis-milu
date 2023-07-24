package com.yuehuanghun.mybatis.milu.criteria;

/**
 * 全文索引模式
 * @author yuehuanghun
 *
 */
public enum FulltextMode {

	/**
	 * 数据库默认
	 */
	NATIVE,
	/**
	 * mysql（及兼容mysql语法）的全文搜索自然语言模式
	 */
	MYSQL_NATURAL_LANG, 
	/**
	 * mysql（及兼容mysql语法）的全文搜索布尔模式
	 */
	MYSQL_BOOLEAN,
	/**
	 * sqlserver的全文搜索contains模式
	 */
	SQLSERVER_CONTAINS,
	/**
	 * sqlserver的全文搜索freetext模式<br>
	 * 会先把要查询的词句先进行分词然后再查询匹配
	 */
	SQLSERVER_FREETEXT
}
