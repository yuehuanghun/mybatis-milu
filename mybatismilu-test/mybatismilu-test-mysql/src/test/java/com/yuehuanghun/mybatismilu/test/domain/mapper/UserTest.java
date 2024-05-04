package com.yuehuanghun.mybatismilu.test.domain.mapper;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.yuehuanghun.AppTest;
import com.yuehuanghun.mybatismilu.test.domain.entity.User;

@SpringBootTest(classes = AppTest.class)
@RunWith(SpringRunner.class)
public class UserTest {
	@Autowired
	private UserMapper userMapper;

	@Test
	@Transactional
	public void testBatchMerge() {
		User user = new User();
		user.setUserName("zhangsan");
		user.setAge(24);
		user.setName("张三");
		
		int effect = userMapper.batchMerge(Arrays.asList(user), "uidx_user_name");
		
		assertTrue(effect > 0);
		System.out.println(JSON.toJSON(user));
	}
	
	
	@Test
	@Transactional
	public void testBatchMerge2() {
		User user = new User();
		user.setUserName("zhangsan");
		user.setAge(25);
		user.setName("张三");
		
		User user2 = new User();
		user2.setUserName("zhaoliu");
		user2.setAge(65);
		user2.setName("赵六");
		
		User user3 = new User();
		user3.setUserName("qianqi");
		user3.setAge(46);
		user3.setName("钱七");
		
		List<User> list = Arrays.asList(user, user2, user3);
		int effect = userMapper.batchMerge(list, "uidx_user_name");
		
		System.out.println(effect);
		assertTrue(effect > 0);
		System.out.println(JSON.toJSON(list));
	}
}
