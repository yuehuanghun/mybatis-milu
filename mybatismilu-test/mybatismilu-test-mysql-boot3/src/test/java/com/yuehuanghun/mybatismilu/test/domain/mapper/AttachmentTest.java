package com.yuehuanghun.mybatismilu.test.domain.mapper;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.alibaba.fastjson.JSON;
import com.yuehuanghun.AppTest;
import com.yuehuanghun.mybatismilu.test.domain.entity.Attachment;

@SpringBootTest(classes = AppTest.class)
public class AttachmentTest {

	@Autowired
	private AttachmentMapper attachmentMapper;
	
	@Test
	public void findRefById() {
		List<Attachment> list = attachmentMapper.findByCriteria(p -> p.select("*", "plans*").eq("id", 1));
		
		System.out.println(JSON.toJSONString(list));
		
		assertEquals(list.size(), 1);
		
		assertEquals(list.get(0).getPlans().size(), 1);
	}
}
