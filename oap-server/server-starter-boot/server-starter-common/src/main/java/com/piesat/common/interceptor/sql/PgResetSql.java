package com.piesat.common.interceptor.sql;

import com.piesat.common.interceptor.SqlUtil;
import com.piesat.common.jpa.entity.BaseEntity;
import com.piesat.common.utils.StringUtils;
import com.piesat.common.utils.reflect.ReflectUtils;
import com.piesat.util.BaseDto;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SetOperationList;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2020-05-14 13:12
 **/
public class PgResetSql {
    public static void intercept(Invocation invocation) throws Throwable {

        final MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];

        Object parameter = invocation.getArgs()[1];
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        List<ResultMap> resultMaps=mappedStatement.getResultMaps();
        if (StringUtils.isBlank(boundSql.getSql())) {
            return ;
        }
        String originalSql = boundSql.getSql().trim();
        Configuration configuration = mappedStatement.getConfiguration();
        List<ParameterMapping> parameterMappings=new ArrayList<>();
        originalSql=formatSql(originalSql,configuration,boundSql,parameterMappings);

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
            add=SqlUtil.resetSql(pp,selectBody,resultMaps);
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
      /* if(!com.piesat.common.utils.StringUtils.isNotNullString(add)){
            return invocation.proceed();
        }*/
        //String mid = mappedStatement.getId();
        BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), originalSql, parameterMappings, boundSql.getParameterObject());
        if (ReflectUtils.getFieldValue(boundSql, "metaParameters") != null) {
            MetaObject mo = (MetaObject) ReflectUtils.getFieldValue(boundSql, "metaParameters");
            ReflectUtils.setFieldValue(newBoundSql, "metaParameters", mo);
        }
        if (ReflectUtils.getFieldValue(boundSql, "additionalParameters") != null) {
            Map<String, Object> additionalParameters =  ReflectUtils.getFieldValue(boundSql, "additionalParameters");
            ReflectUtils.setFieldValue(newBoundSql, "additionalParameters", additionalParameters);
        }
        MappedStatement newMs = SqlUtil.copyFromMappedStatement(mappedStatement, new SqlUtil.BoundSqlSqlSource(newBoundSql));

        invocation.getArgs()[0] = newMs;

    }
    private static String formatSql(String sql, Configuration configuration, BoundSql boundSql,  List<ParameterMapping> parameterMappingNew ) {

        sql=sql.trim();
        //填充占位符, 目前基本不用mybatis存储过程调用,故此处不做考虑
        Object parameterObject = boundSql.getParameterObject();
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();

        Map<Integer,String> parameters = new LinkedHashMap<>();
        List<ParameterMapping> parameterMappings=boundSql.getParameterMappings();
        parameterMappingNew.addAll(parameterMappings);
        if (parameterMappings != null) {
            MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    //  参数值
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    //  获取参数名称
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        // 获取参数值
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        // 如果是单个值则直接赋值
                        value = parameterObject;
                    } else {
                        value = metaObject == null ? null : metaObject.getValue(propertyName);
                    }


                    StringBuilder builder = new StringBuilder();

                    if (value instanceof String) {
                        builder.append("'");
                        builder.append(value);
                        builder.append("'");
                        parameters.put(i,builder.toString());
                        parameterMappingNew.remove(parameterMapping);
                    }


                }
            }
        }
        String[]  sqls=sql.split("\\?");
        String newSql="";
        if(sqls.length>0&&parameters.size()>0){
            for(int i=0;i<sqls.length;i++){
                String s=parameters.get(i);
                if(null!=s){
                    newSql=newSql+" "+sqls[i].trim()+s;
                }else {
                    newSql=newSql+" "+sqls[i].trim()+"?";
                }
            }
        }

        if(!"".equals(newSql)){
            if(!sql.endsWith("?")){
                newSql=newSql.substring(0,newSql.length()-1);
            }
           sql=newSql;
        }
        //for (String value : parameters) {
           // sql = sql.replaceFirst("\\?", value);
        //}
        return sql;
    }
}

