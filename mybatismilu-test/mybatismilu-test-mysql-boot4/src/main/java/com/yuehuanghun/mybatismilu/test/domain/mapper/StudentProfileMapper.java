package com.yuehuanghun.mybatismilu.test.domain.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.yuehuanghun.mybatis.milu.BaseMapper;
import com.yuehuanghun.mybatis.milu.annotation.NamingQuery;
import com.yuehuanghun.mybatismilu.test.domain.entity.StudentProfile;

@Mapper
public interface StudentProfileMapper extends BaseMapper<StudentProfile, Long> {

	@NamingQuery
	public List<StudentProfile> findByStudentNameLike(String name);
}
