package com.yuehuanghun.mybatismilu.test.domain.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.yuehuanghun.AppTest;
import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.criteria.Conditions;
import com.yuehuanghun.mybatis.milu.criteria.Exists;
import com.yuehuanghun.mybatis.milu.criteria.Predicates;
import com.yuehuanghun.mybatis.milu.criteria.QueryPredicate;
import com.yuehuanghun.mybatis.milu.criteria.QueryPredicateImpl;
import com.yuehuanghun.mybatis.milu.criteria.UpdatePredicate;
import com.yuehuanghun.mybatis.milu.data.Sort.Direction;
import com.yuehuanghun.mybatis.milu.pagehelper.PageRequest;
import com.yuehuanghun.mybatismilu.test.domain.entity.ClassTeacherRel;
import com.yuehuanghun.mybatismilu.test.domain.entity.Classs;
import com.yuehuanghun.mybatismilu.test.domain.entity.Student;
import com.yuehuanghun.mybatismilu.test.dto.ClassDTO;

@SpringBootTest(classes = AppTest.class)
@RunWith(SpringRunner.class)
public class ClassMapperTest {
	@Autowired
	private ClassMapper classMapper;

	@Test
	public void testFindByTeacherListName() {
		List<Classs> result = classMapper.findByTeacherListName("黄老师");
		assertTrue(result.size() > 0);
		
		System.out.println(JSON.toJSONString(result));
	}
	@Test
	public void testFindByStudentListName() {
		List<Classs> result = classMapper.findByStudentListName("张三");
		assertTrue(result.size() == 1);
	}
	@Test
	public void testFindByStudentListNameIn() {
		List<Classs> result = classMapper.findByStudentListNameInAndStudentListAgeGreaterThan(new String[] {"张三", "李四"}, 5);
		assertTrue(result.size() > 0);
	}
	
	@Test
	public void testRangQuery() {
		Date now = new Date();
		classMapper.findByAddTimeAfterAndAddTimeBefore(now, now);
		classMapper.findByAddTimeBetween(now, now);
	}
	
	@Test
	public void testFindByCriteria() {
		List<Classs> result = classMapper.findByCriteria(new QueryPredicateImpl().eq("name", "一年级").eq("studentListName", "张三").order(Direction.DESC,"id").order("studentListAddTime"));
		assertTrue(result.size() == 1);
		
		result = classMapper.findByCriteria(new QueryPredicateImpl().eq("name", "一年级").order(Direction.DESC,"id").order("studentListAddTime"));
		assertTrue(result.size() == 3);
		
		result = classMapper.findByCriteria(new QueryPredicateImpl().eq("name", "一年级").order(Direction.DESC,"id").order("studentListAddTime").limit(2));
		assertTrue(result.size() == 2);
		
		result = classMapper.findByCriteria(new QueryPredicateImpl().eq("name", "一年级").order(Direction.DESC,"id").order("studentListAddTime").limit(new PageRequest(2)));
		assertTrue(result.size() == 2);
		
		result = classMapper.findByCriteria(new QueryPredicateImpl().eq("name", "一年级").order(Direction.DESC,"id").order("studentListAddTime").limit(1, 2, false));
		assertTrue(result.size() == 2);
		
		//只查某些字段
		result = classMapper.findByCriteria(new QueryPredicateImpl().select("id", "name", "addTime").eq("name", "一年级").order(Direction.DESC,"id").order("studentListAddTime").limit(1, 10, false));
		assertTrue(result.size() == 3);
		
		//不查某些字段
		result = classMapper.findByCriteria(new QueryPredicateImpl().exselects("addTime").eq("name", "一年级").order(Direction.DESC,"id").order("studentListAddTime").limit(1, 10, false));
		assertTrue(result.size() == 3);
		
		result = classMapper.findByCriteria(new QueryPredicateImpl().regex("name", "^一.*$" ));
		assertTrue(result.size() == 1);
	}
	
	@Test
	public void testFindUniqueByCriteria() {
		Classs classs = classMapper.findUniqueByCriteria(p -> p.eq("id", 1L));
		assertNotNull(classs);
		
		classs = classMapper.findUniqueByCriteria(p -> p.orderAsc("id").limit(1));
		
		assertNotNull(classs);
		assertEquals(classs.getId().longValue(), 1L);
		
		QueryPredicate p = new QueryPredicateImpl();
		p.eq("id", 1L);
		
		classs = classMapper.findUniqueByCriteria(p);
		assertNotNull(classs);
	}
	
