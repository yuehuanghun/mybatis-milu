package com.yuehuanghun.mybatis.milu;

/**
 * 点位符解析器
 * @author yuehuanghun
 *
 */
public interface PlaceholderResolver {
	String resolvePlaceholder(String placeholderName);
	
	public static final PlaceholderResolver DONOTHING = new PlaceholderResolver() {
		
		@Override
		public String resolvePlaceholder(String placeholderName) {
			return placeholderName;
		}
	};
}
