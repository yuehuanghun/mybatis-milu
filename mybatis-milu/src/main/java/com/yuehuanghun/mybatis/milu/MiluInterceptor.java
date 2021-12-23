package com.yuehuanghun.mybatis.milu;

import java.sql.Statement;

import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

import com.yuehuanghun.mybatis.milu.mapping.ResultMapHelper;

/**
 * 仅用于执行查询后，清除ThreadLocal数据
 * 
 * @author yuehuanghun
 *
 */
@Intercepts({ @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = { Statement.class }) })
public class MiluInterceptor implements Interceptor {

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		try {
			return invocation.proceed();
		} finally {
			ResultMapHelper.clear();
		}
	}

}
