package com.yuehuanghun.mybatismilu.test.domain.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.yuehuanghun.mybatis.milu.tool.Constants;

import lombok.Data;

@Entity
@Table(name = "class")
@Data
public class Classs {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = Constants.ID_GENERATOR_SNOWFLAKE)
	private Long id; //使用分布式ID，内置的snowflakeId

	private Date addTime;
	
	private String name;
	
	@ManyToMany(mappedBy = "classList")
	private List<Teacher> teacherList; //多对多关系演示，双向引用
	
	@OneToMany(mappedBy = "classs")
	private List<Student> studentList; //一对多关系演示，双向引用
}
