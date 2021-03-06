package com.piesat.common.interceptor;

import com.alibaba.druid.pool.DruidDataSource;
import com.piesat.common.config.DatabseType;
import com.piesat.common.interceptor.sql.MysqlResetSql;
import com.piesat.common.interceptor.sql.PgResetSql;
import com.piesat.common.interceptor.sql.XuguResetSql;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2020-03-10 13:12
 **/

@Intercepts({@Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
), @Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}
)})
public class MybatisInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        String type = null;
        try {
            final MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            Configuration configuration = mappedStatement.getConfiguration();
            DruidDataSource dataSource = (DruidDataSource) configuration.getEnvironment().getDataSource();
            type = DatabseType.getType(dataSource.getUrl());
        } catch (Exception e) {
            //e.printStackTrace();
        }
        if ("mysql".equals(type)) {
            MysqlResetSql.intercept(invocation);
        }
        if ("xugu".equals(type)) {
            XuguResetSql.intercept(invocation);
        }
        if ("postgresql".equals(type)) {
            PgResetSql.intercept(invocation);
        }
        return invocation.proceed();
    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }


}
