package com.yuehuanghun.mybatismilu.test.domain.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.yuehuanghun.mybatis.milu.annotation.alias.id.SnowflakeId;

import lombok.Data;

@Data
@Table(name = "COMPANY")
@Entity
public class Company {

	@SnowflakeId
	@Column(name = "ID")
	private Long id;

	@Column(name = "COMPANY_NAME")
	private String companyName;
	
	@Column(name = "C_NAME")
	private String cName;
	
	@OneToMany(mappedBy = "company")
	private List<Employee> employees;
}
