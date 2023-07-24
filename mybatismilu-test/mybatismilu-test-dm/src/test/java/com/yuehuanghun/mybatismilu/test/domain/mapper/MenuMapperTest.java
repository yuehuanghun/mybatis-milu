package com.yuehuanghun.mybatismilu.test.domain.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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

import com.yuehuanghun.AppTest;
import com.yuehuanghun.mybatis.milu.criteria.Patch;
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
	
	@Test
	public void testExampleQuery() {
		Menu example = new Menu();
		example.setName("一级");
		
		List<Menu> list = menuMapper.findByExample(example);
		assertEquals(list.size(), 1);
	}
	
	@Test
	public void testExampleFetchRef() {
		Menu example = new Menu();
		example.setName("二级");
		List<Menu> list = menuMapper.findByExample(example, null, null, "parent");
		assertEquals(list.size(), 2);
		assertNotNull(list.get(0).getParent());
		assertNull(list.get(0).getChildrens());
		
		list = menuMapper.findByExample(example);
		assertEquals(list.size(), 2);
		assertNotNull(list.get(0).getParent());
//		assertEquals(list.stream().filter(item -> item.getId().longValue() == 5).findAny().get().getChildrens().size(), 1);
		
		example = new Menu();
		example.setName("一级");
		list = menuMapper.findByExample(example, null, null, "children");
		assertEquals(list.size(), 1);
		assertNull(list.get(0).getParent());
		assertNotNull(list.get(0).getChildrens());
		assertEquals(list.get(0).getChildrens().size(), 2);
	}
	
	@Test
	@Transactional
	public void testPatchUpdateByIds() {
		Patch patch = new Patch().add("name", "一级11");
		menuMapper.updatePatchByIds(patch, Arrays.asList(1L, 2L));
		
		Optional<Menu> menuOpt = menuMapper.findById(1L);
		assertTrue(menuOpt.isPresent());
		assertEquals("一级11", menuOpt.get().getName());
		
		menuOpt = menuMapper.findById(2L);
		assertTrue(menuOpt.isPresent());
		assertEquals("一级11", menuOpt.get().getName());
	}
	
	@Test
	@Transactional
	public void testPatchUpdateByCriteria() {
		Patch patch = new Patch().add("name", "一级11");
		menuMapper.updatePatchByCriteria(patch, p -> p.eq("id", 1L));
		
		Optional<Menu> menuOpt = menuMapper.findById(1L);
		assertTrue(menuOpt.isPresent());
		assertEquals("一级11", menuOpt.get().getName());		

		menuMapper.updatePatchByCriteria(patch, p -> p.startWith("name", "二级"));
		
		menuOpt = menuMapper.findById(3L);
		assertTrue(menuOpt.isPresent());
		assertEquals("一级11", menuOpt.get().getName());
	}
	
	@Test
	@Transactional
	public void testPatchUpdateByLambdaCriteria() {
		Patch patch = new Patch().add(Menu::getName, "一级11");
		menuMapper.updatePatchByLambdaCriteria(patch, p -> p.eq(Menu::getId, 1L));
		
		Optional<Menu> menuOpt = menuMapper.findById(1L);
		assertTrue(menuOpt.isPresent());
		assertEquals("一级11", menuOpt.get().getName());
	}
}
