/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yuehuanghun.mybatis.milu.tool.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.metamodel.KeyType;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

/**
 * 默认的转换器<br>
 * 目前仅会转换以下类型：<br>
 * java.util.Date <br>
 * java.sql.Date <br>
 * java.sql.Time <br>
 * java.time.LocalDate <br>
 * java.time.LocalDateTime <br>
 * java.time.LocalTime <br>
 * 
 * @author yuehuanghun
 *
 */
public class DefaultExampleQueryConverter implements ExampleQueryConverter {
	private static final Log log = LogFactory.getLog(DefaultExampleQueryConverter.class);
	
	protected static final Map<Class<?>, Converter<?>> CONVERTER_MAP = new HashMap<>();
	static {
		CONVERTER_MAP.put(java.util.Date.class, new DateConverter());
		CONVERTER_MAP.put(java.sql.Date.class, new SqlDateConverter());
		CONVERTER_MAP.put(java.sql.Time.class, new SqlTimeConverter());
		CONVERTER_MAP.put(LocalDate.class, new LocalDateConverter());
		CONVERTER_MAP.put(LocalDateTime.class, new LocalDateTimeConverter());
		CONVERTER_MAP.put(LocalTime.class, new LocalTimeConverter());
	}

	@Override
	public Object convert(Object target, Class<?> attrJavaType, String keyName, KeyType keyType) {
		if(target == null) {
			return null;
		}
		
		if(attrJavaType.isInstance(target)) {
			return target;
		}
		
		Converter<?> converter = CONVERTER_MAP.get(attrJavaType);
		if(converter != null) {
			Object result = converter.convert(target);
			if(result != null) {
				return result;
			} else {
				if(!isEmpty(target)) { //减少警告
					log.warn(String.format("键%s的对象%s无法转为%s类型对象", keyName, target, attrJavaType.getName()));
				}				
			}
		}
		
		return target;
	}

	interface Converter<T> {
		T convert(Object target);
	}
	
	static class DateConverter implements Converter<java.util.Date> {
		@Override
		public java.util.Date convert(Object target) {
			if(CharSequence.class.isInstance(target)) {
				if(isEmpty(target)) {
					return null;
				}
				return DateUtil.parseDate(target.toString());
			} else if(Number.class.isInstance(target)) {
				return new java.util.Date(((Number)target).longValue());
			}
			return null;
		}		
	}
	
	static class SqlDateConverter implements Converter<java.sql.Date> {
		@Override
		public java.sql.Date convert(Object target) {
			if(CharSequence.class.isInstance(target)) {
				if(isEmpty(target)) {
					return null;
				}
				return DateUtil.parseSqlDate(target.toString());
			} else if(Number.class.isInstance(target)) {
				return new java.sql.Date(((Number)target).longValue());
			}
			return null;
		}		
	}
	
	static class SqlTimeConverter implements Converter<java.sql.Time> {
		@Override
		public java.sql.Time convert(Object target) {
			if(CharSequence.class.isInstance(target)) {
				if(isEmpty(target)) {
					return null;
				}
				return DateUtil.parseSqlTime(target.toString());
			} else if(Number.class.isInstance(target)) {
				return new java.sql.Time(((Number)target).longValue());
			}
			return null;
		}		
	}
	
	static class LocalDateConverter implements Converter<LocalDate> {
		@Override
		public LocalDate convert(Object target) {
			if(CharSequence.class.isInstance(target)) {
				if(isEmpty(target)) {
					return null;
				}
				return DateUtil.parseLocalDate(target.toString());
			} else if(Number.class.isInstance(target)) {
				return new java.util.Date(((Number)target).longValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			}
			return null;
		}		
	}
	
	static class LocalDateTimeConverter implements Converter<LocalDateTime> {
		@Override
		public LocalDateTime convert(Object target) {
			if(CharSequence.class.isInstance(target)) {
				if(isEmpty(target)) {
					return null;
				}
				return DateUtil.parseLocalDateTime(target.toString());
			} else if(Number.class.isInstance(target)) {
				return new java.util.Date(((Number)target).longValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			}
			return null;
		}		
	}

