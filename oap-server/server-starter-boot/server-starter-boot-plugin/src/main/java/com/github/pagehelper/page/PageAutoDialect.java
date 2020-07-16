//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.github.pagehelper.page;

import com.github.pagehelper.Dialect;
import com.github.pagehelper.PageException;
import com.github.pagehelper.dialect.AbstractHelperDialect;
import com.github.pagehelper.dialect.helper.*;
import com.github.pagehelper.util.StringUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import javax.sql.DataSource;
import org.apache.ibatis.mapping.MappedStatement;

public class PageAutoDialect {
    private static Map<String, Class<? extends Dialect>> dialectAliasMap = new HashMap();
    private boolean autoDialect = true;
    private boolean closeConn = true;
    private Properties properties;
    private Map<String, AbstractHelperDialect> urlDialectMap = new ConcurrentHashMap();
    private ReentrantLock lock = new ReentrantLock();
    private AbstractHelperDialect delegate;
    private ThreadLocal<AbstractHelperDialect> dialectThreadLocal = new ThreadLocal();

    public PageAutoDialect() {
    }

    public static void registerDialectAlias(String alias, Class<? extends Dialect> dialectClass) {
        dialectAliasMap.put(alias, dialectClass);
    }

    public void initDelegateDialect(MappedStatement ms) {
        if(this.delegate == null) {
            if(this.autoDialect) {
                this.delegate = this.getDialect(ms);
            } else {
                this.dialectThreadLocal.set(this.getDialect(ms));
            }
        }

    }

    public AbstractHelperDialect getDelegate() {
        return this.delegate != null?this.delegate:(AbstractHelperDialect)this.dialectThreadLocal.get();
    }

    public void clearDelegate() {
        this.dialectThreadLocal.remove();
    }

    private String fromJdbcUrl(String jdbcUrl) {
        Iterator var2 = dialectAliasMap.keySet().iterator();

        String dialect;
        do {
            if(!var2.hasNext()) {
                return null;
            }

            dialect = (String)var2.next();
        } while(jdbcUrl.indexOf(":" + dialect + ":") == -1);

        return dialect;
    }

    private Class resloveDialectClass(String className) throws Exception {
        return dialectAliasMap.containsKey(className.toLowerCase())?(Class)dialectAliasMap.get(className.toLowerCase()):Class.forName(className);
    }

    private AbstractHelperDialect initDialect(String dialectClass, Properties properties) {
        if(StringUtil.isEmpty(dialectClass)) {
            throw new PageException("使用 PageHelper 分页插件时，必须设置 helper 属性");
        } else {
            AbstractHelperDialect dialect;
            try {
                Class sqlDialectClass = this.resloveDialectClass(dialectClass);
                if(!AbstractHelperDialect.class.isAssignableFrom(sqlDialectClass)) {
                    throw new PageException("使用 PageHelper 时，方言必须是实现 " + AbstractHelperDialect.class.getCanonicalName() + " 接口的实现类!");
                }

                dialect = (AbstractHelperDialect)sqlDialectClass.newInstance();
            } catch (Exception var5) {
                throw new PageException("初始化 helper [" + dialectClass + "]时出错:" + var5.getMessage(), var5);
            }

            dialect.setProperties(properties);
            return dialect;
        }
    }

    private String getUrl(DataSource dataSource) {
        Connection conn = null;

        String var3;
        try {
            conn = dataSource.getConnection();
            var3 = conn.getMetaData().getURL();
        } catch (SQLException var12) {
            throw new PageException(var12);
        } finally {
            if(conn != null) {
                try {
                    if(this.closeConn) {
                        conn.close();
                    }
                } catch (SQLException var11) {
                    ;
                }
            }

        }

        return var3;
    }

