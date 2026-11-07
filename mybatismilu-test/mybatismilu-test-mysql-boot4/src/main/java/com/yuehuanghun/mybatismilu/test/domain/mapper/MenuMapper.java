package com.yuehuanghun.mybatismilu.test.domain.mapper;

import java.util.Date;
import java.util.List;

import com.yuehuanghun.mybatis.milu.BaseMapper;
import com.yuehuanghun.mybatis.milu.annotation.NamingQuery;
import com.yuehuanghun.mybatismilu.test.domain.entity.Menu;

public interface MenuMapper extends BaseMapper<Menu, Long> {

	@NamingQuery
	public List<Menu> findByParentName(String parentName);
	
	@NamingQuery
	public List<Menu> findDistinctByChildrensName(String childrenName);
	
	@NamingQuery
	public List<Menu> findDistinctByChildrensNameAndChildrensAddTimeLessThan(String childrenName, Date addTimeBefore);
}
