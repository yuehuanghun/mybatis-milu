package com.yuehuanghun.mybatismilu.test.domain.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Version;

import com.yuehuanghun.mybatis.milu.annotation.AttributeOptions;
import com.yuehuanghun.mybatis.milu.annotation.Filler;
import com.yuehuanghun.mybatis.milu.annotation.LogicDelete;
import com.yuehuanghun.mybatis.milu.annotation.alias.attr.CreateTime;
import com.yuehuanghun.mybatis.milu.annotation.alias.id.SnowflakeId;
import com.yuehuanghun.mybatismilu.test.config.MyLogicDeleteProvider;

import lombok.Data;

@Data
@Entity
public class Teacher {

	@SnowflakeId
	private Long id; //数据库自增ID
	
	@CreateTime
	private Date addTime;
	
	private String name;
	
	private Integer age;
	
	@Version
	@AttributeOptions(filler = @Filler(fillOnInsert = true))
	private Integer revision;
	
	@ManyToMany
	@JoinTable(name = "class_teacher_rel", joinColumns = @JoinColumn(name = "teacher_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "class_id", referencedColumnName = "id"))
	private List<Classs> classList; //多对多引用演示
	
	@LogicDelete(provider = MyLogicDeleteProvider.class)
	private String isDeleted;
}
