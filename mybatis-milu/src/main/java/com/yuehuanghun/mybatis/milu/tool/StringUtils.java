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
package com.yuehuanghun.mybatis.milu.tool;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringUtils {
	private final static Pattern UNDERLINE_PATTERN = Pattern.compile("([A-Za-z\\d]+)(_)?");

	private final static Pattern CAMEL_PATTERN = Pattern.compile("[A-Z]([a-z\\d]+)?");
	
	public final static String EMPTY = "";

	public static boolean isBlank(final CharSequence cs) {
		int strLen;
		if (cs == null || (strLen = cs.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (Character.isWhitespace(cs.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotBlank(final CharSequence cs) {
		return !isBlank(cs);
	}
	
	public static String defaultBlank(String str) {
		if(isBlank(str)) {
			return EMPTY;
		}
		return str;
	}
	
	//在sql模板中使用
	public static boolean notBlank(final Object obj) {
		if(obj == null) {
			return false;
		}
		if(CharSequence.class.isInstance(obj)) {
			return isNotBlank((CharSequence)obj);
		}
		return true;
	}
	
	public static Object toCollectioin(final Object obj) {
		if(obj == null || Collection.class.isInstance(obj) || obj.getClass().isArray()) {
			return obj;
		}
		if(String.class.isInstance(obj)) {
			if(((String)obj).contains(Segment.COMMA)) {
				return Arrays.asList(((String)obj).split(Segment.COMMA)).stream().filter(StringUtils::isNotBlank).map(String::trim).collect(Collectors.toList());
			}
		}
		return new String[] {obj.toString()};
	}

	/**
	 * 下划线转驼峰法
	 * 
	 * @param line       源字符串
	 * @param smallCamel 大小驼峰,是否为小驼峰
	 * @return 转换后的字符串
	 */
	public static String underline2Camel(String line, boolean smallCamel) {
		if (line == null || "".equals(line)) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		Matcher matcher = UNDERLINE_PATTERN.matcher(line);
		while (matcher.find()) {
			String word = matcher.group();
			sb.append(smallCamel && matcher.start() == 0 ? Character.toLowerCase(word.charAt(0))
					: Character.toUpperCase(word.charAt(0)));
			int index = word.lastIndexOf('_');
			if (index > 0) {
				sb.append(word.substring(1, index).toLowerCase());
			} else {
				sb.append(word.substring(1).toLowerCase());
			}
		}
		return sb.toString();
	}

	/**
	 * 驼峰法转下划线
	 * 
	 * @param line      源字符串
	 * @param lowerCase 转换字符是否转为小写，true:小写，false：大写
	 * @return 转换后的字符串
	 */
	public static String camel2Underline(String line, boolean lowerCase) {
		if (line == null || EMPTY.equals(line)) {
			return EMPTY;
		}
		line = String.valueOf(line.charAt(0)).toUpperCase().concat(line.substring(1));
		StringBuilder sb = new StringBuilder();
		Matcher matcher = CAMEL_PATTERN.matcher(line);
		while (matcher.find()) {
			String word = matcher.group();
			sb.append(lowerCase ? word.toLowerCase() : word.toUpperCase());
			sb.append(matcher.end() == line.length() ? EMPTY : "_");
		}
		return sb.toString();
	}

	public static String uncapitalize(String string) {
		if (isBlank(string)) {
			return string;
		}
		return string.substring(0, 1).toLowerCase() + string.substring(1, string.length());
	}
	
	public static String capitalize(String string) {
		if (isBlank(string)) {
			return string;
		}
		return string.substring(0, 1).toUpperCase() + string.substring(1, string.length());
	}

	public static String collectionToCommaDelimitedString(Collection<?> coll) {
		return collectionToDelimitedString(coll, ",");
	}

	public static String collectionToDelimitedString(Collection<?> coll, String delim) {
		return collectionToDelimitedString(coll, delim, EMPTY, EMPTY);
	}

	public static String collectionToDelimitedString(Collection<?> coll, String delim, String prefix, String suffix) {

		if (coll == null || coll.isEmpty()) {
			return EMPTY;
		}

		StringBuilder sb = new StringBuilder();
		Iterator<?> it = coll.iterator();
		while (it.hasNext()) {
			sb.append(prefix).append(it.next()).append(suffix);
			if (it.hasNext()) {
				sb.append(delim);
			}
		}
		return sb.toString();
	}
	
	public static String concat(String[] strs) {
		if(strs == null) {
			return null;
		}
		if(strs.length == 1) {
			return strs[0];
		}
		StringBuilder sb = new StringBuilder();
		for(String str : strs) {
			sb.append(str);
		}
		return sb.toString();
	}
}