	@Test
	public void testFindByLambdaCriteria_mix() {
		List<Classs> result = classMapper.findByLambdaCriteria(p -> p.eq(Classs::getName, "一年级").andP(sp -> sp.eq("studentListName", "张三")));
		assertTrue(result.size() == 1);
		
		result = classMapper.findByLambdaCriteria(p -> p.eq(Classs::getName, "一年级").andP(sp -> sp.eq("studentListName", null)));
		assertTrue(result.size() == 1);
	}

	@Test
	@Transactional
	public void testUpdateByCriteriaAndDeleteByCriteria() {
		Date now = new Date();
		Classs clazz = new Classs();
		clazz.setAddTime(now);
		clazz.setName("三年级");
		
		int insertResult = classMapper.insert(clazz);
		assertEquals(insertResult, 1);
		
		clazz.setName("四年级");
		
		int updateResult = classMapper.updateByCriteria(clazz, predicate -> predicate.eq("name", "三年级"));
		assertEquals(updateResult, 1);
		
		int deleteResult = classMapper.deleteByCriteria(predicate -> predicate.eq("name", "四年级"));
		assertEquals(deleteResult, 1);
		
	}
	
	@Test
	@Transactional
	public void testUpdateByCriteriaAndDeleteByCriteriaExists() {
		Classs clazz = new Classs();
		clazz.setName("四年级");
		
		int updateResult = classMapper.updateByCriteria(clazz, p -> p.exists(Exists.of(StudentMapper.class).join("classId", "id").criteria(ep -> ep.eq("name", "张三"))));
		assertEquals(updateResult, 1);
		
		updateResult = classMapper.updateByLambdaCriteria(clazz, p -> p.exists(Exists.of(StudentMapper.class).join(Student::getClassId, Classs::getId).lambdaCriteria(ep -> ep.eq(Student::getName, "张三"))));
		assertEquals(updateResult, 1);
		
		int deleteResult = classMapper.deleteByCriteria(p -> p.exists(Exists.of(StudentMapper.class).join("classId", "id").criteria(ep -> ep.eq("name", "张三"))));
		assertEquals(deleteResult, 1);
		
		deleteResult = classMapper.deleteByLambdaCriteria(p -> p.exists(Exists.of(StudentMapper.class).join(Student::getClassId, Classs::getId).lambdaCriteria(ep -> ep.eq(Student::getName, "张三"))));
		assertEquals(updateResult, 1);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testFindByLambdaCriteria() {
		Classs params = new Classs();
		params.setName("一年级");
		List<Classs> result = classMapper.findByLambdaCriteria(predicate -> predicate.apply(params).eq(Classs::getName).order(Direction.ASC, Classs::getAddTime, Classs::getName));
		
		assertTrue(result.size() == 1);
		
		result = classMapper.findByLambdaCriteria(predicate -> predicate.apply(params).eq(Classs::getName).limit(10, false));
		
		assertTrue(result.size() == 1);
		
		result = classMapper.findByLambdaCriteria(predicate -> predicate.apply(params).select(Classs::getName, Classs::getAddTime).eq(Classs::getName).limit(10, false));
		
		assertTrue(result.size() == 1);
		
		result = classMapper.findByLambdaCriteria(predicate -> predicate.apply(params).and(p -> p.eq(Classs::getName).lt(Classs::getAddTime, new Date())).or(p -> p.eq(Classs::getName, "二年级").gt(Classs::getAddTime, new Date())));
		
		assertTrue(result.size() == 1);
		
		result = classMapper.findByLambdaCriteria(predicate -> predicate.eq(Classs::getName, params.getName()).order(Direction.ASC, Classs::getAddTime, Classs::getName));
		
		assertTrue(result.size() == 1);
		
		result = classMapper.findByLambdaCriteria(predicate -> predicate.eq(Classs::getName, "").order(Direction.ASC, Classs::getAddTime, Classs::getName));
		
		assertTrue(result.size() == 2);
		
		result = classMapper.findByLambdaCriteria(predicate -> predicate.conditionMode(Mode.NOT_NULL).eq(Classs::getName, "").order(Direction.ASC, Classs::getAddTime, Classs::getName));
		
		assertTrue(result.size() == 0);
		
		result = classMapper.findByLambdaCriteria(predicate -> predicate.regex(Classs::getName, "^一.*$"));
		
		assertTrue(result.size() == 1);
	}

	@Test
	public void testFindUniqueByLambdaCriteria() {
		Classs classs = classMapper.findUniqueByLambdaCriteria(p -> p.eq(Classs::getId, 1L));
		assertNotNull(classs);
		
		classs = classMapper.findUniqueByLambdaCriteria(p -> p.orderAsc(Classs::getId).limit(1));
		
		assertNotNull(classs);
		assertEquals(classs.getId().longValue(), 1L);
	}

	@Test
	public void testFindByLambdaCriteria_subCriteria() {
		Classs params = new Classs();
		params.setName("一年级");
		List<Classs> result = classMapper.findByLambdaCriteria(predicate -> predicate.apply(params).eq(Classs::getName).and(p -> p.lte(Classs::getAddTime, null).or(op -> op.gte(Classs::getAddTime, null))));
		assertTrue(result.size() == 1);
	}
	
	@Test
	public void testFindByCriteria_resultType() {
		List<ClassDTO> result = classMapper.findByCriteria(p -> p.exselect("addTime").eq("name", "一年级"), ClassDTO.class);
		assertTrue(result.size() == 1);
		assertEquals(result.get(0).getClass(), ClassDTO.class);
		assertEquals(result.get(0).getName(), "一年级");
	}
	

	@SuppressWarnings("unchecked")
	@Test
	public void testFindByLambdaCriteria_resultType() {
		List<ClassDTO> result = classMapper.findByLambdaCriteria(p -> p.exselect(Classs::getAddTime).eq(Classs::getName, "一年级"), ClassDTO.class);
		assertTrue(result.size() == 1);
		assertEquals(result.get(0).getClass(), ClassDTO.class);
		assertEquals(result.get(0).getName(), "一年级");
		
		List<Long> result2 = classMapper.findByLambdaCriteria(p -> p.select(Classs::getId).eq(Classs::getName, "一年级"), Long.class);
		assertEquals(result2.size(), 1);
		assertNotNull(result2.get(0));
		assertTrue(result2.get(0) == 1);
		
		result2 = classMapper.findByLambdaCriteria(p -> p.select(Classs::getId), Long.class);
		assertEquals(result2.size(), 2);
		assertNotNull(result2.get(0));
		assertNotNull(result2.get(1));
	}
	
	@Test
	public void testFindById() {
		Optional<Classs> clazzOpt = classMapper.findById(1L);
		assertTrue(clazzOpt.isPresent());
		
		assertNotNull(clazzOpt.get().getData());
		assertEquals(clazzOpt.get().getData().size(), 4);
		System.out.println(clazzOpt.get());
		
		clazzOpt = classMapper.findById(2L);
		assertTrue(clazzOpt.isPresent());
		assertNull(clazzOpt.get().getData());
	}
	
	@Test
	@Transactional
	public void testInsert() {
		Classs clazz = new Classs();
		clazz.setName("六年级");
		clazz.setData(Arrays.asList(3L, 4L, 5L, 6L));
		
		classMapper.insert(clazz);
		
		Optional<Classs> clazzOpt = classMapper.findById(clazz.getId());
		assertTrue(clazzOpt.isPresent());
		
		assertNotNull(clazzOpt.get().getData());
		assertEquals(clazzOpt.get().getData().size(), 4);
		assertNotNull(clazzOpt.get().getAddTime());
	}
	
	@Test
	@Transactional
	public void testBatchInsert() {
		Classs clazz = new Classs();
		clazz.setName("六年级");
		clazz.setData(Arrays.asList(3L, 4L, 5L, 6L));
		
		List<Classs> classList = new ArrayList<>();
		classList.add(clazz);
		
		clazz = new Classs();
		clazz.setName("五年级");
		clazz.setData(Arrays.asList(1L, 3L, 7L, 8L));
		
		classList.add(clazz);
		
		int effect = classMapper.batchInsert(classList);
		assertEquals(effect, 2);
		
		Optional<Classs> clazzOpt = classMapper.findById(clazz.getId());
		assertTrue(clazzOpt.isPresent());
		
		assertNotNull(clazzOpt.get().getData());
		assertEquals(clazzOpt.get().getData().size(), 4);
		assertNotNull(clazzOpt.get().getAddTime());
	}
	
	@Test
	@Transactional
	public void testUpdateById() {
		Classs clazz = new Classs();
		clazz.setName("六年级");
		clazz.setData(Arrays.asList(3L, 4L, 5L, 6L));
		clazz.setId(1L);
		
		int effect = classMapper.updateById(clazz);
		assertEquals(effect, 1);
	}
	
	@Test
	@Transactional
	public void testUpdateAttrById() {
		int effect = classMapper.updateAttrById("data", Arrays.asList(3L, 4L, 5L, 6L), 1L);
		assertEquals(effect, 1);
	}
	
	@Test
	@Transactional
	public void testUpdateByCriteria() {
		Classs clazz = new Classs();
		clazz.setName("六年级");
		clazz.setData(Arrays.asList(3L, 4L, 5L, 6L));
		
		UpdatePredicate predicate = Predicates.updatePredicate();
		predicate.eq("id", 1L);
		int hashCode1 = predicate.hashCode();
		int effect = classMapper.updateByCriteria(clazz, predicate);
		int hashCode2 = predicate.hashCode();
		assertEquals(effect, 1);
		assertEquals(hashCode1, hashCode2);
	}
	
	@Test
	@Transactional
	public void testUpdateAttrByCriteria() {
		int effect = classMapper.updateAttrByCriteria("data", Arrays.asList(3L, 4L, 5L, 6L), p -> p.eq("id", 1L));
		assertEquals(effect, 1);
		
		effect = classMapper.updateAttrByCriteria("data", null, p -> p.eq("id", 1L));
		assertEquals(effect, 1);
		
		Optional<Classs> classs = classMapper.findById(1L);
		assertNull(classs.get().getData());
		
		effect = classMapper.updateAttrByCriteria("isDeleted", "Y", p -> p.eq("id", 1L));
		assertEquals(effect, 1);
	}
	
	@Test
	@Transactional
	public void testUpdateAttrByLambdaCriteria() {
		int effect = classMapper.updateAttrByLambdaCriteria(Classs::getData, Arrays.asList(3L, 4L, 5L, 6L), p -> p.eq(Classs::getId, 1L));
		assertEquals(effect, 1);
		
		effect = classMapper.updateAttrByLambdaCriteria(Classs::getData, null, p -> p.eq(Classs::getId, 1L));
		assertEquals(effect, 1);
		
		Optional<Classs> classs = classMapper.findById(1L);
		assertNull(classs.get().getData());
	}
	
	@Test
	@Transactional
	public void testFindByCriteria_ref() {
		List<Classs> list = classMapper.findByCriteria(p -> p.select("*","studentList*").eq("id", 1L));
		System.out.println(JSON.toJSONString(list));
		assertEquals(1, list.size());
		assertNotNull(list.get(0).getStudentList());
	}
	
	@Test
	public void testFindByCriteria_distinct() {
		List<Classs> list = classMapper.findByCriteria(p -> p.like("studentListName", "王%"));
		assertEquals(2, list.size());
		
		list = classMapper.findByCriteria(p -> p.like("studentListName", "王%").distinct());
		assertEquals(1, list.size());
	}
	
	@Test
	@Transactional
	public void testLogicDelete() {
		int effect = classMapper.logicDeleteById(1L);
		
		assertEquals(effect, 1);
		
		Optional<Classs> clazz = classMapper.findById(1L);
		assertEquals(clazz.get().getIsDeleted(), "Y");
		
		List<Classs> classList = classMapper.findByCriteria(p -> p.deleted());
		assertEquals(classList.size(), 2);
		
		classList = classMapper.findByCriteria(p -> p.undeleted());
		assertEquals(classList.size(), 0);
		
		classList = classMapper.findByCriteria(p -> p.eq("name", "一年级").deleted());
		assertEquals(classList.size(), 1);
		
		classList = classMapper.findByLambdaCriteria(p -> p.deleted());
		assertEquals(classList.size(), 2);
		
		classList = classMapper.findByCriteria(p -> p.eq("name", "一年级").or(op -> op.eq("name", "二年级").undeleted()));
		assertEquals(classList.size(), 1);
		
		classList = classMapper.findByCriteria(p -> p.eq("name", "一年级").deleted().or(op -> op.eq("name", "二年级").undeleted()));
		assertEquals(classList.size(), 1);
		
		classList = classMapper.findByLambdaCriteria(p -> p.eq(Classs::getName, "一年级").orP(op -> op.eq("name", "二年级")));
		assertEquals(classList.size(), 2);
	}
	
	@Test
	@Transactional
	public void testResumeLogicDelete() {
		int effect = classMapper.resumeLogicDeletedById(1L);
		
		assertEquals(effect, 1);
		
		Optional<Classs> clazz = classMapper.findById(1L);
		assertEquals(clazz.get().getIsDeleted(), "N");
	}
	
	@Test
	@Transactional
	public void testInsertAutoSetDeleteField() {
		Classs clazz = new Classs();
		clazz.setName("六年级");
		
		classMapper.insert(clazz);
		
		assertEquals("N", clazz.getIsDeleted());
		
		clazz = new Classs();
		clazz.setName("六年级");
		
		Classs clazz2 = new Classs();
		clazz2.setName("五年级");
		
		classMapper.batchInsert(Arrays.asList(clazz, clazz2));
		assertEquals("N", clazz.getIsDeleted());
		assertEquals("N", clazz2.getIsDeleted());
	}
	
	@Test
	public void testFindByCriteriaExists() {
		QueryPredicate predicate1 = Predicates.queryPredicate();
		predicate1.exists(Exists.of(StudentMapper.class).join("classId", "id").criteria(ep -> {
			ep.select("id");
			ep.eq("name", "张三");
		}));
		List<Classs> list = classMapper.findByCriteria(predicate1);
		assertEquals(list.size(), 1);
		
		list = classMapper.findByLambdaCriteria(p -> {
			p.exists(Exists.of(StudentMapper.class).join(Student::getClassId, Classs::getId).lambdaCriteria(ep -> {
				ep.select(Student::getId);
				ep.eq(Student::getName, "张三");
			}));
		});
		assertEquals(list.size(), 1);
	}
	
	@Test
	public void testFindByCriteriaExists2() {
		List<Classs> list = classMapper.findByCriteria(p -> {
			p.exists(Exists.of(ClassTeacherRelMaper.class).join("classId", "id").criteria(ep -> {
				ep.select("id");
				ep.eq("teachersName", "黄老师");
			}));
			p.undeleted();
		});
		assertEquals(list.size(), 1);
		
		list = classMapper.findByCriteria(p -> {
			p.exists(Exists.of(ClassTeacherRelMaper.class).join(Student::getClassId, Classs::getId).lambdaCriteria(ep -> {
				ep.select(ClassTeacherRel::getId);
				ep.and(Conditions.equal("teachersName", "黄老师"));
			}));
			p.undeleted();
		});
		assertEquals(list.size(), 1);
	}
	
	@Test
	public void testFindByCriteriaNotExists() {
		List<Classs> list = classMapper.findByCriteria(p -> {
			p.notExists(Exists.of(StudentMapper.class).join("classId", "id").criteria(ep -> {
				ep.select("id");
				ep.eq("name", "张三");
			}));
		});
		assertEquals(list.size(), 1);
	}
	
	@Test
	public void testFindByCriteriaHashCodeEquals() {
		QueryPredicate predicate1 = Predicates.queryPredicate();
		predicate1.eq("name", "二年级");
		QueryPredicate predicate2 = Predicates.queryPredicate();
		predicate2.eq("name", "一年级");
		
		int hashCode1 = predicate1.hashCode();
		classMapper.findByCriteria(predicate1);
		int hashCode2 = predicate1.hashCode();
		assertEquals(hashCode1, hashCode2);
		
		hashCode1 = predicate2.hashCode();
		classMapper.findByCriteria(predicate2);
		hashCode2 = predicate2.hashCode();
		
		assertEquals(predicate1, predicate2);
	}
}
