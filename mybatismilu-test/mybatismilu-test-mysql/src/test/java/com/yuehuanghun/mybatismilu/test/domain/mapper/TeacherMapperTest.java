package com.yuehuanghun.mybatismilu.test.domain.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
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
		List<TeacherDTO> teacherList = teacherMapper.findByAge(31);
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
		
		PageHelper.startPage(1, 10, "addTime DESC");
		list = teacherMapper.findByAgeLessThan(100);
		assertTrue(list.size() == 2);
		assertNull(list.get(0).getAddTime());
		
		PageHelper.startPage(1, 10, "add_time DESC");
		list = teacherMapper.findByAgeLessThan(100);
		assertTrue(list.size() == 2);
		assertNull(list.get(0).getAddTime());
	}
	
	@Test
	@Transactional
	public void testFindByIdWithLock() {
		Teacher teacher = teacherMapper.findByIdWithLock(1L);
		assertNotNull(teacher);
		
		teacher.setAge(teacher.getAge() + 1);
		
		teacherMapper.updateById(teacher);
	}
	
	@Test
	@Transactional
	public void testFindByIdWithShareLock() {
		Teacher teacher = teacherMapper.findByIdWithShareLock(1L);
		assertNotNull(teacher);
		
		teacher.setAge(teacher.getAge() + 1);
		
		teacherMapper.updateById(teacher);
	}
	
	@Test
	@Transactional
	public void testFindByCriteriaWithLock() {
		List<Teacher> list = teacherMapper.findByCriteria(p -> p.eq("id", 1L).lock(LockModeType.PESSIMISTIC_WRITE));
		assertTrue(list.size() == 1);
		
		Teacher teacher = list.get(0);
		
		teacher.setAge(teacher.getAge() + 1);
		
		teacherMapper.updateById(teacher);
	}
	
	@Test
	@Transactional
	public void testFindByLambdaCriteriaWithLock() {
		List<Teacher> list = teacherMapper.findByLambdaCriteria(p -> p.eq(Teacher::getId, 1L).lock(LockModeType.PESSIMISTIC_WRITE));
		assertTrue(list.size() == 1);
		
		Teacher teacher = list.get(0);
		
		teacher.setAge(teacher.getAge() + 1);
		
		teacherMapper.updateById(teacher);
	}
	
	@Test
	@Transactional
	public void testUpdateWithOptimisticLock() { //乐观锁
		Optional<Teacher> teacherOpt = teacherMapper.findById(1L);
		assertTrue(teacherOpt.isPresent());
		
		Teacher teacher = teacherOpt.get();
		Teacher conTeacher = new Teacher();
		BeanUtils.copyProperties(teacher, conTeacher);
		
		teacher.setAge(teacher.getAge() + 1);
		
		int result = teacherMapper.updateById(teacher);
		assertTrue(result == 1);
		
		conTeacher.setAge(teacher.getAge() + 1);
		result = teacherMapper.updateById(conTeacher); //版本号未更新，更新失败
		assertTrue(result == 0);
	}
	
	@Test
	public void testFindByCriteria() {
		List<Teacher> teachers = teacherMapper.findByCriteria(p -> p.neq("id", 1L));
		System.out.println(JSON.toJSONString(teachers));
	}
	
	@Test
	@Transactional
	public void testLogicDelete() {
		int effect = teacherMapper.logicDeleteById(1L);
		
		assertEquals(effect, 1);
		
		Optional<Teacher> techer = teacherMapper.findById(1L);
		assertEquals(techer.get().getIsDeleted(), "YES");
	}
	
	@Test
	@Transactional
	public void testResumeLogicDelete() {
		int effect = teacherMapper.resumeLogicDeletedById(2L);
		
		assertEquals(effect, 1);
		
		Optional<Teacher> techer = teacherMapper.findById(2L);
		assertEquals(techer.get().getIsDeleted(), "NO");
	}
}
