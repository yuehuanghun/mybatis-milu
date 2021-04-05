package com.yuehuanghun.mybatismilu.test.domain.mapper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.yuehuanghun.AppTest;
import com.yuehuanghun.mybatis.milu.data.Sort;
import com.yuehuanghun.mybatis.milu.data.Sort.Direction;
import com.yuehuanghun.mybatismilu.test.domain.entity.Student;

@SpringBootTest(classes = AppTest.class)
@RunWith(SpringRunner.class)
public class StudentMapperTest {
	@Autowired
	private StudentMapper studentMapper;

	@Test
	public void testFindById() {
		Optional<Student> result = studentMapper.findById(2L);
		assertTrue(result.isPresent());
	};

	@Test
	public void testFindByIds() {
		List<Student> result = studentMapper.findByIds(Arrays.asList(2L, 3L));
		assertTrue(result.size() == 2);
	};

	@Test
	public void testFindAll() {
		List<Student> result = studentMapper.findAll();
		assertTrue(result.size() > 0);
	};

	@Test
	public void testFindAllAndSort() {
		List<Student> result = studentMapper.findAllAndSort(new Sort(Direction.DESC, "addTime"));
		assertTrue(result.size() > 0);
	};

	@Test
	public void testFindByExample() {
		Student example = new Student();
		example.setName("张三");
		List<Student> result = studentMapper.findByExample(example);
		assertTrue(result.size() > 0);
	};

	@Test
	public void testFindByExampleAndSort() {
		Student example = new Student();
		example.setName("张");
		
		Sort sort = new Sort(Direction.DESC, "addTime");
		
		List<Student> result = studentMapper.findByExampleAndSort(example, sort);
		assertTrue(result.size() > 0);
	};

	@Test
	@Transactional
	public void testInsert() {
		Student student = new Student();
		student.setAddTime(new Date());
		student.setAge(9);
		student.setClassId(1L);
		student.setName(randomName());
		
		int result = studentMapper.insert(student);
		assertTrue(result == 1);
	};

	@Test
	@Transactional
	public void testBatchInsert() {
		List<Student> list = new ArrayList<>();
		Student student = new Student();
		student.setAddTime(new Date());
		student.setAge(9);
		student.setClassId(1L);
		student.setName(randomName());
		
		list.add(student);
		
		student = new Student();
		student.setAddTime(new Date());
		student.setAge(10);
		student.setClassId(1L);
		student.setName(randomName());
		
		list.add(student);
		
		int result = studentMapper.batchInsert(list);
		assertTrue(result == 2);
		assertNotNull(student.getId());
	};

	@Test
	@Transactional
	public void testUpdateById() {
		Student student = new Student();
		student.setAge(10);
		student.setId(2L);
		int result = studentMapper.updateById(student);
		assertTrue(result == 1);
	};

	@Test
	@Transactional
	public void testDeleteById() {
		int result = studentMapper.deleteById(2L);
		assertTrue(result == 1);
	};

	@Transactional
	@Test
	public void testDeleteByIds() {
		int result = studentMapper.deleteByIds(Arrays.asList(2L, 3L));
		assertTrue(result == 2);
	};

	@Test
	public void testCountByExample() {
		Student example = new Student();
		example.setName("张三");
		int result = studentMapper.countByExample(example);
		assertTrue(result == 1);
	};

	@Test
	public void testFindByName() {
		List<Student> result = studentMapper.findByName("张三");
		assertTrue(result.size() == 1);
	}

	@Test
	public void testCountByName() {
		int result = studentMapper.countByName("张三");
		assertTrue(result == 1);
	}
	
	@Test
	public void testFindByClasssName() {
		List<Student> result = studentMapper.findByClasssName("一年级");
		assertTrue(result.size() > 0);
	}
	
	@Test
	public void testFindTop5ByAddTimeAfter() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2020);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DATE, 1);
		studentMapper.findTop5ByAddTimeAfter(cal.getTime());
	}
	
	private static String randomName() {
		return new String(new char[] {getRandomChar(), getRandomChar()});
	}
	
	private static char getRandomChar() {
        return (char) (0x4e00 + (int) (Math.random() * (0x9fa5 - 0x4e00 + 1)));
    }
	
	@Test
	public void testFindByClasssNameOrderByClasssIdDescAddTimeAsc() {
		List<Student> result = studentMapper.findByClasssNameOrderByClasssIdDescAddTimeAsc("一年级");
		assertTrue(result.size() > 0);
	}
}
