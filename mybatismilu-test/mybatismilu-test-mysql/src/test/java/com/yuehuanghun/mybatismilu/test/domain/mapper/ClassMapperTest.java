package com.yuehuanghun.mybatismilu.test.domain.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.yuehuanghun.AppTest;
import com.yuehuanghun.mybatis.milu.annotation.Mode;
import com.yuehuanghun.mybatis.milu.criteria.QueryPredicateImpl;
import com.yuehuanghun.mybatis.milu.data.Sort.Direction;
import com.yuehuanghun.mybatismilu.test.domain.entity.Classs;

@SpringBootTest(classes = AppTest.class)
@RunWith(SpringRunner.class)
public class ClassMapperTest {
	@Autowired
	private ClassMapper classMapper;

	@Test
	public void testFindByTeacherListName() {
		List<Classs> result = classMapper.findByTeacherListName("黄老师");
		assertTrue(result.size() > 0);
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
		
		result = classMapper.findByCriteria(new QueryPredicateImpl().eq("name", "一年级").order(Direction.DESC,"id").order("studentListAddTime").limit(10));
		assertTrue(result.size() == 3);
		
		result = classMapper.findByCriteria(new QueryPredicateImpl().eq("name", "一年级").order(Direction.DESC,"id").order("studentListAddTime").limit(1, 10, false));
		assertTrue(result.size() == 3);
		
		//只查某些字段
		result = classMapper.findByCriteria(new QueryPredicateImpl().select("id", "name", "addTime").eq("name", "一年级").order(Direction.DESC,"id").order("studentListAddTime").limit(1, 10, false));
		assertTrue(result.size() == 3);
		
		//不查某些字段
		result = classMapper.findByCriteria(new QueryPredicateImpl().exselects("addTime").eq("name", "一年级").order(Direction.DESC,"id").order("studentListAddTime").limit(1, 10, false));
		assertTrue(result.size() == 3);
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
	}
}
