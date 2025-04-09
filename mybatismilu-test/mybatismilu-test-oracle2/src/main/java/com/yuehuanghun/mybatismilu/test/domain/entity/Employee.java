package com.yuehuanghun.mybatismilu.test.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.yuehuanghun.mybatis.milu.annotation.alias.id.SnowflakeId;

import lombok.Data;

@Data
@Table(name = "EMPLOYEE")
@Entity
public class Employee {
	@SnowflakeId
	@Column(name = "ID")
	private Long id;

	@Column(name = "COMPANY_ID")
	private Long companyId;

	@Column(name = "NAME")
	private String name;
	
	@ManyToOne
	@JoinColumn(name = "COMPANY_ID", referencedColumnName = "ID")
	private Company company;
}
