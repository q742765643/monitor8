package com.piesat.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piesat.common.interceptor.CurrentUserArgumentResolver;
import com.piesat.common.interceptor.RequestParamMethodArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/13 18:09
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Autowired
    private ObjectMapper objectMapper;
    @Resource
    private CurrentUserArgumentResolver currentUserArgumentResolver;
    @Autowired
    private ConfigurableBeanFactory beanFactory;
    @Value("${fileUpload.savaPath:/zzj/data/upload}")
    private String savePath;
    @Value("${fileUpload.httpPath:/upload}")
    private String httpPath;


    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new RequestParamMethodArgumentResolver(beanFactory, true));
        //argumentResolvers.add(currentUserArgumentResolver);
        // 注册Spring data jpa pageable的参数分解器
        // argumentResolvers.add(new CurrentUserArgumentResolver());
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(httpPath + "/**").addResourceLocations("file:" + savePath + "/");
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
   /*  @Override
   public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
       *//* for (int i = 0; i < converters.size(); i++) {
            HttpMessageConverter<?> messageConverter = converters.get(i);
            if (messageConverter instanceof MappingJackson2HttpMessageConverter) {
                converters.remove(i);
            }
        }*//*
        converters.add(formHttpMessageConverter());


    }*/

  /*  @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(){
        ObjectMapper objectMapper = new ObjectMapper();
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new CustomEncryptHttpMessageConverter(objectMapper);
        // 设置日期格式
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 设置中文编码格式
        List<MediaType> list = new ArrayList<MediaType>();
        list.add(MediaType.APPLICATION_JSON_UTF8);
        list.add(MediaType.APPLICATION_JSON);
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(list);
        return mappingJackson2HttpMessageConverter;
    }*/
/*  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
      // add方法可以指定顺序，有多个自定义的WebMvcConfigurerAdapter时，可以改变相互之间的顺序
      // 但是都在springmvc内置的converter前面
      converters.add(formHttpMessageConverter());
  }*/

    // 添加converter的第三种方式
    // 同一个WebMvcConfigurerAdapter中的configureMessageConverters方法先于extendMessageConverters方法执行
    // 可以理解为是三种方式中最后执行的一种，不过这里可以通过add指定顺序来调整优先级，也可以使用remove/clear来删除converter，功能强大
    // 使用converters.add(xxx)会放在最低优先级（List的尾部）
    // 使用converters.add(0,xxx)会放在最高优先级（List的头部）

   /* @Bean
    public MyFormHttpMessageConverter formHttpMessageConverter(){
        ObjectMapper objectMapper=new ObjectMapper();
        return new MyFormHttpMessageConverter(objectMapper);
    }*/

    @Override
    public void addCorsMappings(CorsRegistry registry) {
      /*  registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
                .allowedHeaders("*")
                .maxAge(3600)
                .allowCredentials(true);*/
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                //设置缓存时间，减少重复响应
                .maxAge(3600)
                .exposedHeaders("Content-Disposition")
                //设置是否允许跨域传cookie
                .allowCredentials(true);
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getHttpPath() {
        return httpPath;
    }

    public void setHttpPath(String httpPath) {
        this.httpPath = httpPath;
    }
}
