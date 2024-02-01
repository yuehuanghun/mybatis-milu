
package com.yuehuanghun.mybatismilu.test.domain.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.Data;

@Entity
@Data
public class StudentProfile {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "timestamp") //自定义ID生成器com.yuehuanghun.mybatismilu.test.generator.TimestampIdGenerator
	private Long id;

	private String fatherName;

	private String motherName;

	private Integer fatherAge;

	private Integer motherAge;
	
	private Long studentId;

	@Column(insertable = false, updatable = false) //对于表一列的创建时间、更新时间，可以使用数据库字段默认值， 设为不可插入，不可更新
	private Date addTime;
	@Column(insertable = false, updatable = false)
	private Date updateTime;
	
	@OneToOne
	@JoinColumn(name = "student_id", referencedColumnName = "id")
	private Student student;  //一对一引用
}

