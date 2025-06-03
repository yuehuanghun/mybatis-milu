package com.yuehuanghun.mybatismilu.test.domain.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.yuehuanghun.mybatis.milu.annotation.AttributeOptions;
import com.yuehuanghun.mybatis.milu.annotation.alias.attr.CreateTime;

import lombok.Data;

@Data
@Entity
public class User {

	@Id
	private Long id;

	@AttributeOptions(index = {"uidx_user_name"})
	private String userName;
	
	private String name;
	
	private Integer age;
	
	@CreateTime
	private LocalDateTime createTime;
}