	static class LocalTimeConverter implements Converter<LocalTime> {
		@Override
		public LocalTime convert(Object target) {
			if(CharSequence.class.isInstance(target)) {
				if(isEmpty(target)) {
					return null;
				}
				return DateUtil.parseLocalTime(target.toString());
			} else if(Number.class.isInstance(target)) {
				return new java.util.Date(((Number)target).longValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
			}
			return null;
		}		
	}
	
	private static boolean isEmpty(Object target) {
		return !StringUtils.notBlank(target);
	}
	
	static class DateUtil {
		static final Pattern P1_REGEX = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}$");
		static final String P1 = "yyyy-MM-dd HH:mm:ss";
		static final Pattern P2_REGEX = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
		static final String P2 = "yyyy-MM-dd";
		static final Pattern P3_REGEX = Pattern.compile("\\d{4}/\\d{2}/\\d{2}\\s\\d{2}:\\d{2}:\\d{2}$");
		static final String P3 = "yyyy/MM/dd HH:mm:ss";
		static final Pattern P4_REGEX = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
		static final String P4 = "yyyy/MM/dd";
		static final Pattern P5_REGEX = Pattern.compile("^\\d{8}$");
		static final String P5 = "yyyyMMdd";
		static final Pattern P6_REGEX = Pattern.compile("^\\d{14}$");
		static final String P6 = "yyyyMMddHHmmss";
		static final Pattern P7_REGEX = Pattern.compile("^\\d{17}$");
		static final String P7 = "yyyyMMddHHmmssSSS";
		static final Pattern P8_REGEX = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}\\.\\d{3}$");
		static final String P8 = "yyyy-MM-dd HH:mm:ss.SSS";
		static final Pattern P9_REGEX = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2},\\d{3}$");
		static final String P9 = "yyyy-MM-dd HH:mm:ss,SSS";
		static final Pattern P10_REGEX = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}$");
		static final String P10 = "yyyy-MM-dd HH:mm";
		
		//时间
		static final Pattern _P1_REGEX = Pattern.compile("^\\d{2}:\\d{2}:\\d{2}$");
		static final String _P1 = "HH:mm:ss";
		static final Pattern _P2_REGEX = Pattern.compile("^\\d{6}$");
		static final String _P2 = "HHmmss";
		
		static final Map<Pattern, String> PATTERN_FORMAT_MAP = new LinkedHashMap<>();
		static final ThreadLocal<Map<String, DateFormat>> dateFormatRepo = new ThreadLocal<>();
		static final Map<String, DateTimeFormatter> DATETIME_FORMATTER_MAP = new ConcurrentHashMap<>();
		
		static {
			PATTERN_FORMAT_MAP.put(P1_REGEX, P1);
			PATTERN_FORMAT_MAP.put(P2_REGEX, P2);
			PATTERN_FORMAT_MAP.put(P3_REGEX, P3);
			PATTERN_FORMAT_MAP.put(P4_REGEX, P4);
			PATTERN_FORMAT_MAP.put(P5_REGEX, P5);
			PATTERN_FORMAT_MAP.put(P6_REGEX, P6);
			PATTERN_FORMAT_MAP.put(P7_REGEX, P7);
			PATTERN_FORMAT_MAP.put(P8_REGEX, P8);
			PATTERN_FORMAT_MAP.put(P9_REGEX, P9);
			PATTERN_FORMAT_MAP.put(P10_REGEX, P10);
			PATTERN_FORMAT_MAP.put(_P1_REGEX, _P1);
			PATTERN_FORMAT_MAP.put(_P2_REGEX, _P2);
		}
		
