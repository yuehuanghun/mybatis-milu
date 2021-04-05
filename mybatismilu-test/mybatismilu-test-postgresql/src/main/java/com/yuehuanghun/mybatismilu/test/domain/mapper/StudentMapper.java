package com.yuehuanghun.mybatismilu.test.domain.mapper;


import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.yuehuanghun.mybatis.milu.BaseMapper;
import com.yuehuanghun.mybatis.milu.annotation.NamingQuery;
import com.yuehuanghun.mybatismilu.test.domain.entity.Student;

@Mapper
public interface StudentMapper extends BaseMapper<Student, Long> {

	@NamingQuery
	public List<Student> findByName(String name);
	@NamingQuery
	public int countByName(String name);
	@NamingQuery
	public List<Student> findByClasssName(String className);	
	@NamingQuery
	public List<Student> findTop5ByAddTimeAfter(Date addTimeBegin);
	@NamingQuery
	public List<Student> findByClasssNameOrderByClasssIdDescAddTimeAsc(String className);
}
