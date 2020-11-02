package com.github.pagehelper.dialect.helper;

import com.github.pagehelper.Page;
import com.github.pagehelper.dialect.AbstractHelperDialect;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;

import java.util.Map;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2020-05-13 16:51
 **/
public class EsSqlDialect extends AbstractHelperDialect {
    public EsSqlDialect() {

    }

    @Override
    public Object processPageParameter(MappedStatement ms, Map<String, Object> paramMap, Page page, BoundSql boundSql, CacheKey pageKey) {

        return paramMap;
    }

    @Override
    public String getPageSql(String sql, Page page, CacheKey pageKey) {
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 14);
        sqlBuilder.append(sql);
        if (page.getStartRow() == 0) {
            sqlBuilder.append(" LIMIT " + page.getTotal());
        } else {
            sqlBuilder.append(" LIMIT " + page.getStartRow() + "," + page.getPageSize());
        }

        return sqlBuilder.toString();
    }
}


