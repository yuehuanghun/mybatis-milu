package com.yuehuanghun.mybatismilu.test.domain.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.yuehuanghun.AppTest;
import com.yuehuanghun.mybatis.milu.criteria.QueryPredicateImpl;
import com.yuehuanghun.mybatis.milu.data.Sort;
import com.yuehuanghun.mybatis.milu.data.Sort.Direction;
import com.yuehuanghun.mybatis.milu.pagehelper.PageRequest;
import com.yuehuanghun.mybatismilu.test.domain.entity.Student;
import com.yuehuanghun.mybatismilu.test.dto.StudentStatistic;

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
	

	@Test
	@Transactional
	public void testUpdateByCriteria() {
		Student student = new Student();
		student.setAge(10);
		int result = studentMapper.updateByCriteria(student, p -> p.eq("id", 2L));
		assertTrue(result == 1);
		assertNotNull(student.getUpdateTime());
		assertNull(student.getAddTime());
	};
	
	@Test
	public void testCountByCountByCriteria() {
		int result = studentMapper.countByCriteria(p -> p.eq("name", "张三"));
		assertTrue(result == 1);
		
		result = studentMapper.countByCriteria(new QueryPredicateImpl().eq("name", "张三"));
		assertTrue(result == 1);
	}
	
	@Test
	public void testCountByLambdaCriteria() {
		int result = studentMapper.countByLambdaCriteria(p -> p.eq(Student::getName, "张三"));
		assertTrue(result == 1);
	}
	
	@Test
	public void testFindByUpdateTimeNotNull() {
		List<Student> list = studentMapper.findByUpdateTimeNotNull();
		assertTrue(list.size() == 5);
	}
	
	@Test
	public void testSumAgeAvgAgeCountIdByGroupByClassId() {
		List<Map<String, Object>> list = studentMapper.sumAgeAvgAgeCountIdByGroupByClassId();
		System.out.println(JSON.toJSONString(list));
		assertTrue(list.size() > 0);
	}
	
	@Test
	public void testSumAgeAvgAgeCountIdBy() {
		Map<String, Object> map = studentMapper.sumAgeAvgAgeCountIdBy();
		System.out.println(JSON.toJSONString(map));
		assertNotNull(map);
	}
	
	@Test
	public void testSumAgeAvgAgeCountIdByGroupByClassIdAndUpdateTime() {
		List<Map<String, Object>> list = studentMapper.sumAgeAvgAgeCountIdByGroupByClassIdAndUpdateTime();
		System.out.println(JSON.toJSONString(list));
		System.out.println(list.get(0).get("updateTime").getClass().getName());
		assertTrue(list.size() > 0);
	}
	
	@Test
	public void testSumAgeAvgAgeCountIdByUpdateTimeGroupByClassIdOrderByClassId() {
		Date updateTime = new Date(1625402486000L);
		List<Map<String, Object>> list = studentMapper.sumAgeAvgAgeCountIdByUpdateTimeGreaterThanGroupByClassIdOrderByClassId(updateTime);
		System.out.println(JSON.toJSONString(list));
		assertTrue(list.size() > 0);
	}
		
	@Test
	public void testMinAgeCountIdByGroupByClassIdOrderByClasssName() {
		List<Map<String, Object>> list = studentMapper.minAgeCountIdByGroupByClassIdOrderByClassId();
		System.out.println(JSON.toJSONString(list));
		assertTrue(list.size() > 0);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testStatisticByCriteria() {
		List<Map<String, Object>> result = studentMapper.statisticByCriteria(p -> p.sum("age").avg("age").count("id").groupBy("classId").orderAsc("classId"));
		assertTrue(result.size() > 0);
		System.out.println(JSON.toJSONString(result));
		
		result = studentMapper.statisticByLambdaCriteria(p -> p.sum(Student::getAge).avg(Student::getAge).count(Student::getId).groupBy(Student::getClassId).orderAsc(Student::getClassId));
		assertTrue(result.size() > 0);
		System.out.println(JSON.toJSONString(result));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testStatisticByCriteriaDynamic() {
		List<StudentStatistic> result = studentMapper.statisticByCriteria(p -> p.sum("age").avg("age").count("id").groupBy("classId").orderAsc("classId"), StudentStatistic.class);
		assertTrue(result.size() > 0);
		assertEquals(result.get(0).getClass(), StudentStatistic.class);
		System.out.println(result.get(0).getClass().getName());
		System.out.println(JSON.toJSONString(result));
		
		result = studentMapper.statisticByLambdaCriteria(p -> p.sum(Student::getAge).avg(Student::getAge).count(Student::getId).groupBy(Student::getClassId).orderAsc(Student::getClassId), StudentStatistic.class);
		assertTrue(result.size() > 0);
		assertEquals(result.get(0).getClass(), StudentStatistic.class);
	}

	@Test
	@Transactional
	public void testUpdateAttrByIdProvider() {
		int effect = studentMapper.updateAttrById("name", "李五", 1L);
		assertTrue(effect == 1);
		Optional<Student> student = studentMapper.findById(1L);
		assertTrue(student.isPresent() && "李五".equals(student.get().getName()));
		
		effect = studentMapper.updateAttrById(Student::getName, "赵四", 1L);
		assertTrue(effect == 1);
		student = studentMapper.findById(1L);
		assertTrue(student.isPresent() && "赵四".equals(student.get().getName()));
		
		
		effect = studentMapper.updateAttrById(Student::getAge, 23, 1L);
		assertTrue(effect == 1);
		student = studentMapper.findById(1L);
		assertTrue(student.isPresent() && student.get().getAge() == 23);
	}
	
	@Test
	public void testFindByExample_page() {
		Student example = new Student();
		List<Student> studentList = studentMapper.findByExample(example, new PageRequest(1));
		
		assertTrue(studentList.size() == 1);
		
		example.setName("王");
		studentList = studentMapper.findByExample(example, new PageRequest(3));
		
		assertTrue(studentList.size() == 2);
		assertTrue(studentList.get(0).getName().startsWith("王"));
	}
	
	@Test
	public void testfindByNameStartsWith_page() {
		List<Student> studentList = studentMapper.findByNameStartsWith("王", new PageRequest(10));
		assertTrue(studentList.size() == 2);
		assertTrue(studentList.get(0).getName().startsWith("王"));
	}
}
