package com.yuehuanghun.mybatis.milu.tool.converter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.yuehuanghun.mybatis.milu.tool.converter.impl.BigDecimalConverter;
import com.yuehuanghun.mybatis.milu.tool.converter.impl.BooleanConverter;
import com.yuehuanghun.mybatis.milu.tool.converter.impl.DateConverter;
import com.yuehuanghun.mybatis.milu.tool.converter.impl.DoubleConverter;
import com.yuehuanghun.mybatis.milu.tool.converter.impl.FloatConverter;
import com.yuehuanghun.mybatis.milu.tool.converter.impl.IntegerConverter;
import com.yuehuanghun.mybatis.milu.tool.converter.impl.LocalDateConverter;
import com.yuehuanghun.mybatis.milu.tool.converter.impl.LocalDateTimeConverter;
import com.yuehuanghun.mybatis.milu.tool.converter.impl.LocalTimeConverter;
import com.yuehuanghun.mybatis.milu.tool.converter.impl.LongConverter;
import com.yuehuanghun.mybatis.milu.tool.converter.impl.ShortConverter;
import com.yuehuanghun.mybatis.milu.tool.converter.impl.SqlDateConverter;
import com.yuehuanghun.mybatis.milu.tool.converter.impl.SqlTimeConverter;
import com.yuehuanghun.mybatis.milu.tool.converter.impl.StringConverter;

/**
 * 值类型转换器
 * @author yuehuanghun
 *
 */
public class ConverterUtils {
	static final Map<Class<?>, Converter<?>> CONVERTER_MAP = new HashMap<>();
	static {
		register(java.util.Date.class, new DateConverter());
		register(java.sql.Date.class, new SqlDateConverter());
		register(java.sql.Time.class, new SqlTimeConverter());
		register(LocalDate.class, new LocalDateConverter());
		register(LocalDateTime.class, new LocalDateTimeConverter());
		register(LocalTime.class, new LocalTimeConverter());
		register(BigDecimal.class, new BigDecimalConverter());
		register(Boolean.class, new BooleanConverter());
		register(Double.class, new DoubleConverter());
		register(Float.class, new FloatConverter());
		register(Integer.class, new IntegerConverter());
		register(Long.class, new LongConverter());
		register(Short.class, new ShortConverter());
		register(String.class, new StringConverter());
	}
	
	public static <T> void register(Class<T> clazz, Converter<T> converter) {
		CONVERTER_MAP.put(clazz, converter);
	}
	
	public static Optional<Converter<?>> getConverter(Class<?> clazz){
		return Optional.ofNullable(CONVERTER_MAP.get(clazz));
	}
}
