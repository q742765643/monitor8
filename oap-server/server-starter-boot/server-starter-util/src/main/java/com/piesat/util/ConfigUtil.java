package com.piesat.util;


import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

/**
 * jConfig工具类 2008/12/31
 *
 * @author yanjianzhong
 */
public class ConfigUtil {
    private static final Configuration config = ConfigurationManager.getConfiguration("jhtht");
//	private static  Configuration config = null;
//	static{
//		String tomcat_home = System.getProperty("catalina.home");
//		config = ConfigurationManager.getConfiguration(tomcat_home + "/htconf/smart");
//	}
//	

    /**
     * 取得某个类别的属性值,支持默认值
     *
     * @param str0         类别名称
     * @param str1         属性名称
     * @param defaultValue 默认值
     * @return 返回属性的值
     */
    public static String getProperty(String type, String propesties, String defaultValue) {
        return config.getProperty(propesties, defaultValue, type);
    }

    /**
     * 取得某个类别的属性值
     *
     * @param str0 类别名称
     * @param str1 属性名称
     * @return 返回属性的值
     */
    public static String getProperty(String type, String propesties) {
        return getProperty(type, propesties, null);
    }

    public static String[] getArray(String arg0) {
        return config.getArray(arg0);
    }


    /**
     * 取得默认类别(general)的属性的值
     *
     * @param str0 属性名称
     * @return 返回属性的值
     */
    public static String getProperty(String str0) {
        return config.getProperty(str0);
    }
}
