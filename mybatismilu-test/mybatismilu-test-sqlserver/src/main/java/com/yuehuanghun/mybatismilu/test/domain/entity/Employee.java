package com.yuehuanghun.mybatismilu.test.domain.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.yuehuanghun.mybatis.milu.annotation.alias.id.SnowflakeId;

import lombok.Data;

@Data
@Table(catalog = "demo2")
@Entity
public class Employee {
	@SnowflakeId
	private Long id;
	
	private Long companyId;
	
	private String name;
	
	@ManyToOne
	@JoinColumn(name = "company_id", referencedColumnName = "id")
	private Company company;
}
