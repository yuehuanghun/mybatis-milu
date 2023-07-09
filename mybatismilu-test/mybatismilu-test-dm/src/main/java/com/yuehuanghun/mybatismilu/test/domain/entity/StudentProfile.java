
package com.yuehuanghun.mybatismilu.test.domain.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.apache.ibatis.type.JdbcType;

import com.yuehuanghun.mybatis.milu.annotation.AttributeOptions;
import com.yuehuanghun.mybatis.milu.annotation.alias.attr.CreateTime;
import com.yuehuanghun.mybatis.milu.annotation.alias.attr.UpdateTime;

import lombok.Data;

@Entity
@Data
public class StudentProfile {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "timestamp") //自定义ID生成器com.yuehuanghun.mybatismilu.test.generator.TimestampIdGenerator
	private Long id;

	private String fatherName;

	@AttributeOptions(jdbcType = JdbcType.VARCHAR)
	private String motherName;

	private Integer fatherAge;

	private Integer motherAge;
	
	private Long studentId;

	@CreateTime
	private Date addTime;
	@UpdateTime
	private Date updateTime;
	
	@OneToOne
	@JoinColumn(name = "student_id", referencedColumnName = "id")
	private Student student;  //一对一引用
}

