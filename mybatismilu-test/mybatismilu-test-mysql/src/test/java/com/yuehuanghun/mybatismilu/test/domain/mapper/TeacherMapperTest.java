package com.yuehuanghun.mybatismilu.test.domain.mapper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON;
import com.yuehuanghun.AppTest;
import com.yuehuanghun.mybatismilu.test.domain.entity.Teacher;
import com.yuehuanghun.mybatismilu.test.dto.TeacherDTO;

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
	@Test
	public void testGetById() {
		Teacher teacher = teacherMapper.getById(1L);
		
		assertNotNull(teacher);
		
		System.out.println(JSON.toJSON(teacher));
	}
	@Test
	public void testFindByAge() {
		List<TeacherDTO> teacherList = teacherMapper.findByAge(30);
		assertTrue(teacherList.size() == 1);
		System.out.println(JSON.toJSONString(teacherList));
	}
	
	@Test
	public void testFindByAgeGreaterThan() {
		List<Long> list = teacherMapper.findTeacherIds(0);
		assertTrue(list.size() == 2);
		System.out.println(JSON.toJSONString(list));
	}
	
	@Test
	public void testFindByAgeLessThan() {
		List<Teacher> list = teacherMapper.findByAgeLessThan(100);
		assertTrue(list.size() == 2);
		assertNull(list.get(0).getAddTime());
	}
}
