package com.yuehuanghun.mybatismilu.test.domain.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.yuehuanghun.mybatis.milu.annotation.AttributeOptions;
import com.yuehuanghun.mybatis.milu.annotation.Filler;

import lombok.Data;

@Entity
@Data
public class Menu {

	@Id
	private Long id;
	
	private Long pid;
	
	private String name;
	
	@AttributeOptions(filler = @Filler(fillOnInsert = true)) //插入时不需要设置值 
	@Column(updatable = false) //设置为不可更新
	private Date addTime;

	@AttributeOptions(filler = @Filler(fillOnInsert = true, fillOnUpdate = true)) //插入和更新时不需要设置值 
	private Date updateTime;
	
	@ManyToOne
	@JoinColumn(name = "pid"/* , referencedColumnName = "id" */) //当referencedColumnName为对方的主键时，是可以省略设置的
	private Menu parent; //自身多对一引用
	
	@OneToMany
	@JoinColumn(name = "id", referencedColumnName = "pid")
	private List<Menu> childrens; //自身一对多引用
}
