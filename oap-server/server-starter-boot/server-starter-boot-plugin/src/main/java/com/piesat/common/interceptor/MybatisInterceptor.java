package com.piesat.common.interceptor;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.piesat.common.annotation.MybatisAnnotation;
import com.piesat.common.config.DatabseType;
import com.piesat.common.interceptor.sql.MysqlResetSql;
import com.piesat.common.interceptor.sql.PgResetSql;
import com.piesat.common.interceptor.sql.XuguResetSql;
import com.piesat.common.jpa.entity.BaseEntity;
import com.piesat.common.utils.reflect.ReflectUtils;
import com.piesat.util.BaseDto;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
        String type= null;
        try {
            final MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            Configuration configuration = mappedStatement.getConfiguration();
            DruidDataSource dataSource= (DruidDataSource) configuration.getEnvironment().getDataSource();
            type = DatabseType.getType(dataSource.getUrl());
        } catch (Exception e) {
            //e.printStackTrace();
        }
        if("mysql".equals(type)){
            MysqlResetSql.intercept(invocation);
        }
        if("xugu".equals(type)){
            XuguResetSql.intercept(invocation);
        }
        if("postgresql".equals(type)){
            PgResetSql.intercept(invocation);
        }
        return invocation.proceed();
    }




    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }




}