		public static java.util.Date parseDate(String dateStr){
			Set<Entry<Pattern, String>> entrySet = PATTERN_FORMAT_MAP.entrySet();
			for(Entry<Pattern, String> entry : entrySet) {
				if(entry.getKey().matcher(dateStr).find()) {
					try {
						Map<String, DateFormat> dateFormatMap = dateFormatRepo.get();
						if(dateFormatMap == null) {
							dateFormatMap = new HashMap<>();
							dateFormatRepo.set(dateFormatMap);
						}
						DateFormat df = dateFormatMap.computeIfAbsent(entry.getValue(), key -> {
							return new SimpleDateFormat(key);
						});
						return df.parse(dateStr);
					} catch (ParseException e) {
						throw new SqlExpressionBuildingException(e);
					}
				}
			}
			throw new SqlExpressionBuildingException(String.format("无法转换“%s”为日期", dateStr));
		}		
		
		public static java.sql.Date parseSqlDate(String dateStr){
			java.util.Date date = parseDate(dateStr);
			return new java.sql.Date(date.getTime());
		}
		
		public static java.sql.Time parseSqlTime(String dateStr){
			java.util.Date date = parseDate(dateStr);
			return new java.sql.Time(date.getTime());
		}
		
		public static LocalDate parseLocalDate(String dateStr) {
			Set<Entry<Pattern, String>> entrySet = PATTERN_FORMAT_MAP.entrySet();
			for(Entry<Pattern, String> entry : entrySet) {
				if(entry.getKey().matcher(dateStr).find()) {
					DateTimeFormatter formatter = DATETIME_FORMATTER_MAP.computeIfAbsent(entry.getValue(), key -> {
						return DateTimeFormatter.ofPattern(key);
					});
					return LocalDate.from(formatter.parse(dateStr));
				}
			}

			throw new SqlExpressionBuildingException(String.format("无法转换“%s”为LocalDate", dateStr));
		}
		
		public static LocalDateTime parseLocalDateTime(String dateStr) {
			Set<Entry<Pattern, String>> entrySet = PATTERN_FORMAT_MAP.entrySet();
			for(Entry<Pattern, String> entry : entrySet) {
				if(entry.getKey().matcher(dateStr).find()) {
					DateTimeFormatter formatter = DATETIME_FORMATTER_MAP.computeIfAbsent(entry.getValue(), key -> {
						return DateTimeFormatter.ofPattern(key);
					});
					return from(formatter.parse(dateStr));
				}
			}

			throw new SqlExpressionBuildingException(String.format("无法转换“%s”为LocalDateTime", dateStr));
		}
		
		public static LocalTime parseLocalTime(String dateStr) {
			Set<Entry<Pattern, String>> entrySet = PATTERN_FORMAT_MAP.entrySet();
			for(Entry<Pattern, String> entry : entrySet) {
				if(entry.getKey().matcher(dateStr).find()) {
					DateTimeFormatter formatter = DATETIME_FORMATTER_MAP.computeIfAbsent(entry.getValue(), key -> {
						return DateTimeFormatter.ofPattern(key);
					});
					return LocalTime.from(formatter.parse(dateStr));
				}
			}

			throw new SqlExpressionBuildingException(String.format("无法转换“%s”为LocalTime", dateStr));
		}
		
		private static LocalDateTime from(TemporalAccessor temporal) {
			if (temporal instanceof LocalDateTime) {
	            return (LocalDateTime) temporal;
	        } else if (temporal instanceof ZonedDateTime) {
	            return ((ZonedDateTime) temporal).toLocalDateTime();
	        } else if (temporal instanceof OffsetDateTime) {
	            return ((OffsetDateTime) temporal).toLocalDateTime();
	        }
			LocalDate date = temporal.query(TemporalQueries.localDate());
	        if (date == null) {
	        	date = LocalDate.of(1970, 1, 1);
	        }
	        LocalTime time = temporal.query(TemporalQueries.localTime());
	        if(time == null) {
	        	time = LocalTime.of(0, 0, 0, 0);
	        }
	        return LocalDateTime.of(date, time);
		}
	}
}
