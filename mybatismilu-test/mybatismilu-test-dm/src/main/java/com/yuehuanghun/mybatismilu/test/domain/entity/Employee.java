package com.yuehuanghun.mybatismilu.test.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.yuehuanghun.mybatis.milu.annotation.alias.id.SnowflakeId;

import lombok.Data;

@Data
@Table(schema = "DEMO2")
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
