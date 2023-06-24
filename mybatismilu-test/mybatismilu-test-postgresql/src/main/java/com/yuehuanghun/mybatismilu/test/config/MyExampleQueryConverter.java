package com.yuehuanghun.mybatismilu.test.config;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

import com.yuehuanghun.mybatis.milu.metamodel.KeyType;
import com.yuehuanghun.mybatis.milu.tool.converter.DefaultExampleQueryConverter;

public class MyExampleQueryConverter extends DefaultExampleQueryConverter {

	@Override
	public Object convert(Object target, Class<?> attrJavaType, String keyName, KeyType keyType) {
		Object result =  super.convert(target, attrJavaType, keyName, keyType);
		
		if(KeyType.END.equals(keyType)) {
			if(result instanceof java.util.Date) {
				Calendar c = Calendar.getInstance();
				c.setTime((Date) result);
				c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 23, 59, 59);
				return c.getTime();
			} else if(result instanceof LocalDateTime) {
				LocalDateTime dt = ((LocalDateTime) result);
				return LocalDateTime.of(dt.toLocalDate(), LocalTime.of(23, 59, 59, 999999999));
			}
		}
		
		return result;
	}

	
}
