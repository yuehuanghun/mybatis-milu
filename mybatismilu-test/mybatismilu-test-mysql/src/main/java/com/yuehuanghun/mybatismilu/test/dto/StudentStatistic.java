package com.yuehuanghun.mybatismilu.test.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class StudentStatistic {

	private Integer ageSum;
	
	private BigDecimal ageAvg;
	
	private Integer idCount;
	
	private Long classId;
}
