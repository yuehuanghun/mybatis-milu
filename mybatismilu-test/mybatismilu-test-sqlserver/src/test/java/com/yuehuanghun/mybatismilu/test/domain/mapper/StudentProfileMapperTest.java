package com.yuehuanghun.mybatismilu.test.domain.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.yuehuanghun.AppTest;
import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.criteria.QueryPredicateImpl;
import com.yuehuanghun.mybatis.milu.data.Sort;
import com.yuehuanghun.mybatis.milu.data.Sort.Direction;
import com.yuehuanghun.mybatismilu.test.domain.entity.Student;
import com.yuehuanghun.mybatismilu.test.domain.entity.StudentProfile;

@SpringBootTest(classes = AppTest.class)
@RunWith(SpringRunner.class)
public class StudentProfileMapperTest {
	@Autowired
	private StudentMapper studentMapper;

	@Autowired
	private StudentProfileMapper studentProfileMapper;
	
	@Test
	public void testFindById() {
		Optional<StudentProfile> result = studentProfileMapper.findById(1L);
		assertTrue(result.isPresent());
	}
	
	@Test
	public void testFindByIds() {
		List<StudentProfile> result = studentProfileMapper.findByIds(Arrays.asList(1L, 2L));
		assertTrue(result.size() == 2);
	}
	
	@Test
	public void testFindAll() {
		List<StudentProfile> result = studentProfileMapper.findAll();
		assertTrue(result.size() > 0);
	}
	
	@Test
	public void findAllAndSort() {
		List<StudentProfile> result = studentProfileMapper.findAll(Sort.by(Direction.ASC, "addTime"));
		assertTrue(result.size() > 0);
	}
	
	@Test
	public void testFindByExample() {
		StudentProfile example = new StudentProfile();
		example.setFatherName("蓝标");
		List<StudentProfile> result = studentProfileMapper.findByExample(example);
		assertTrue(result.size() == 1);
	}
	
	@Test
	public void testFindByExampleAndSort() {
		StudentProfile example = new StudentProfile();
		example.setFatherName("蓝标");
		List<StudentProfile> result = studentProfileMapper.findByExample(example, Sort.by(Direction.DESC, "addTime"));
		assertTrue(result.size() > 0);
	}
	
	@Test
	@Transactional
	public void testInsert() {
		Student student = new Student();
		student.setAddTime(LocalDateTime.now());
		student.setClassId(2L);
		student.setName("张建国");
		student.setAge(9);
		
		int result = studentMapper.insert(student);
		
		assertTrue(result == 1);
		
		StudentProfile profile = new StudentProfile();
		profile.setFatherName("张爱民");
		profile.setFatherAge(43);
		profile.setMotherName("何兰芳");
		profile.setMotherAge(33);
		profile.setStudentId(student.getId());
		
		result = studentProfileMapper.insert(profile);
		
		assertTrue(result == 1);
	}
	
	@Test
	@Transactional
	public void testBatchInsert() {
		Student student = new Student();
		student.setAddTime(LocalDateTime.now());
		student.setClassId(2L);
		student.setName("张建国");
		student.setAge(9);
		
		Student student2 = new Student();
		student2.setAddTime(LocalDateTime.now());
		student2.setClassId(2L);
		student2.setName("刘兰之");
		student2.setAge(9);
		
		int result = studentMapper.batchInsert(Arrays.asList(student, student2));
		
		assertTrue(result == 2);
		
		StudentProfile profile = new StudentProfile();
		profile.setFatherName("张爱民");
		profile.setFatherAge(43);
		profile.setMotherName("何兰芳");
		profile.setMotherAge(33);
		profile.setStudentId(student.getId());
		
		StudentProfile profile2 = new StudentProfile();
		profile2.setFatherName("刘胡");
		profile2.setFatherAge(42);
		profile2.setMotherName("毛芬");
		profile2.setMotherAge(32);
		profile2.setStudentId(student2.getId());
		
		result = studentProfileMapper.batchInsert(Arrays.asList(profile, profile2));
		
		assertTrue(result == 2);
	}
	
