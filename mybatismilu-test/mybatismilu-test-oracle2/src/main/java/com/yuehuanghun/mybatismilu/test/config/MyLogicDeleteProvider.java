package com.yuehuanghun.mybatismilu.test.config;

import com.yuehuanghun.mybatis.milu.tool.logicdel.LogicDeleteProvider;

public class MyLogicDeleteProvider implements LogicDeleteProvider {

	@Override
	public Object value(Context context) {
		return "YES";
	}

	@Override
	public Object resumeValue(Context context) {
		return "NO";
	}

}
