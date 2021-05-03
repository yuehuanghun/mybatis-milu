package com.yuehuanghun.mybatismilu.test.dto;

import java.util.Date;

import lombok.Data;

@Data
public class TeacherDTO {
	private Long id;
	
	private Date addTime;
	
	private String name;
	
	private Integer age;
}
