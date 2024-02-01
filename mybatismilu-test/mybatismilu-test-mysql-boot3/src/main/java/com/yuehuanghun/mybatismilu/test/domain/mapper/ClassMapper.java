package com.yuehuanghun.mybatismilu.test.domain.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.yuehuanghun.mybatis.milu.BaseMapper;
import com.yuehuanghun.mybatis.milu.annotation.NamingQuery;
import com.yuehuanghun.mybatismilu.test.domain.entity.Classs;

@Mapper
public interface ClassMapper extends BaseMapper<Classs, Long> {

	@NamingQuery
	public List<Classs> findByTeacherListName(String teacherName);
	
	@NamingQuery
	public List<Classs> findByStudentListName(String studentName);
	
	@NamingQuery
	public List<Classs> findByStudentListNameInAndStudentListAgeGreaterThan(String[] studentNames, int age);
	
	@NamingQuery
	public List<Classs> findByAddTimeAfterAndAddTimeBefore(Date beginTime, Date endTime);
	
	@NamingQuery
	public List<Classs> findByAddTimeBetween(Date beginTime, Date endTime);
}
