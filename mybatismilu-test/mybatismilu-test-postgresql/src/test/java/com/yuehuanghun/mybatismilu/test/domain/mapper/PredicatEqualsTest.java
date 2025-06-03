package com.yuehuanghun.mybatismilu.test.domain.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.LockModeType;

import org.junit.Test;

import com.yuehuanghun.mybatis.milu.criteria.Expression;
import com.yuehuanghun.mybatis.milu.criteria.LambdaQueryPredicate;
import com.yuehuanghun.mybatis.milu.criteria.LambdaQueryPredicateImpl;
import com.yuehuanghun.mybatis.milu.criteria.QueryPredicate;
import com.yuehuanghun.mybatis.milu.criteria.QueryPredicateImpl;
import com.yuehuanghun.mybatis.milu.criteria.StatisticPredicate;
import com.yuehuanghun.mybatis.milu.criteria.StatisticPredicateImpl;
import com.yuehuanghun.mybatismilu.test.domain.entity.Student;

public class PredicatEqualsTest {

	@Test
	public void testQueryPredicateEquals() {
		QueryPredicate predicate1 = new QueryPredicateImpl().eq("name", "张三").lt("age", 10).orderAsc("id");
		QueryPredicate predicate2 = new QueryPredicateImpl().eq("name", "李四").lt("age", 13).orderAsc("id").limit(10);
		
		assertEquals(predicate1, predicate2);
		
		predicate1.select("id","name");
		predicate2.select("id","name");
		assertEquals(predicate1, predicate2);
		assertEquals(predicate1.hashCode(), predicate2.hashCode());
		
		predicate2.select("age");
		assertNotEquals(predicate1, predicate2);
		assertNotEquals(predicate1.hashCode(), predicate2.hashCode());
		
		QueryPredicate predicate3 = new QueryPredicateImpl().eq("name", "张三").lt("age", 10).orderAsc("id").lock();
		QueryPredicate predicate4 = new QueryPredicateImpl().eq("name", "李四").lt("age", 13).orderAsc("id").lock();
		assertEquals(predicate3, predicate4);
		assertEquals(predicate3.hashCode(), predicate4.hashCode());
		
		predicate4.lock(LockModeType.PESSIMISTIC_READ);
		assertNotEquals(predicate3, predicate4);
		assertNotEquals(predicate3.hashCode(), predicate4.hashCode());	
	}
	
	@Test
	public void testStatisticPredicateEquals() {
		StatisticPredicate predicate1 = new StatisticPredicateImpl().sum("age").avg("age").count("id").like("name", "李%").gt("age", 5).groupBy("classId").orderAsc("classsName");
		StatisticPredicate predicate2 = new StatisticPredicateImpl().sum("age").avg("age").count("id").like("name", "张%").gt("age", 7).groupBy("classId").orderAsc("classsName");
		assertEquals(predicate1, predicate2);
		assertEquals(predicate1.hashCode(), predicate2.hashCode());
		
		predicate2.max("age");
		assertNotEquals(predicate1, predicate2);
		assertNotEquals(predicate1.hashCode(), predicate2.hashCode());
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testLambdaQueryPredicateImpl() {
		LambdaQueryPredicate<Student> predicate1 = new LambdaQueryPredicateImpl<Student>().eq(Student::getName,"张三").lt(Student::getAge, 10).orderAsc(Student::getId).lock();
		LambdaQueryPredicate<Student> predicate2 = new LambdaQueryPredicateImpl<Student>().eq(Student::getName,"李四").lt(Student::getAge, 10).orderAsc(Student::getId).lock();
		assertEquals(predicate1, predicate2);
		assertEquals(predicate1.hashCode(), predicate2.hashCode());
		
		predicate2.exselect(Student::getAddTime);
		
		assertNotEquals(predicate1, predicate2);
		assertNotEquals(predicate1.hashCode(), predicate2.hashCode());
	}
	
	@Test
	public void testOnePredicateMultiHash() {
		Map<Expression, String> map = new HashMap<>();
		StatisticPredicate predicate1 = new StatisticPredicateImpl().sum("age").avg("age").count("id").like("name", "李%").gt("age", 5).groupBy("classId").orderAsc("classsName");
		map.put(predicate1, "1");
		predicate1.eq("age", 1);
		map.put(predicate1, "2");
		System.out.println(map);
		assertEquals(map.size(), 2);
	}
}
