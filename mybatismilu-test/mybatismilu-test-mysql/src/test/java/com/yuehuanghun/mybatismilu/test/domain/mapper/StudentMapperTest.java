package com.yuehuanghun.mybatismilu.test.domain.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yuehuanghun.AppTest;
import com.yuehuanghun.mybatis.milu.annotation.JoinMode;
import com.yuehuanghun.mybatis.milu.criteria.LambdaUpdatePredicate;
import com.yuehuanghun.mybatis.milu.criteria.QueryPredicateImpl;
import com.yuehuanghun.mybatis.milu.data.Sort;
import com.yuehuanghun.mybatis.milu.data.Sort.Direction;
import com.yuehuanghun.mybatis.milu.ext.Pair;
import com.yuehuanghun.mybatis.milu.pagehelper.PageRequest;
import com.yuehuanghun.mybatismilu.test.domain.entity.Student;
import com.yuehuanghun.mybatismilu.test.dto.StudentDTO;
import com.yuehuanghun.mybatismilu.test.dto.StudentStatistic;
import com.yuehuanghun.mybatismilu.test.service.StudentService;

@SpringBootTest(classes = AppTest.class)
@RunWith(SpringRunner.class)
public class StudentMapperTest {
	@Autowired
	private StudentMapper studentMapper;
	@Autowired
	private StudentService studentService;

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
		
		PageHelper.startPage(1, 10, "addTime DESC, age");
		
		result = studentMapper.findAll();
		assertTrue(result.size() > 0);
		
        PageHelper.startPage(1, 10, "updateTime DESC, age");
		
