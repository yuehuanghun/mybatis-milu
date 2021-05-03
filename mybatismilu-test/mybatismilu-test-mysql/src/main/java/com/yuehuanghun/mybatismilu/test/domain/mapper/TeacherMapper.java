package com.yuehuanghun.mybatismilu.test.domain.mapper;

import java.util.List;

import javax.persistence.LockModeType;

import org.apache.ibatis.annotations.Mapper;

import com.yuehuanghun.mybatis.milu.BaseMapper;
import com.yuehuanghun.mybatis.milu.annotation.NamingQuery;
import com.yuehuanghun.mybatis.milu.annotation.StatementOptions;
import com.yuehuanghun.mybatismilu.test.domain.entity.Teacher;
import com.yuehuanghun.mybatismilu.test.dto.TeacherDTO;

@Mapper
public interface TeacherMapper extends BaseMapper<Teacher, Long> {
	@NamingQuery
	public List<Teacher> findByClassListName(String className);
	
	//没有被@NamingQuery声明的接口，使用的是mapper.xml中定义的statement
	public Teacher getById(Long id);
	
	@NamingQuery
	public List<TeacherDTO> findByAge(int age); //返回类（或集合元素）非实体类的
	
	@NamingQuery
	@StatementOptions(selects = "id", asExpression = "findByAgeGreaterThan") //查询的字段，并覆盖method的查询表达式
	public List<Long> findTeacherIds(int age);
	
	@NamingQuery
	public List<Teacher> findByAgeGreaterThan(int age); //跟上一个使用相同的表达式，使用asExpression可以解决应用场景冲突问题
	
	@NamingQuery
	@StatementOptions(exselects = "addTime") //不查询的字段
	public List<Teacher> findByAgeLessThan(int age);
	
	@NamingQuery
	@StatementOptions(asExpression = "findById", lockModeType = LockModeType.PESSIMISTIC_WRITE)
	public Teacher findByIdWithLock(Long id);
	
	@NamingQuery
	@StatementOptions(asExpression = "findById", lockModeType = LockModeType.PESSIMISTIC_READ)
	public Teacher findByIdWithShareLock(Long id);
}
