package com.yuehuanghun.mybatismilu.test.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.alibaba.fastjson.JSON;
import com.yuehuanghun.AppTest;
import com.yuehuanghun.mybatismilu.test.domain.entity.TeachingPlan;

@SpringBootTest(classes = AppTest.class)
public class TeachingPlanMapperTest {
	@Autowired
	private TeachingPlanMapper teachingPlanMapper;

	@Test
	public void findAllRef() {
		List<TeachingPlan> plans = teachingPlanMapper.findByCriteria(p -> p.select("*", "attachments*"));
		System.out.println(JSON.toJSONString(plans));
		assertEquals(2, plans.size());
		
		assertNotNull(plans.get(0).getAttachments());
		
		assertEquals(2, plans.get(0).getAttachments().size());
	}
	
	@Test
	public void findRefById() {
		List<TeachingPlan> plans = teachingPlanMapper.findByCriteria(p -> p.select("*", "attachments*").eq("id", 1));
		System.out.println(JSON.toJSONString(plans));
		assertEquals(1, plans.size());
		
		assertNotNull(plans.get(0).getAttachments());
		
		assertEquals(2, plans.get(0).getAttachments().size());
	}
	
	@Test
	public void findRefAttr() {
		List<TeachingPlan> plans = teachingPlanMapper.findByCriteria(p -> p.eq("attachmentsFileName", "一年级语文上册"));
		System.out.println(JSON.toJSONString(plans));
	}
}
