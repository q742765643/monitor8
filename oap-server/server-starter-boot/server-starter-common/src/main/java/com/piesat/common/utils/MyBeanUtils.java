package com.piesat.common.utils;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;

import java.util.Date;
import java.util.Map;

/**
 * @author yaya
 * @description TODO
 * @date 2020/4/28 16:21
 */
public class MyBeanUtils {
    public static Object getObject(Object obj, Map<String, String[]> map) {
        try {    //传参1是JavaBean对象，参数2是map
            DateConverter dc = new DateConverter();
            dc.setPattern("MM/dd/yyyy HH:mm:ss");
            ConvertUtils.register(dc, Date.class);
            BeanUtils.populate(obj, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
}