    private AbstractHelperDialect getDialect(MappedStatement ms) {
        DataSource dataSource = ms.getConfiguration().getEnvironment().getDataSource();
        String url = this.getUrl(dataSource);
        if(this.urlDialectMap.containsKey(url)) {
            return (AbstractHelperDialect)this.urlDialectMap.get(url);
        } else {
            AbstractHelperDialect var6;
            try {
                this.lock.lock();
                if(this.urlDialectMap.containsKey(url)) {
                    AbstractHelperDialect var10 = (AbstractHelperDialect)this.urlDialectMap.get(url);
                    return var10;
                }

                if(StringUtil.isEmpty(url)) {
                    throw new PageException("无法自动获取jdbcUrl，请在分页插件中配置dialect参数!");
                }

                String dialectStr = this.fromJdbcUrl(url);
                if(dialectStr == null) {
                    throw new PageException("无法自动获取数据库类型，请通过 helperDialect 参数指定!");
                }

                AbstractHelperDialect dialect = this.initDialect(dialectStr, this.properties);
                this.urlDialectMap.put(url, dialect);
                var6 = dialect;
            } finally {
                this.lock.unlock();
            }

            return var6;
        }
    }

    public void setProperties(Properties properties) {
        String closeConn = properties.getProperty("closeConn");
        if(StringUtil.isNotEmpty(closeConn)) {
            this.closeConn = Boolean.parseBoolean(closeConn);
        }

        String useSqlserver2012 = properties.getProperty("useSqlserver2012");
        if(StringUtil.isNotEmpty(useSqlserver2012) && Boolean.parseBoolean(useSqlserver2012)) {
            registerDialectAlias("sqlserver", SqlServer2012Dialect.class);
            registerDialectAlias("sqlserver2008", SqlServerDialect.class);
        }

        String dialectAlias = properties.getProperty("dialectAlias");
        if(StringUtil.isNotEmpty(dialectAlias)) {
            String[] alias = dialectAlias.split(";");

            for(int i = 0; i < alias.length; ++i) {
                String[] kv = alias[i].split("=");
                if(kv.length != 2) {
                    throw new IllegalArgumentException("dialectAlias 参数配置错误，请按照 alias1=xx.dialectClass;alias2=dialectClass2 的形式进行配置!");
                }

                for(int j = 0; j < kv.length; ++j) {
                    try {
                        Class<? extends Dialect> diallectClass = (Class<? extends Dialect>) Class.forName(kv[1]);
                        registerDialectAlias(kv[0], diallectClass);
                    } catch (ClassNotFoundException var10) {
                        throw new IllegalArgumentException("请确保 dialectAlias 配置的 Dialect 实现类存在!", var10);
                    }
                }
            }
        }

        String dialect = properties.getProperty("helperDialect");
        String runtimeDialect = properties.getProperty("autoRuntimeDialect");
        if(StringUtil.isNotEmpty(runtimeDialect) && "TRUE".equalsIgnoreCase(runtimeDialect)) {
            this.autoDialect = false;
            this.properties = properties;
        } else if(StringUtil.isEmpty(dialect)) {
            this.autoDialect = true;
            this.properties = properties;
        } else {
            this.autoDialect = false;
            this.delegate = this.initDialect(dialect, properties);
        }

    }

    static {
        registerDialectAlias("hsqldb", HsqldbDialect.class);
        registerDialectAlias("h2", HsqldbDialect.class);
        registerDialectAlias("postgresql", HsqldbDialect.class);
        registerDialectAlias("phoenix", HsqldbDialect.class);
        registerDialectAlias("mysql", MySqlDialect.class);
        registerDialectAlias("mariadb", MySqlDialect.class);
        registerDialectAlias("sqlite", MySqlDialect.class);
        registerDialectAlias("oracle", OracleDialect.class);
        registerDialectAlias("db2", Db2Dialect.class);
        registerDialectAlias("informix", InformixDialect.class);
        registerDialectAlias("informix-sqli", InformixDialect.class);
        registerDialectAlias("sqlserver", SqlServerDialect.class);
        registerDialectAlias("sqlserver2012", SqlServer2012Dialect.class);
        registerDialectAlias("derby", SqlServer2012Dialect.class);
        registerDialectAlias("dm", OracleDialect.class);
        registerDialectAlias("edb", OracleDialect.class);
        //registerDialectAlias("es", EsSqlDialect.class);

    }
}
