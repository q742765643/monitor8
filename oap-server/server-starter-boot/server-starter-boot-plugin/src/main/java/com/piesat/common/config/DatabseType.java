package com.piesat.common.config;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2020-02-27 13:24
 **/
public class DatabseType {
    public static String type = "";

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

    public static void setType(String url) {
        DatabseType.type = getType(url);
    }

    public static String getType(String url) {
        if (url.indexOf("mysql") != -1) {
            return "mysql";
        }
        if (url.indexOf("xugu") != -1) {
            return "xugu";
        }
        if (url.indexOf("postgresql") != -1) {
            return "postgresql";
        }
        return null;
    }


}

