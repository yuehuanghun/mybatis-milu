package com.yuehuanghun.mybatis.milu.example.example1.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;

import com.yuehuanghun.mybatis.milu.annotation.alias.attr.CreateTime;
import com.yuehuanghun.mybatis.milu.annotation.alias.id.SnowflakeId;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ExampleOne {

	@SnowflakeId
	private Long id;
	
	private String name;
	
	@CreateTime
	private LocalDateTime createTime;

	public ExampleOne(String name) {
		super();
		this.name = name;
	}
}
