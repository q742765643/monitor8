package com.piesat.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/14 9:11
 */
//@Configuration
public class FilterConfig {
    /*@Bean
    public FilterRegistrationBean<DataFilter> registerDataFilter(ObjectMapper objectMapper) {
        FilterRegistrationBean<DataFilter> registration = new FilterRegistrationBean<>(new DataFilter(objectMapper));
        // registration.setFilter(new DataFilter(objectMapper));
        registration.addUrlPatterns("/api/*");
        //registration.addInitParameter("exclusions","/swagger-ui.html");
        registration.setName("DataFilter");
        registration.setOrder(1);  //值越小，Filter越靠前。
        return registration;
    }*/
}
