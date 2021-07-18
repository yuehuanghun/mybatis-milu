package com.yuehuanghun.mybatismilu.test.domain.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.TableGenerator;

import com.yuehuanghun.mybatis.milu.annotation.AttributeOptions;
import com.yuehuanghun.mybatis.milu.annotation.ExampleQuery;
import com.yuehuanghun.mybatis.milu.annotation.Filler;
import com.yuehuanghun.mybatis.milu.annotation.ExampleQuery.MatchType;

import lombok.Data;

@Data
@Entity
public class Student {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "tableSequence")
	@TableGenerator(name = "tableSequence", table = "sequence", valueColumnName = "current_seq", pkColumnName = "id", pkColumnValue = "1")
	private Long id; //使用数据表模拟数字序列
	
	@AttributeOptions(filler = @Filler(fillOnInsert = true))
	private Date addTime;
	
	@AttributeOptions(filler = @Filler(fillOnInsert = true, fillOnUpdate = true)) //当在插入或更新数据时，如果该属性为null则自动填充值
	private Date updateTime;
	
	@AttributeOptions(exampleQuery = @ExampleQuery(matchType = MatchType.CONTAIN)) //使用findByExample方法时name不为空时，即执行name LIKE %nameValue%
	private String name;
	
	private Integer age;
	
	private Long classId;
	
	@ManyToOne
	@JoinColumn(name = "class_id", referencedColumnName = "id")
	private Classs classs; //对多一引用
	
	@OneToOne(mappedBy = "student")
	private StudentProfile studentProfile; //一对一引用
}
