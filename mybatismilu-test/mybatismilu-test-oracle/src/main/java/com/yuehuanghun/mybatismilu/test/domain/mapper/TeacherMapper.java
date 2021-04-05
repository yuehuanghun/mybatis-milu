package com.yuehuanghun.mybatismilu.test.domain.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.yuehuanghun.mybatis.milu.BaseMapper;
import com.yuehuanghun.mybatis.milu.annotation.NamingQuery;
import com.yuehuanghun.mybatismilu.test.domain.entity.Teacher;

@Mapper
public interface TeacherMapper extends BaseMapper<Teacher, Long> {
	@NamingQuery
	public List<Teacher> findByClassListName(String className);
}
