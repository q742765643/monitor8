package com.piesat.common.grpc.config;


import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.annotation.GrpcServiceScan;
import com.piesat.common.grpc.binding.GrpcServiceProxy;
import com.piesat.common.grpc.service.GrpcClientService;
import com.piesat.common.grpc.service.SerializeService;
import com.piesat.common.grpc.service.impl.SofaHessianSerializeService;
import com.piesat.common.grpc.util.ClassNameUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
@Configuration
@EnableConfigurationProperties(GrpcProperties.class)
public class GrpcAutoConfiguration {

    private static BeanFactory beanFactory;
    private final ApplicationContext applicationContext;
    private final GrpcProperties grpcProperties;

    public GrpcAutoConfiguration(ApplicationContext applicationContext, GrpcProperties grpcProperties) {
        this.applicationContext = applicationContext;
        this.grpcProperties = grpcProperties;
        ProxyUtil.registerBeans(beanFactory, applicationContext);
        GrpcAutoConfiguration.beanFactory = null;
        /*if(null==ProxyUtil.applicationContext){
            ProxyUtil.applicationContext=applicationContext;
        }*/
    }

    /**
     * 全局 RPC 序列化/反序列化
     */
    @Bean
    @ConditionalOnMissingBean(SerializeService.class)
    public SerializeService serializeService() {
        return new SofaHessianSerializeService();
    }

    @Bean
    @ConditionalOnMissingBean(GrpcClientService.class)
    public GrpcClientService grpcClientService() {
        return new GrpcClientService();
    }

    /**
     * 手动扫描 @GrpcService 注解的接口，生成动态代理类，注入到 Spring 容器
     */
    public static class ExternalGrpcServiceScannerRegistrar implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware {

        private BeanFactory beanFactory;

        private ResourceLoader resourceLoader;

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
            GrpcAutoConfiguration.beanFactory = beanFactory;
        }

        @Override
        public void setResourceLoader(ResourceLoader resourceLoader) {
            this.resourceLoader = resourceLoader;
        }

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            ClassPathBeanDefinitionScanner scanner = new ClassPathGrpcServiceScanner(registry);
            scanner.setResourceLoader(this.resourceLoader);
            scanner.addIncludeFilter(new AnnotationTypeFilter(GrpcHthtService.class));
            Set<BeanDefinition> beanDefinitions = scanPackages(importingClassMetadata, scanner);
            ProxyUtil.registerMap(beanFactory, beanDefinitions);
        }

        /**
         * 包扫描
         */
        private Set<BeanDefinition> scanPackages(AnnotationMetadata importingClassMetadata, ClassPathBeanDefinitionScanner scanner) {
            List<String> packages = new ArrayList<>();
            Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(GrpcServiceScan.class.getCanonicalName());
            if (annotationAttributes != null) {
                String[] basePackages = (String[]) annotationAttributes.get("packages");
                if (basePackages.length > 0) {
                    packages.addAll(Arrays.asList(basePackages));
                }
            }
            Set<BeanDefinition> beanDefinitions = new HashSet<>();
            if (CollectionUtils.isEmpty(packages)) {
                return beanDefinitions;
            }
            packages.forEach(pack -> beanDefinitions.addAll(scanner.findCandidateComponents(pack)));
            return beanDefinitions;
        }

    }

    protected static class ClassPathGrpcServiceScanner extends ClassPathBeanDefinitionScanner {

        ClassPathGrpcServiceScanner(BeanDefinitionRegistry registry) {
            super(registry, false);
        }

        @Override
        protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
        }

    }

    public static class ProxyUtil {
        public static ApplicationContext applicationContext = null;


        static void registerMap(BeanFactory beanFactory, Set<BeanDefinition> beanDefinitions) {
            for (BeanDefinition beanDefinition : beanDefinitions) {
                String className = beanDefinition.getBeanClassName();
                if (StringUtils.isEmpty(className)) {
                    continue;
                }
                try {
                    ChannelUtil channelUtil = ChannelUtil.getInstance();
                    // 创建代理类
                    Class<?> target = Class.forName(className);
                    Object invoker = new Object();
                    InvocationHandler invocationHandler = new GrpcServiceProxy<>(target, invoker);
                    Object proxy = Proxy.newProxyInstance(GrpcHthtService.class.getClassLoader(), new Class[]{target}, invocationHandler);

                    // 注册到 Spring 容器
                    channelUtil.getGrpcServices().put(className, proxy);
                } catch (ClassNotFoundException e) {
                    log.warn("class not found : " + className);
                }
            }
        }

        static void registerBeans(BeanFactory beanFactory, ApplicationContext applicationContext) {
            ChannelUtil channelUtil = ChannelUtil.getInstance();
            for (Map.Entry<String, Object> map : channelUtil.getGrpcServices().entrySet()) {
                String className = map.getKey();
                if (StringUtils.isEmpty(className)) {
                    continue;
                }

                try {
                    Class<?> target = Class.forName(className);
                    int length = applicationContext.getBeanNamesForType(target).length;
                    if (length == 0) {
                        String beanName = ClassNameUtils.beanName(className);
                        GrpcHthtService grpcHthtService = target.getAnnotation(GrpcHthtService.class);
                        ChannelUtil.getInstance().getgrpcChannel(className, grpcHthtService, applicationContext);
                        ((DefaultListableBeanFactory) beanFactory).registerSingleton(beanName, map.getValue());
                    }
                } catch (ClassNotFoundException e) {
                    log.warn("class not found : " + className);

                }


            }
        }

    }


}
