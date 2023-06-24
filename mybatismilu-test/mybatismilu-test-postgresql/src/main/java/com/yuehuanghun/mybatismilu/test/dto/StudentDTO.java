package com.yuehuanghun.mybatismilu.test.dto;

import com.yuehuanghun.mybatismilu.test.domain.entity.Student;

import lombok.Getter;
import lombok.Setter;

public class StudentDTO extends Student {

	@Getter
	@Setter
	private String more;
}
