package com.yuehuanghun.mybatismilu.test.config;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.alibaba.fastjson.JSON;

public class JsonTypeHandler extends BaseTypeHandler<Object> {
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setString(i, JSON.toJSONString(parameter));
	}

	@Override
	public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
		String val = rs.getString(columnName);
		return parseJson(val);
	}
	
	private Object parseJson(String val) {
		return JSON.parseObject(val, getRawType());
	}

	@Override
	public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		String val = rs.getString(columnIndex);
		return parseJson(val);
	}

	@Override
	public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		throw new RuntimeException("不支持");
	}



}
