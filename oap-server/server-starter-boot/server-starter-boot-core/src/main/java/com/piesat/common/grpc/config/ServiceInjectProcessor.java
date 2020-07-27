
package com.piesat.common.grpc.config;

import com.google.common.collect.Lists;
import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.rpc.CommonServiceGrpc;
import io.grpc.Channel;
import io.grpc.ClientInterceptor;
import io.grpc.stub.AbstractStub;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.channelfactory.GrpcChannelFactory;
import net.devh.boot.grpc.client.inject.StubTransformer;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/13 16:10
 */
@Slf4j
@Component
public class ServiceInjectProcessor implements BeanPostProcessor {
    @Autowired
    private ApplicationContext applicationContext;
    private GrpcChannelFactory channelFactory = null;
    private List<StubTransformer> stubTransformers = null;
    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        Class<?> targeClass = bean.getClass();
        Field[] fields = targeClass.getDeclaredFields();
        for (Field field: fields ) {
            final GrpcHthtClient annotation = AnnotationUtils.findAnnotation(field, GrpcHthtClient.class);
            if (null!=annotation) {  //判断属性是否是自定义注解@MyAnnotation
                if(!field.getType().isInterface()) {  //加自定义注解的属性必须是接口类型（这样才可能出现多个不同的实例bean)
                    throw new BeanCreationException("GrpcHthtClient field must be declared an interface");
                } else {
                    try {
                        this.hanldGrpcHthtClient(field, bean, field.getType());
                        final String name = annotation.value();
                        if(null!=name&&!"".equals(name)){
                            this.getgrpcChannel(field.getType().getName(),annotation);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if(bean==null){
            log.error("grpc接口初始化失败{}",targeClass.getCanonicalName());
        }
        return bean;
    }

    /*@Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        Class<?> targeClass = bean.getClass();
        Field[] fields = targeClass.getDeclaredFields();
        for (Field field: fields ) {
            final GrpcHthtClient annotation = AnnotationUtils.findAnnotation(field, GrpcHthtClient.class);
            if (null!=annotation) {  //判断属性是否是自定义注解@MyAnnotation
                if(!field.getType().isInterface()) {  //加自定义注解的属性必须是接口类型（这样才可能出现多个不同的实例bean)
                    throw new BeanCreationException("GrpcHthtClient field must be declared an interface");
                } else {
                    try {
                        this.hanldGrpcHthtClient(field, bean, field.getType());
                        final String name = annotation.value();
                        if(null!=name&&!"".equals(name)){
                            this.getgrpcChannel(field.getType().getName(),annotation);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if(bean==null){
            log.error("grpc接口初始化失败{}",targeClass.getCanonicalName());
        }
        return bean;
    }
*/
    /**
     * Processes the given injection point and computes the appropriate value for the injection.
     *
     * @param <T> The type of the value to be injected.
     * @param injectionTarget The target of the injection.
     * @param injectionType The class that will be used to compute injection.
     * @param annotation The annotation on the target with the metadata for the injection.
     * @return The value to be injected for the given injection point.
     */
    protected <T> T processInjectionPoint(final Member injectionTarget, final Class<T> injectionType,
                                          final GrpcHthtClient annotation) {
        final List<ClientInterceptor> interceptors = interceptorsFromAnnotation(annotation);
        final String name = annotation.value();
        final Channel channel;
        try {
            channel = getChannelFactory().createChannel(name, interceptors, annotation.sortInterceptors());
            if (channel == null) {
                throw new IllegalStateException("Channel factory created a null channel for " + name);
            }
        } catch (final RuntimeException e) {
            throw new IllegalStateException("Failed to create channel: " + name, e);
        }

        final T value = valueForMember(name, injectionTarget, injectionType, channel);
        if (value == null) {
            throw new IllegalStateException(
                    "Injection value is null unexpectedly for " + name + " at " + injectionTarget);
        }
        return value;
    }

    /**
     * Lazy getter for the {@link GrpcChannelFactory}.
     *
     * @return The grpc channel factory to use.
     */
    private GrpcChannelFactory getChannelFactory() {
        if (this.channelFactory == null) {
            final GrpcChannelFactory factory = this.applicationContext.getBean(GrpcChannelFactory.class);
            this.channelFactory = factory;
            return factory;
        }
        return this.channelFactory;
    }

    /**
     * Lazy getter for the {@link StubTransformer}s.
     *
     * @return The stub transformers to use.
     */
    private List<StubTransformer> getStubTransformers() {
        if (this.stubTransformers == null) {
            final Collection<StubTransformer> transformers =
                    this.applicationContext.getBeansOfType(StubTransformer.class).values();
            this.stubTransformers = new ArrayList<>(transformers);
            return this.stubTransformers;
        }
        return this.stubTransformers;
    }

    /**
     * Gets or creates the {@link ClientInterceptor}s that are referenced in the given annotation.
     *
     * <p>
     * <b>Note:</b> This methods return value does not contain the global client interceptors because they are handled
     * by the {@link GrpcChannelFactory}.
     * </p>
     *
     * @param annotation The annotation to get the interceptors for.
     * @return A list containing the interceptors for the given annotation.
     * @throws BeansException If the referenced interceptors weren't found or could not be created.
     */
    protected List<ClientInterceptor> interceptorsFromAnnotation(final GrpcHthtClient annotation) throws BeansException {
        final List<ClientInterceptor> list = Lists.newArrayList();
        for (final Class<? extends ClientInterceptor> interceptorClass : annotation.interceptors()) {
            final ClientInterceptor clientInterceptor;
            if (this.applicationContext.getBeanNamesForType(ClientInterceptor.class).length > 0) {
                clientInterceptor = this.applicationContext.getBean(interceptorClass);
            } else {
                try {
                    clientInterceptor = interceptorClass.getConstructor().newInstance();
                } catch (final Exception e) {
                    throw new BeanCreationException("Failed to create interceptor instance", e);
                }
            }
            list.add(clientInterceptor);
        }
        for (final String interceptorName : annotation.interceptorNames()) {
            list.add(this.applicationContext.getBean(interceptorName, ClientInterceptor.class));
        }
        return list;
    }

    /**
     * Creates the instance to be injected for the given member.
     *
     * @param name The name that was used to create the channel.
     * @param <T> The type of the instance to be injected.
     * @param injectionTarget The target member for the injection.
     * @param injectionType The class that should injected.
     * @param channel The channel that should be used to create the instance.
     * @return The value that matches the type of the given field.
     * @throws BeansException If the value of the field could not be created or the type of the field is unsupported.
     */
    protected <T> T valueForMember(final String name, final Member injectionTarget, final Class<T> injectionType,
                                   final Channel channel) throws BeansException {
        if (Channel.class.equals(injectionType)) {
            return injectionType.cast(channel);
        } else if (AbstractStub.class.isAssignableFrom(injectionType)) {
            try {
                @SuppressWarnings("unchecked")
                final Class<? extends AbstractStub<?>> stubClass =
                        (Class<? extends AbstractStub<?>>) injectionType.asSubclass(AbstractStub.class);
                final Constructor<? extends AbstractStub<?>> constructor =
                        ReflectionUtils.accessibleConstructor(stubClass, Channel.class);
                AbstractStub<?> stub = constructor.newInstance(channel);
                for (final StubTransformer stubTransformer : getStubTransformers()) {
                    stub = stubTransformer.transform(name, stub);
                }
                return injectionType.cast(stub);
            } catch (final NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException e) {
                throw new BeanInstantiationException(injectionType,
                        "Failed to create gRPC client for : " + injectionTarget, e);
            }
        } else {
            throw new InvalidPropertyException(injectionTarget.getDeclaringClass(), injectionTarget.getName(),
                    "Unsupported type " + injectionType.getName());
        }
    }
    private void hanldGrpcHthtClient(Field field, Object bean, Class type) throws IllegalAccessException {
        //获取所有该属性接口的实例bean
        Object o=null;
        Object springObj=null;
        ChannelUtil channelUtil=ChannelUtil.getInstance();
        try {
            springObj=applicationContext.getBean(field.getType());
        } catch (BeansException e) {
            //e.printStackTrace();
        }
        if(null==springObj){
            o= channelUtil.getGrpcServices().get(field.getType().getName());
        }else{
            o=springObj;
        }
        //设置该域可设置修改
        field.setAccessible(true);
        //获取注解@MyAnnotation中配置的value值
        //String injectVal = field.getAnnotation(GrpcHthtClient.class).;
        //将找到的实例赋值给属性域
        field.set(bean,o);
    }
    private synchronized void getgrpcChannel(String className,GrpcHthtClient annotation){
        String name=annotation.value();
        ChannelUtil channelUtil=ChannelUtil.getInstance();
        channelUtil.getGrpcServerName().put(className,name);

    }
}

