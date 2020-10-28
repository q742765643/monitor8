package com.piesat.common.grpc.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 *@program: backup
 *@描述
 *@author  zzj
 *@创建时间  2019/5/8 11:01
 **/
@Component
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(SpringUtil.applicationContext == null) {
            SpringUtil.applicationContext = applicationContext;
        }
        System.out.println("---------------------------------------------------------------------");

        System.out.println("---------------------------------------------------------------------");

        System.out.println("---------------me.shijunjie.util.SpringUtil------------------------------------------------------");

        System.out.println("========ApplicationContext配置成功,在普通类可以通过调用SpringUtils.getAppContext()获取applicationContext对象,applicationContext="+SpringUtil.applicationContext+"========");

        System.out.println("---------------------------------------------------------------------");
    }

    /**
     *@描述
     *@参数 []
     *@返回值 org.springframework.context.ApplicationContext
     *@author zzj
     *@创建时间 2019/5/8 10:56 
     **/
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     *@描述
     *@参数 [name]
     *@返回值 java.lang.Object
     *@author zzj
     *@创建时间 2019/5/8 10:56 
     **/
    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }

    /**
     *@描述
     *@参数 [clazz]
     *@返回值 T
     *@author zzj
     *@创建时间 2019/5/8 10:56 
     **/
    public static <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }
    /**
     *@描述
     *@参数 [name, clazz]
     *@返回值 T
     *@author zzj
     *@创建时间 2019/5/8 10:59
     **/
    public static <T> T getBean(String name,Class<T> clazz){
        return getApplicationContext().getBean(name, clazz);
    }


    public static Object getBeanOrNull(String name){
        try {
            return getApplicationContext().getBean(name);
        } catch (BeansException e) {
        }
        return null;
    }
}
