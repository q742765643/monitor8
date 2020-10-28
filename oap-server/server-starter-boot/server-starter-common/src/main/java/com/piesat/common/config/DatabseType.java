package com.piesat.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2020-02-27 13:24
 **/
public class DatabseType {
    public static String type="";


    public static void setType(String url) {
        DatabseType.type = getType(url);
    }

    static {
      /*  YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application.yml"));
        Properties properties = yaml.getObject();
        String active = properties.getProperty("spring.profiles.active");
        if(active!=null){
            yaml.setResources(new ClassPathResource("application-"+active+".yml"));
            properties=yaml.getObject();
        }
        String url= (String) properties.get("spring.datasource.url");
        if(url.indexOf("mysql")!=-1){
            type="mysql";
        }
        if(url.indexOf("xugu")!=-1){
            type="xugu";
        }
        if(url.indexOf("postgresql")!=-1){
            type="postgresql";
        }*/

    }

    public static String getType(String url){
        if(url.indexOf("mysql")!=-1){
            return "mysql";
        }
        if(url.indexOf("xugu")!=-1){
            return "xugu";
        }
        if(url.indexOf("postgresql")!=-1){
            return "postgresql";
        }
        return null;
    }


}

