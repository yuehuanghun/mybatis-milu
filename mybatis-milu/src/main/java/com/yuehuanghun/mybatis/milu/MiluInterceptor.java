package com.yuehuanghun.mybatis.milu;

import java.util.HashMap;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.yuehuanghun.mybatis.milu.mapping.ResultMapHelper;

/**
 * 仅用于执行查询后，清除ThreadLocal数据
 * 
 * @author yuehuanghun
 *
 */
@Intercepts(
        {
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        }
)
public class MiluInterceptor implements Interceptor {
    private String countSuffix = "_COUNT";
    
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		String statementId = null;
		try {
			if(invocation.getArgs()[1] == null) {
				invocation.getArgs()[1] = new HashMap<>();
			}
			MappedStatement statement = (MappedStatement) invocation.getArgs()[0];
			statementId = statement.getId();
			return invocation.proceed();
		} finally {
			//判断是否为PageHelper的行数查询statement
			if(statementId != null && !statementId.endsWith(countSuffix)) {
				ResultMapHelper.clear();
			}
		}
	}

}