	@Test
	@Transactional
	public void testUpdateById() {
		Student student = new Student();
		student.setAddTime(LocalDateTime.now());
		student.setClassId(2L);
		student.setName("张建国");
		student.setAge(9);
		
		int result = studentMapper.insert(student);
		
		assertTrue(result == 1);
		
		StudentProfile profile = new StudentProfile();
		profile.setFatherName("张爱民");
		profile.setFatherAge(43);
		profile.setMotherName("何兰芳");
		profile.setMotherAge(33);
		profile.setStudentId(student.getId());
		
		result = studentProfileMapper.insert(profile);
		
		assertTrue(result == 1);
		
		
		profile.setFatherAge(44);
		result = studentProfileMapper.updateById(profile);
	}
	
	@Test
	@Transactional
	public void testDeleteById() {
		Student student = new Student();
		student.setAddTime(LocalDateTime.now());
		student.setClassId(2L);
		student.setName("张建国");
		student.setAge(9);
		
		int result = studentMapper.insert(student);
		
		assertTrue(result == 1);
		
		result = studentMapper.deleteById(student.getId());
		
		assertTrue(result == 1);
	}
	
	@Test
	@Transactional
	public void testDeleteByIds() {
		Student student = new Student();
		student.setAddTime(LocalDateTime.now());
		student.setClassId(2L);
		student.setName("张建国");
		student.setAge(9);
		
		Student student2 = new Student();
		student2.setAddTime(LocalDateTime.now());
		student2.setClassId(2L);
		student2.setName("刘兰之");
		student2.setAge(9);
		
		int result = studentMapper.batchInsert(Arrays.asList(student, student2));
		
		assertTrue(result == 2);
		
		StudentProfile profile = new StudentProfile();
		profile.setFatherName("张爱民");
		profile.setFatherAge(43);
		profile.setMotherName("何兰芳");
		profile.setMotherAge(33);
		profile.setStudentId(student.getId());
		
		StudentProfile profile2 = new StudentProfile();
		profile2.setFatherName("刘胡");
		profile2.setFatherAge(42);
		profile2.setMotherName("毛芬");
		profile2.setMotherAge(32);
		profile2.setStudentId(student2.getId());
		
		result = studentProfileMapper.batchInsert(Arrays.asList(profile, profile2));
		assertTrue(result == 2);
		
		result = studentProfileMapper.deleteByIds(Arrays.asList(profile.getId(), profile2.getId()));
		
		assertTrue(result == 2);
	}
	
	@Test
	public void testCountByExample() {
		StudentProfile example = new StudentProfile();
		example.setFatherName("蓝标");
		int result = studentProfileMapper.countByExample(example);
		
		assertTrue(result == 1);
	}
	
