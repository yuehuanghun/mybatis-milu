package com.yuehuanghun.mybatismilu.test.domain.entity;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.ibatis.type.JdbcType;

import com.yuehuanghun.mybatis.milu.annotation.AttributeOptions;
import com.yuehuanghun.mybatis.milu.annotation.EntityOptions;
import com.yuehuanghun.mybatis.milu.annotation.ExampleQuery;
import com.yuehuanghun.mybatis.milu.annotation.ExampleQuery.MatchType;
import com.yuehuanghun.mybatis.milu.annotation.Filler;
import com.yuehuanghun.mybatis.milu.annotation.LogicDelete;
import com.yuehuanghun.mybatis.milu.annotation.EntityOptions.FetchRef;

import lombok.Data;

@Entity
@Data
@FetchRef(refAttrs = {"parent","childrens"})
@EntityOptions(fetchRefs = {@FetchRef(group = "parent", refAttrs = "parent"), @FetchRef(group = "children", refAttrs = "childrens")})
public class Menu {

	@Id
	private Long id;
	
	private Long pid;
	
	@ExampleQuery(matchType = MatchType.CONTAIN)
	@AttributeOptions(exampleQuery = @ExampleQuery(matchType = MatchType.EQUAL))
	private String name;
	
	@AttributeOptions(filler = @Filler(fillOnInsert = true)) //插入时不需要设置值 
	@Column(updatable = false) //设置为不可更新
	private Date addTime;

	@AttributeOptions(filler = @Filler(fillOnInsert = true, fillOnUpdate = true)) //插入和更新时不需要设置值 
	private Date updateTime;
	
	@ManyToOne
	@JoinColumn(name = "pid", referencedColumnName = "id")
	private Menu parent; //自身多对一引用
	
	@OneToMany
	@JoinColumn(name = "id", referencedColumnName = "pid")
	private List<Menu> childrens; //自身一对多引用
	
	@LogicDelete
	private Integer isDeleted;
	
	@LogicDelete(value = "#{now}", resumeValue = "", main = false)
	@AttributeOptions(jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	private LocalDateTime deleteTime;
}
