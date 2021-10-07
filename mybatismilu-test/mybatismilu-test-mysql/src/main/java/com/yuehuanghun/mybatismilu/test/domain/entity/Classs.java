package com.yuehuanghun.mybatismilu.test.domain.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.ibatis.type.JdbcType;

import com.yuehuanghun.mybatis.milu.annotation.AttributeOptions;
import com.yuehuanghun.mybatis.milu.annotation.alias.attr.CreateTime;
import com.yuehuanghun.mybatis.milu.annotation.alias.id.SnowflakeId;
import com.yuehuanghun.mybatismilu.test.config.JsonTypeHandler;

import lombok.Data;

@Entity
@Table(name = "class")
@Data
public class Classs {

	@SnowflakeId
	private Long id; //使用分布式ID，内置的snowflakeId

	@CreateTime
	private Date addTime;
	
	private String name;
	
	@AttributeOptions(typeHandler = JsonTypeHandler.class, jdbcType = JdbcType.VARCHAR)
	private List<Long> data;
	
	@ManyToMany(mappedBy = "classList")
	private List<Teacher> teacherList; //多对多关系演示，双向引用
	
	@OneToMany(mappedBy = "classs")
	private List<Student> studentList; //一对多关系演示，双向引用
}