		result = studentMapper.findAll();
		assertTrue(result.size() > 0);
	};

	@Test
	public void testFindAllAndSort() {
		List<Student> result = studentMapper.findAllAndSort(new Sort(Direction.DESC, "addTime"));
		assertTrue(result.size() > 0);
	};

	@Test
	public void testFindByExample() throws ParseException {
		Student example = new Student();
		example.setName("张三");
		List<Student> result = studentMapper.findByExample(example);
		assertTrue(result.size() > 0);
		
		example = new Student();
		example.setName("");
		example.setAge(7);
		result = studentMapper.findByExample(example);
		assertTrue(result.size() == 1);
		
		example = new Student();
		Date startDate = DateUtils.parseDate("2017-06-08", "yyyy-MM-dd");
		Date endDate = new Date();
		Map<String, Object> params = new HashMap<>();
		params.put("addTimeBegin", startDate);
		params.put("addTimeEnd", endDate);
		example.setParams(params);
		
		result = studentMapper.findByExample(example);
		assertTrue(result.size() == 4);
		
		params.put("addTimeBegin", "2017-06-08");
		params.put("addTimeEnd", "");
		result = studentMapper.findByExample(example);
		assertTrue(result.size() == 4);
		
		params.clear();;
		params.put("nameIn", new String[] {"张三", "李四"});
		result = studentMapper.findByExample(example);
		assertTrue(result.size() == 2);
		
		params.clear();;
		params.put("nameIn", "张三, 李四");
		result = studentMapper.findByExample(example);
		assertTrue(result.size() == 2);
	};
	
	@Test
	public void testFindByExample2() throws ParseException {
		Student example = new Student();
		example.setName("张");
		
		Sort sort = new Sort(Direction.DESC, "addTime");
		
		List<Student> result = studentMapper.findByExample(example, sort);
		assertTrue(result.size() > 0);
		
		sort = Sort.by(Direction.DESC, Student::getAddTime);
		
		result = studentMapper.findByExample(example, sort);
		assertTrue(result.size() > 0);
		
		example = new Student();
		Date startDate = DateUtils.parseDate("2017-06-08", "yyyy-MM-dd");
		Date endDate = new Date();
		Map<String, Object> params = new HashMap<>();
		params.put("addTimeBegin", startDate);
		params.put("addTimeEnd", endDate);
		example.setParams(params);
		
		result = studentMapper.findByExample(example, sort);
		assertTrue(result.size() == 4);
		
		params.put("addTimeBegin", "2017-06-08");
		params.put("addTimeEnd", "");
		result = studentMapper.findByExample(example, sort);
		assertTrue(result.size() == 4);	

		result = studentMapper.findByExample(example, sort, new PageRequest(1), "class");
		assertTrue(result.size() == 1);
		assertNotNull(result.get(0).getClasss());
	}
	
	@Test
	public void testFindUniqueByExample() throws ParseException {
		Student example = new Student();
		example.setId(1L);
		Student student = studentMapper.findUniqueByExample(example, null);
		assertNotNull(student);
		
	}

	@Test
	public void testFindByExampleAndSort() throws ParseException {
		Student example = new Student();
		example.setName("张");
		
		Sort sort = new Sort(Direction.DESC, "addTime");
		
		List<Student> result = studentMapper.findByExampleAndSort(example, sort);
		assertTrue(result.size() > 0);
		
		sort = Sort.by(Direction.DESC, Student::getAddTime);
		
		result = studentMapper.findByExampleAndSort(example, sort);
		assertTrue(result.size() > 0);
		
		example = new Student();
		Date startDate = DateUtils.parseDate("2017-06-08", "yyyy-MM-dd");
		Date endDate = new Date();
		Map<String, Object> params = new HashMap<>();
		params.put("addTimeBegin", startDate);
		params.put("addTimeEnd", endDate);
		example.setParams(params);
		
		result = studentMapper.findByExampleAndSort(example, sort);
		assertTrue(result.size() == 4);
		
		params.put("addTimeBegin", "2017-06-08");
		params.put("addTimeEnd", "");
		result = studentMapper.findByExampleAndSort(example, sort);
		assertTrue(result.size() == 4);
	};
	
	@Test
	public void testFindUniqueByExampleAndSort() {
		Student example = new Student();
		Sort sort = new Sort(Direction.DESC, "id");
		
		Student student = studentMapper.findUniqueByExampleAndSort(example, sort, new PageRequest(1));
		
		assertEquals(student.getId().longValue(), 5L);
	}

	@Test
	@Transactional
	public void testInsert() {
		Student student = new Student();
		student.setAge(9);
		student.setClassId(1L);
		student.setName(randomName());
		
		int result = studentMapper.insert(student);
		assertTrue(result == 1);
		assertNotNull(student.getAddTime());
	};

	@Test
	@Transactional
	public void testBatchInsert() {
		List<Student> list = new ArrayList<>();
		Student student = new Student();
		student.setAge(9);
		student.setClassId(1L);
		student.setName(randomName());
		
		list.add(student);
		
		student = new Student();
		student.setAge(10);
		student.setClassId(1L);
		student.setName(randomName());
		
		list.add(student);
		
		int result = studentMapper.batchInsert(list);
		assertTrue(result == 2);
		assertNotNull(student.getId());
		assertNotNull(student.getAddTime());
	};

	@Test
	@Transactional
	public void testUpdateById() {
		Student student = new Student();
		student.setAge(10);
		student.setId(2L);
		Date now = new Date();
		int result = studentMapper.updateById(student);
		System.out.println(JSON.toJSONString(student));
		assertTrue(result == 1);
		assertNotNull(student.getUpdateTime());
		assertTrue(now.compareTo(student.getUpdateTime()) <= 0);
		assertNull(student.getAddTime());
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
	public void testCountByExample() throws ParseException {
		Student example = new Student();
		example.setName("张三");
		int result = studentMapper.countByExample(example);
		assertTrue(result == 1);
		
		example = new Student();
		Date startDate = DateUtils.parseDate("2017-06-08", "yyyy-MM-dd");
		Date endDate = new Date();
		Map<String, Object> params = new HashMap<>();
		params.put("addTimeBegin", startDate);
		params.put("addTimeEnd", endDate);
		example.setParams(params);
		
		result = studentMapper.countByExample(example);
		assertTrue(result == 4);
		
		params.put("addTimeBegin", "2017-06-08");
		params.put("addTimeEnd", "");
		result = studentMapper.countByExample(example);
		assertTrue(result == 4);
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
		List<Map<String, Object>> list = studentMapper.sumAgeAvgAgeCountIdByUpdateTimeGroupByClassIdOrderByClassId(updateTime);
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
		
		result = studentMapper.statisticByCriteria(p -> p.sum("age").avg("age").count("id").groupByAs("classId", "class").orderAsc("classId"));
		assertTrue(result.size() > 0);
		System.out.println(JSON.toJSONString(result));
		
		result = studentMapper.statisticByLambdaCriteria(p -> p.sum(Student::getAge).avg(Student::getAge).count(Student::getId).groupBy(Student::getClassId).orderAsc(Student::getClassId));
		assertTrue(result.size() > 0);
		System.out.println(JSON.toJSONString(result));
		
		result = studentMapper.statisticByCriteria(p -> p.sum("age").avg("age").count("id").undeleted().groupBy("classId").orderAsc("classId"));
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
	public void testFindByExample_pageInEntity() {
		Student example = new Student();
		List<Student> studentList = studentMapper.findByExample(example);
		
		assertTrue(studentList.size() == 5);
		assertTrue(!Page.class.isInstance(studentList));
		
		example.setPageNum(1);
		example.setPageSize(3);
		studentList = studentMapper.findByExample(example);
		assertTrue(Page.class.isInstance(studentList));
		
		assertTrue(studentList.size() == 3);
		
		example.setName("王");
		studentList = studentMapper.findByExample(example);
		
		assertTrue(Page.class.isInstance(studentList));		
		assertTrue(studentList.size() == 2);
		assertTrue(studentList.get(0).getName().startsWith("王"));
	}
	
	@Test
	public void testfindByNameStartsWith_page() {
		List<Student> studentList = studentMapper.findByNameStartsWith("王", new PageRequest(10));
		assertTrue(studentList.size() == 2);
		assertTrue(studentList.get(0).getName().startsWith("王"));
	}
	
	@Test
	public void testFindByCateria_ref() {
		List<Student> list = studentMapper.findByCriteria(p -> p.select("*", "classs*").eq("id", 1L));
		System.out.println(JSON.toJSONString(list));
		assertEquals(1, list.size());
		assertNotNull(list.get(0).getClasss());
		
		list = studentMapper.findByCriteria(p -> p.select("*", "classs*").eq("id", 1L).limit(1));
		System.out.println(JSON.toJSONString(list));
		assertEquals(1, list.size());
		assertNotNull(list.get(0).getClasss());
	}
	
	@Test
	public void testFindByCateria_ref2() {
		List<Student> list = studentMapper.findByCriteria(p -> p.select("*", "classs*", "studentProfile*").eq("id", 1L));
		System.out.println(JSON.toJSONString(list));
		assertEquals(1, list.size());
		assertNotNull(list.get(0).getClasss());
		assertNotNull(list.get(0).getStudentProfile());
	}
	
	@Test
	public void testFindByCateria_ref3() {
		List<Student> list = studentMapper.findByCriteria(p -> p.select("id","addTime", "classsId", "classsName").eq("id", 1L));
		System.out.println(JSON.toJSONString(list));
		assertEquals(1, list.size());
		assertNotNull(list.get(0).getAddTime());
		assertNull(list.get(0).getName());
		assertNotNull(list.get(0).getClasss());
		assertNotNull(list.get(0).getClasss().getName());
		assertNull(list.get(0).getClasss().getAddTime());
	}
	
	@Test
	public void testFindByCateria_ref4() {
		List<Student> list = studentMapper.findByCriteria(p -> p.select("*", "classs*"));
		System.out.println(JSON.toJSONString(list));
		assertEquals(5, list.size());
		assertNotNull(list.get(0).getClasss());
	}
	
	@Test
	public void testFindByCateria_ref4_joinMode() {
		List<Student> list = studentMapper.findByCriteria(p -> p.joinMode(JoinMode.LEFT_JOIN).select("*", "classs*"));
		System.out.println(JSON.toJSONString(list));
		assertEquals(5, list.size());
		assertNotNull(list.get(0).getClasss());
	}
	
	@Test
	public void testFindByCateria_ref5() {
		List<Student> list = studentMapper.findByCriteria(p -> p.select("classs*").eq("id", 1L));
		System.out.println(JSON.toJSONString(list));
		assertEquals(1, list.size());
		assertNull(list.get(0).getId());
		assertNotNull(list.get(0).getClasss());
	}
	
	@Test
	public void testFindByCateria_ref5_joinMode() {
		List<Student> list = studentMapper.findByCriteria(p -> p.joinMode("classs", JoinMode.LEFT_JOIN).select("classs*").eq("id", 1L));
		System.out.println(JSON.toJSONString(list));
		assertEquals(1, list.size());
		assertNull(list.get(0).getId());
		assertNotNull(list.get(0).getClasss());
	}
	
	@Test
	public void testFindByLambdaCateria_ref_joinMode() {
		List<Student> list = studentMapper.findByLambdaCriteria(p -> p.joinMode(JoinMode.LEFT_JOIN).selects("*,classs*"));
		System.out.println(JSON.toJSONString(list));
		assertEquals(5, list.size());
		assertNotNull(list.get(0).getClasss());
	}
	
	@Test
	@Transactional
	public void testUpdateAttrByCriteria() {
		int effect = studentMapper.updateAttrByCriteria("name", "狗剩", p -> p.eq("id", 1L));
		assertEquals(effect, 1);
		
		effect = studentMapper.updateAttrByCriteria("name", null, p -> p.eq("id", 1L));
		assertEquals(effect, 1);
		
		Optional<Student> classs = studentMapper.findById(1L);
		assertNull(classs.get().getName());
	}
	
	@Test
	@Transactional
	public void testUpdateAttrByLambdaCriteria() {
		int effect = studentMapper.updateAttrByLambdaCriteria(Student::getName, "狗剩", p -> p.eq(Student::getId, 1L));
		assertEquals(effect, 1);
		
		effect = studentMapper.updateAttrByLambdaCriteria(Student::getName, null, p -> p.eq(Student::getId, 1L));
		assertEquals(effect, 1);
		
		Optional<Student> classs = studentMapper.findById(1L);
		assertNull(classs.get().getName());
	}
	
	@Test
	@Transactional
	public void testEntitySubClass() {
		StudentDTO student = new StudentDTO();
		student.setClassId(1L);
		student.setName("王大柱");
		student.setAge(38);
		
		studentMapper.insert(student);
		
		assertNotNull(student.getAddTime());
		assertNotNull(student.getUpdateTime());
	}
	
	@Test
	@Transactional
	public void testLogicDelete() {
		int effect = studentMapper.logicDeleteById(1L);
		
		assertEquals(effect, 1);
		
		Optional<Student> student = studentMapper.findById(1L);
		assertTrue(student.get().getIsDeleted());
	}
	
	@Test
	@Transactional
	public void testResumeLogicDelete() {
		int effect = studentMapper.resumeLogicDeletedById(1L);
		
		assertEquals(effect, 1);
		
		Optional<Student> student = studentMapper.findById(1L);
		assertTrue(!student.get().getIsDeleted());
	}
	
	@Test
	@Transactional
	public void testLogicDeleteByIds() {
		int effect = studentMapper.logicDeleteByIds(Arrays.asList(1L, 2L));
		
		assertEquals(effect, 2);
		
		Optional<Student> student = studentMapper.findById(1L);
		assertTrue(student.get().getIsDeleted());
		
		student = studentMapper.findById(2L);
		assertTrue(student.get().getIsDeleted());
	}
	
	@Test
	@Transactional
	public void testResumeLogicDeleteByIds() {
		int effect = studentMapper.resumeLogicDeletedByIds(Arrays.asList(1L, 2L));
		
		assertEquals(effect, 2);
		
		Optional<Student> student = studentMapper.findById(1L);
		assertTrue(!student.get().getIsDeleted());
		
		student = studentMapper.findById(2L);
		assertTrue(!student.get().getIsDeleted());
	}
	
	@Test
	@Transactional
	public void testLogicDeleteByCriteria() {
		int effect = studentMapper.logicDeleteByCriteria(p -> p.in("id", Arrays.asList(1L, 2L)));
		
		assertEquals(effect, 2);
		
		Optional<Student> student = studentMapper.findById(1L);
		assertTrue(student.get().getIsDeleted());
		
		student = studentMapper.findById(2L);
		assertTrue(student.get().getIsDeleted());
	}
	
	@Test
	@Transactional
	public void testLogicDeleteByLambdaCriteria() {
		int effect = studentMapper.logicDeleteByLambdaCriteria(p -> p.in(Student::getId, Arrays.asList(1L, 2L)));
		
		assertEquals(effect, 2);
		
		Optional<Student> student = studentMapper.findById(1L);
		assertTrue(student.get().getIsDeleted());
		
		student = studentMapper.findById(2L);
		assertTrue(student.get().getIsDeleted());
	}
	
	@Test
	@Transactional
	public void testResumeLogicDeletedByCriteria() {
		int effect = studentMapper.resumeLogicDeletedByCriteria(p -> p.in("id", Arrays.asList(1L, 2L)));
		
		assertEquals(effect, 2);
		
		Optional<Student> student = studentMapper.findById(1L);
		assertTrue(!student.get().getIsDeleted());
		
		student = studentMapper.findById(2L);
		assertTrue(!student.get().getIsDeleted());
	}
	
	@Test
	@Transactional
	public void testResumeLogicDeletedByLambdaCriteria() {
		int effect = studentMapper.resumeLogicDeletedByLambdaCriteria(p -> p.in(Student::getId, Arrays.asList(1L, 2L)));
		
		assertEquals(effect, 2);
		
		Optional<Student> student = studentMapper.findById(1L);
		assertTrue(!student.get().getIsDeleted());
		
		student = studentMapper.findById(2L);
		assertTrue(!student.get().getIsDeleted());
	}
	
	@Test
	public void testMultiSortOrder() {
		List<Student> list = studentMapper.findByLambdaCriteria(p -> p.orderDesc(Student::getAge, Student::getClassId));
		System.out.println(JSON.toJSONString(list));
		list = studentMapper.findByLambdaCriteria(p -> p.orderDesc(Student::getClassId, Student::getAge));
		System.out.println(JSON.toJSONString(list));
		
		list = studentMapper.findByLambdaCriteria(p -> p.orderAsc(Student::getClassId, Student::getAge));
		System.out.println(JSON.toJSONString(list));
		
		list = studentMapper.findByLambdaCriteria(p -> p.orderAsc(Student::getClassId, Student::getAge, Student::getName));
		System.out.println(JSON.toJSONString(list));
		
		Sort sort = Sort.asc(Student::getAge).andDesc(Student::getClassId);
		list = studentMapper.findAllAndSort(sort);
	}
	
	@Test
	public void testCriteriaSort() {
		List<Student> list = studentMapper.findByCriteria(p -> p.order(Sort.asc("classId")));
		System.out.println(JSON.toJSONString(list));
		
		System.out.println(studentMapper.getClass().getName());
		Field[] fields = studentMapper.getClass().getFields();
		System.out.println("fields : " + fields.length);
		for(Field field : fields) {
			System.out.println(field.getName());
		}
		
		list = studentMapper.findByLambdaCriteria(p -> p.order(Sort.desc(Student::getClassId)));
		System.out.println(JSON.toJSONString(list));
		
		list = studentMapper.findByLambdaCriteria(p -> p.order(Sort.desc(Student::getClassId, Student::getAge)));
		System.out.println(JSON.toJSONString(list));
	}
	
	@Test
	@Transactional
	public void testBatchSave() {
		List<Student> list = new ArrayList<>();
		
		for(int i = 0; i < 10; i++) {
			Student student = new Student();
			student.setAge(RandomUtils.nextInt(9, 11));
			student.setClassId(1L);
			student.setName(randomName());
			
			if(i > 5) {
				student.setIsDeleted(Boolean.FALSE);
			}
			
			list.add(student);
		}
		
		studentService.batchSave(list);
	}

	@Test
	@Transactional
	public void testBatchUpdateById() {
		List<Student> list = studentService.getAll();
		
		list.forEach(item -> item.setAge(20));
		
		studentService.batchUpdateById(list);
	}
	
	@Test
	@Transactional
	public void testBatchUpdateByLambdaCriteria() {
		List<Student> list = studentService.getAll();
		
		Collection<Pair<Student, Consumer<LambdaUpdatePredicate<Student>>>> entityAndPredicateList = list.stream().map(item -> {
			Pair<Student, Consumer<LambdaUpdatePredicate<Student>>> pair = Pair.of(item, p -> p.eq(Student::getId, item.getId()));
			return pair;
		}).collect(Collectors.toList());
		
		list.forEach(item -> {
			item.setAge(22);
			item.setIsDeleted(Boolean.TRUE);
		});
		
		studentService.batchUpdateByLambdaCriteria(entityAndPredicateList);
		
		Student updateParam1 = new Student();
		updateParam1.setAge(10);
		
		Student updateParam2 = new Student();
		updateParam2.setAge(11);
		updateParam2.setIsDeleted(Boolean.TRUE);
		
		Pair<Student, Consumer<LambdaUpdatePredicate<Student>>> pair1 = Pair.of(updateParam1, p -> p.lt(Student::getId, 3L).gt(Student::getId, 0L));
		
		Pair<Student, Consumer<LambdaUpdatePredicate<Student>>> pair2 = Pair.of(updateParam2, p -> p.gte(Student::getId, 3L));
		
		studentService.batchUpdateByLambdaCriteria(Arrays.asList(pair1, pair2));
	}
}
