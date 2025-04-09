package com.yuehuanghun.mybatismilu.test.domain.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Version;

import com.yuehuanghun.mybatis.milu.annotation.AttributeOptions;
import com.yuehuanghun.mybatis.milu.annotation.Filler;
import com.yuehuanghun.mybatis.milu.annotation.LogicDelete;
import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatismilu.test.config.MyLogicDeleteProvider;

import lombok.Data;

@Data
@Entity
public class Teacher {

	@Id
	private Long id; //数据库自增ID
	
	private Date addTime;
	
	@AttributeOptions(updateMode = Mode.ALL)
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
	
	private String cv;
}
