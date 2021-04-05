package com.yuehuanghun.mybatismilu.test.domain.mapper;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.yuehuanghun.AppTest;
import com.yuehuanghun.mybatismilu.test.domain.entity.Teacher;

@SpringBootTest(classes = AppTest.class)
@RunWith(SpringRunner.class)
public class TeacherMapperTest {
	@Autowired
	private TeacherMapper teacherMapper;

	@Test
	public void testFindByClassListName() {
		List<Teacher> result = teacherMapper.findByClassListName("一年级");
		
		assertTrue(result.size() == 2);
	}
}
