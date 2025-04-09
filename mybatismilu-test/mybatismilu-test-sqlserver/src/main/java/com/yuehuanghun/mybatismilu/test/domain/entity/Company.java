package com.yuehuanghun.mybatismilu.test.domain.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.yuehuanghun.mybatis.milu.annotation.alias.id.SnowflakeId;

import lombok.Data;

@Data
@Table(catalog = "${db2}")
@Entity
public class Company {

	@SnowflakeId
	private Long id;
	
	private String companyName;
	
	@OneToMany(mappedBy = "company")
	private List<Employee> employees;
}
