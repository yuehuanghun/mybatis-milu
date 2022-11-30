package com.yuehuanghun.mybatismilu.test.domain.mapper;

import java.util.List;

import com.yuehuanghun.mybatis.milu.BaseMapper;
import com.yuehuanghun.mybatis.milu.annotation.NamingQuery;
import com.yuehuanghun.mybatismilu.test.domain.entity.Company;

public interface CompanyMapper extends BaseMapper<Company, Long> {

	@NamingQuery
	List<Company> findByCompanyName(String companyName);
	
	@NamingQuery
	Company findByEmployeesName(String name);
}
