package com.piesat.common.interceptor.sql;

import com.piesat.common.interceptor.SqlUtil;
import com.piesat.common.jpa.entity.BaseEntity;
import com.piesat.common.utils.reflect.ReflectUtils;
import com.piesat.util.BaseDto;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SetOperationList;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.reflection.MetaObject;

import java.io.StringReader;
import java.util.List;
import java.util.Map;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2020-05-14 13:02
 **/
public class MysqlResetSql {
    public static void intercept(Invocation invocation) throws Throwable{
        final MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];

        Object parameter = invocation.getArgs()[1];
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        List<ResultMap> resultMaps=mappedStatement.getResultMaps();
        if (StringUtils.isBlank(boundSql.getSql())) {
            return ;
        }
        String originalSql = boundSql.getSql().trim();
        String add="";
        if (parameter instanceof BaseEntity) {
            BaseEntity entity = (BaseEntity) parameter;
            CCJSqlParserManager pm = new CCJSqlParserManager();
            Statement parse = pm.parse(new StringReader(originalSql));
            Select noOrderSelect = (Select)parse;
            SelectBody selectBody = noOrderSelect.getSelectBody();
            if (selectBody instanceof SetOperationList){
                return ;
            }
            add=SqlUtil.resetSql(entity.getParams(),selectBody,resultMaps);
            if(com.piesat.common.utils.StringUtils.isNotNullString(add)) {
                try {

                    PlainSelect setOperationList = (PlainSelect)selectBody;
                    originalSql = noOrderSelect.toString();
                    if (null!=setOperationList.getOrderByElements()){
                        setOperationList.setOrderByElements(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String sql = originalSql+" order by  " + add;
                originalSql = sql;
            }
        }  if (parameter instanceof BaseDto) {
            BaseDto entity = (BaseDto) parameter;
            CCJSqlParserManager pm = new CCJSqlParserManager();
            Statement parse = pm.parse(new StringReader(originalSql));
            Select noOrderSelect = (Select)parse;
            SelectBody selectBody = noOrderSelect.getSelectBody();
            if (selectBody instanceof SetOperationList){
                return ;
            }
            add=SqlUtil.resetSql(entity.getParams(),selectBody,resultMaps);

            if(com.piesat.common.utils.StringUtils.isNotNullString(add)) {
                try {

                    PlainSelect setOperationList = (PlainSelect)selectBody;
                    originalSql = noOrderSelect.toString();
                    if (null!=setOperationList.getOrderByElements()){
                        setOperationList.setOrderByElements(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String sql = originalSql+" order by  " + add;
                originalSql = sql;
            }
        }else if(parameter instanceof Map){
            Map map = (Map) parameter;
            String pp= null;
            try {
                pp = (String) map.get("params");
            } catch (Exception e) {
                // e.printStackTrace();
            }
            CCJSqlParserManager pm = new CCJSqlParserManager();
            Statement parse = pm.parse(new StringReader(originalSql));
            Select noOrderSelect = (Select)parse;
            SelectBody selectBody = noOrderSelect.getSelectBody();
            if (selectBody instanceof SetOperationList){
                return ;
            }
            add= SqlUtil.resetSql(pp,selectBody,resultMaps);
            if(com.piesat.common.utils.StringUtils.isNotNullString(add)){
                try {
                    PlainSelect setOperationList = (PlainSelect)selectBody;
                    originalSql = noOrderSelect.toString();
                    if (null!=setOperationList.getOrderByElements()){
                        setOperationList.setOrderByElements(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String sql = originalSql+" order by  " + add;
                originalSql = sql;
            }

        }
        if(!com.piesat.common.utils.StringUtils.isNotNullString(add)){
            return ;
        }
        //String mid = mappedStatement.getId();
        BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), originalSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
        if (ReflectUtils.getFieldValue(boundSql, "metaParameters") != null) {
            MetaObject mo = (MetaObject) ReflectUtils.getFieldValue(boundSql, "metaParameters");
            ReflectUtils.setFieldValue(newBoundSql, "metaParameters", mo);
        }
        if (ReflectUtils.getFieldValue(boundSql, "additionalParameters") != null) {
            Map<String, Object> additionalParameters =  ReflectUtils.getFieldValue(boundSql, "additionalParameters");
            ReflectUtils.setFieldValue(newBoundSql, "additionalParameters", additionalParameters);
        }
        MappedStatement newMs = SqlUtil.copyFromMappedStatement(mappedStatement,new SqlUtil.BoundSqlSqlSource(newBoundSql));

        invocation.getArgs()[0] = newMs;
    }
}