	@Test
	public void testFindByCriteria() {
		List<StudentProfile> result = studentProfileMapper.findByCriteria(new QueryPredicateImpl().eq("fatherName", "蓝标"));
		assertTrue(result.size() == 1);
		
		result = studentProfileMapper.findByCriteria(predicate -> predicate.eq("fatherName", "蓝标"));
		assertTrue(result.size() == 1);
		
		result = studentProfileMapper.findByCriteria(predicate -> predicate.eq("fatherName", "蓝标").order(Direction.ASC, "addTime").limit(10));
		assertTrue(result.size() == 1);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testFindByLambdaCriteria() {
		StudentProfile param = new StudentProfile();
		param.setFatherName("蓝标");
		
		List<StudentProfile> result = studentProfileMapper.findByLambdaCriteria(p -> p.apply(param).eq(StudentProfile::getFatherName));
		
		assertTrue(result.size() == 1);
		
		result = studentProfileMapper.findByLambdaCriteria(p -> p.apply(param).eq(StudentProfile::getFatherName).order(StudentProfile::getAddTime).limit(10));
		
		assertTrue(result.size() == 1);
	}
	
	@Test
	@Transactional
	public void testUpdateByCriteria() {
		Student student = new Student();
		student.setAddTime(LocalDateTime.now());
		student.setClassId(2L);
		student.setName("张建国");
		student.setAge(9);
		
		int result = studentMapper.insert(student);
		
		assertTrue(result == 1);
		
		StudentProfile profile = new StudentProfile();
		profile.setFatherName("张爱民");
		profile.setFatherAge(43);
		profile.setMotherName("何兰芳");
		profile.setMotherAge(33);
		profile.setStudentId(student.getId());
		
		result = studentProfileMapper.insert(profile);
		
		assertTrue(result == 1);
		
		
		profile.setFatherAge(44);
		
		result = studentProfileMapper.updateByCriteria(profile, p -> p.eq("fatherName", "张爱民"));
		
		assertTrue(result == 1);
		
		assertTrue(profile.getMotherName() != null);
		
		profile.setMotherName(null); //通过Mode.ALL将为null的字段也更新到数据库
		int effect = studentProfileMapper.updateByCriteria(profile, p -> p.eq("id", profile.getId()).updateMode(Mode.ALL));
		assertEquals(effect, 1);
		Optional<StudentProfile> updated = studentProfileMapper.findById(profile.getId());
		assertTrue(updated.get().getMotherName() == null);
	}
	
	@Test
	@Transactional
	public void testUpdateByLambdaCriteria() {
		Student student = new Student();
		student.setAddTime(LocalDateTime.now());
		student.setClassId(2L);
		student.setName("张建国");
		student.setAge(9);
		
		int result = studentMapper.insert(student);
		
		assertTrue(result == 1);
		
		StudentProfile profile = new StudentProfile();
		profile.setFatherName("张爱民");
		profile.setFatherAge(43);
		profile.setMotherName("何兰芳");
		profile.setMotherAge(33);
		profile.setStudentId(student.getId());
		
		result = studentProfileMapper.insert(profile);
		
		assertTrue(result == 1);
		
		
		profile.setFatherAge(44);
		
		result = studentProfileMapper.updateByLambdaCriteria(profile, p -> p.apply(profile).eq(StudentProfile::getFatherName));
		
		assertTrue(result == 1);
	}
	
	
	@Test
	@Transactional
	public void testDeleteByCriteria() {
		Student student = new Student();
		student.setAddTime(LocalDateTime.now());
		student.setClassId(2L);
		student.setName("张建国");
		student.setAge(9);
		
		int result = studentMapper.insert(student);
		
		assertTrue(result == 1);
		
		StudentProfile profile = new StudentProfile();
		profile.setFatherName("张爱民");
		profile.setFatherAge(43);
		profile.setMotherName("何兰芳");
		profile.setMotherAge(33);
		profile.setStudentId(student.getId());
		
		result = studentProfileMapper.insert(profile);
		
		assertTrue(result == 1);
		
		
		result = studentProfileMapper.deleteByCriteria(p -> p.eq("fatherName", "张爱民"));
		
		assertTrue(result == 1);
	}
	
	@Test
	@Transactional
	public void testDeleteByLambdaCriteria() {
		Student student = new Student();
		student.setAddTime(LocalDateTime.now());
		student.setClassId(2L);
		student.setName("张建国");
		student.setAge(9);
		
		int result = studentMapper.insert(student);
		
		assertTrue(result == 1);
		
		StudentProfile profile = new StudentProfile();
		profile.setFatherName("张爱民");
		profile.setFatherAge(43);
		profile.setMotherName("何兰芳");
		profile.setMotherAge(33);
		profile.setStudentId(student.getId());
		
		result = studentProfileMapper.insert(profile);
		
		assertTrue(result == 1);
				
		result = studentProfileMapper.deleteByLambdaCriteria(p -> p.apply(profile).eq(StudentProfile::getFatherName));
		
		assertTrue(result == 1);
	}
	
	@Test
	public void testFindByStudentNameLike() {
		List<StudentProfile> result = studentProfileMapper.findByStudentNameLike("蓝%");
		
		assertTrue(result.size() > 0);
	}
}
