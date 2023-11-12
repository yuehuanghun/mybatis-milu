package com.yuehuanghun.mybatismilu.test.config;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class Boolean2IntTypeHandler extends BaseTypeHandler<Boolean> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType)
			throws SQLException {
		if(parameter == null) {
			ps.setNull(i, Types.INTEGER);
		} else if(parameter.booleanValue()) {
			ps.setInt(i, (short) 1);
		} else {
			ps.setInt(i, (short) 0);
		}
	}

	@Override
	public Boolean getNullableResult(ResultSet rs, String columnName) throws SQLException {
		if(rs.wasNull()) {
			return null;
		}
		int value = rs.getInt(columnName);
		return value == 0 ? false : true;
	}

	@Override
	public Boolean getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		if(rs.wasNull()) {
			return null;
		}
		int value = rs.getInt(columnIndex);
		return value == 0 ? false : true;
	}

	@Override
	public Boolean getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		if(cs.wasNull()) {
			return null;
		}
		int value = cs.getInt(columnIndex);
		return value == 0 ? false : true;
	}

}
