package com.yuehuanghun.mybatismilu.test.domain.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import com.yuehuanghun.mybatismilu.test.domain.entity.Menu;

@SpringBootTest(classes = AppTest.class)
@RunWith(SpringRunner.class)
public class MenuMapperTest {

	@Autowired
	private MenuMapper menuMapper;
	
	@Test
	public void testFindByParentName() {
		List<Menu> list = menuMapper.findByParentName("一级1");
		assertTrue(list.size() == 2);
	}
	
	@Test
	public void testFindDistinctByChildrensName() {
		List<Menu> list = menuMapper.findDistinctByChildrensName("三级1");
		assertTrue(list.size() == 1);
	}
	
	@Test
	public void testFindDistinctByChildrensNameAndChildrensAddTimeLessThan() {
		List<Menu> list = menuMapper.findDistinctByChildrensNameAndChildrensAddTimeLessThan("三级1", new Date());
		assertTrue(list.size() == 1);
	}
	
	@Test
	@Transactional
	public void testLogicDelete() {
		int effect = menuMapper.logicDeleteById(1L);
		
		assertEquals(effect, 1);
		
		Optional<Menu> menu = menuMapper.findById(1L);
		assertTrue(menu.get().getIsDeleted() == 1);
		assertTrue(menu.get().getDeleteTime() != null);
	}
	
	@Test
	@Transactional
	public void testResumeLogicDelete() {
		int effect = menuMapper.resumeLogicDeletedById(5L);
		
		assertEquals(effect, 1);
		
		Optional<Menu> menu = menuMapper.findById(1L);
		assertTrue(menu.get().getIsDeleted() == 0);
		assertTrue(menu.get().getDeleteTime() == null);
	}
	
	@Test
	public void testJoin() {
		Menu menu = menuMapper.findUniqueByLambdaCriteria(p -> p.selects("*, parent*, childrens*").eq(Menu::getId, 5L));
		assertNotNull(menu);
		assertNotNull(menu.getParent());
		assertNotNull(menu.getChildrens());
		assertEquals(menu.getChildrens().size(), 1);
	}
}
