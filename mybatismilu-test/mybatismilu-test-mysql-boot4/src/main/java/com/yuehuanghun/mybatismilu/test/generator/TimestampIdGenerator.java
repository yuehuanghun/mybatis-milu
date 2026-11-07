package com.yuehuanghun.mybatismilu.test.generator;

import java.io.Serializable;

import org.springframework.stereotype.Component;

import com.yuehuanghun.mybatis.milu.id.IdGenerateContext;
import com.yuehuanghun.mybatis.milu.id.IdentifierGenerator;

/**
 * 自定义ID生成器示范
 * @author yuehuanghun
 *
 */
@Component //声明为spring组件，自动装配
public class TimestampIdGenerator implements IdentifierGenerator {
	
	private long lastId = System.currentTimeMillis();

	@Override
	public Serializable generate(IdGenerateContext context) {
		return nextId();
	}
	
	private synchronized long nextId() {
		long nextId = System.currentTimeMillis();
		if(nextId <= lastId) {
			while(true) {
				nextId = System.currentTimeMillis();
				if(nextId > lastId) {
					lastId = nextId;
					return nextId;
				}
			}
		}
		lastId = nextId;
		return nextId;
	}

	@Override
	public String getName() {
		return "timestamp";
	}
}
