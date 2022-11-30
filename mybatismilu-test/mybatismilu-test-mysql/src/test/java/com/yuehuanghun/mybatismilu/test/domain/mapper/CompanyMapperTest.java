package com.yuehuanghun.mybatismilu.test.domain.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import com.yuehuanghun.mybatismilu.test.domain.entity.Company;

@SpringBootTest(classes = AppTest.class)
@RunWith(SpringRunner.class)
public class CompanyMapperTest {

	@Autowired
	private CompanyMapper companyMapper;
	
	@Test
	public void testFindById() {
		Optional<Company> companyOpt = companyMapper.findById(1L);
		
		assertTrue(companyOpt.isPresent());
	}
	
	@Test
	public void testFindByCriteria() {
		List<Company> companyList = companyMapper.findByCriteria(p -> p.eq("companyName", "千百度"));
		
		assertEquals(companyList.size(), 1);
		
		companyList = companyMapper.findByCriteria(p -> p.select("*", "employees*").eq("companyName", "千百度"));
		
		assertEquals(companyList.size(), 1);
		
		assertTrue(companyList.get(0).getEmployees() != null);
	}
	
	@Test
	public void testCountByCriteria() {
		int count = companyMapper.countByCriteria(p -> p.eq("companyName", "千百度"));
		
		assertEquals(count, 1);
	}
	
	@Test
	@Transactional
	public void testDeleteById() {
		int effect = companyMapper.deleteById(1L);
		
		assertEquals(effect, 1);
	}
	
	@Test
	@Transactional
	public void testInsert() {
		Company company = new Company();
		company.setCompanyName("阑珊处");
		int effect = companyMapper.insert(company);
		
		assertEquals(effect, 1);
	}
	
	@Test
	public void testFindByExample() {
		Company example = new Company();
		example.setId(1L);
		List<Company> companys = companyMapper.findByExample(example);
		
		assertEquals(companys.size(), 1);
	}
	
	@Test
	public void testFindAll() {
		assertEquals(companyMapper.findAll().size(), 1);
		assertEquals(companyMapper.findAllAndSort(Sort.by("id")).size(), 1);
	}
	
	@Test
	@Transactional
	public void testUpdateById() {
		Optional<Company> companyOpt = companyMapper.findById(1L);
		
		assertTrue(companyOpt.isPresent());
		
		Company company = companyOpt.get();

		company.setCompanyName("阑珊处");
		
		companyMapper.updateById(company);
	}
	
	@Test
	public void testFindByCompanyName() {
		List<Company> list = companyMapper.findByCompanyName("千百度");
		
		assertEquals(list.size(), 1);
	}
	
	@Test
	public void testFindByEmployeeName() {
		Company company = companyMapper.findByEmployeesName("张三");
		
		assertNotNull(company);
	}
}
