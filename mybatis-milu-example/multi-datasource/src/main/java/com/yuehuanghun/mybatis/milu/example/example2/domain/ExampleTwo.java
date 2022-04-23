package com.yuehuanghun.mybatis.milu.example.example2.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;

import com.yuehuanghun.mybatis.milu.annotation.alias.attr.CreateTime;
import com.yuehuanghun.mybatis.milu.annotation.alias.attr.UpdateTime;
import com.yuehuanghun.mybatis.milu.annotation.alias.id.SnowflakeId;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ExampleTwo {

	@SnowflakeId
	private Long id;
	
	private String code;
	
	private String name;
	
	@CreateTime
	private LocalDateTime createTime;

	@UpdateTime
	private LocalDateTime updateTime;

	public ExampleTwo(String code, String name) {
		super();
		this.code = code;
		this.name = name;
	}
	
}
