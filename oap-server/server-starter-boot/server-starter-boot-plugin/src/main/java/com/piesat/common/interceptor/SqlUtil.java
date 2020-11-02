package com.piesat.common.interceptor;

import com.alibaba.fastjson.JSON;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.apache.ibatis.mapping.*;

import java.util.List;
import java.util.Map;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2020-05-14 13:07
 **/
public class SqlUtil {

    public static MappedStatement copyFromMappedStatement(MappedStatement ms,
                                                          SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(),
                ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null) {
            for (String keyProperty : ms.getKeyProperties()) {
                builder.keyProperty(keyProperty);
            }
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.cache(ms.getCache());
        return builder.build();
    }

    public static String resetSql(String param, SelectBody selectBody, List<ResultMap> resultMaps) {
        List<SelectItem> selectItems = ((PlainSelect) selectBody).getSelectItems();
        StringBuilder addSql = new StringBuilder();
        List<ResultMapping> resultMappings = resultMaps.get(0).getResultMappings();
        try {
            if (null == param || !com.piesat.common.utils.StringUtils.isNotNullString(param)) {
                return "";
            }
            Map<String, Object> map = JSON.parseObject(param, Map.class);

            Map<String, String> mapList = (Map<String, String>) map.get("orderBy");
            if (null == mapList || mapList.isEmpty()) {
                return "";

            }
            mapList.forEach((k, v) -> {
                String name = k.trim();
                if (name.indexOf(".") != -1) {
                    name = name.substring(name.lastIndexOf(".") + 1);
                }
                if (null != resultMappings && !resultMappings.isEmpty()) {
                    for (ResultMapping resultMapping : resultMappings) {
                        if (name.toUpperCase().equals(resultMapping.getProperty().toUpperCase())) {
                            name = resultMapping.getColumn();
                            break;
                        }
                    }
                }
                if (null != selectItems && !selectItems.isEmpty()) {
                    for (SelectItem selectItem : selectItems) {
                        SelectExpressionItem selectExpressionItem = (SelectExpressionItem) selectItem;
                        String sitem = selectExpressionItem.getExpression().toString().toUpperCase();
                        sitem = sitem.replaceAll("_", "");
                        String vname = name.toUpperCase().replaceAll("_", "");
                        if (sitem.indexOf(vname) != -1) {
                            name = selectExpressionItem.getExpression().toString();
                            break;
                        }
                    }
                }
                addSql.append(name).append(" ").append(v).append(",");
            });
            String sql = addSql.toString();
            return sql.substring(0, sql.length() - 1);
        } catch (Exception e) {
        }
        return "";

    }

    public static String replace(String string, String toReplace, String replacement) {
//        int pos = string.lastIndexOf(toReplace);
        int pos = string.indexOf(toReplace);
        if (pos > -1) {
            return string.substring(0, pos)
                    + replacement
                    + string.substring(pos + toReplace.length(), string.length());
        } else {
            return string;
        }
    }

    public static class BoundSqlSqlSource implements SqlSource {
        BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }

}

