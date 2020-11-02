package com.piesat.common.grpc.config;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import io.grpc.Channel;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannelBuilder;
import io.grpc.internal.DnsNameResolverProvider;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.channelfactory.GrpcChannelFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/12/13 14:14
 */
@Slf4j
public class ChannelUtil {
    //private ConcurrentHashMap<String,CommonServiceGrpc.CommonServiceBlockingStub> blockingStub=new ConcurrentHashMap<>();
    private volatile static ChannelUtil instance = null;
    private ConcurrentHashMap<String, Object> grpcServices = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Channel> grpcChannel = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> grpcServerName = new ConcurrentHashMap<>();
    private GrpcChannelFactory channelFactory = null;

    //私有的默认构造函数
    public ChannelUtil() {
    }

    public static ChannelUtil getInstance() {
        if (instance == null) {
            synchronized (ChannelUtil.class) {
                if (instance == null) {
                    instance = new ChannelUtil();
                }
            }
        }
        return instance;
    }

    public synchronized ConcurrentHashMap<String, Object> getGrpcServices() {
        return grpcServices;
    }

    public synchronized ConcurrentHashMap<String, Channel> getGrpcChannel() {
        return grpcChannel;
    }

    public synchronized ConcurrentHashMap<String, String> getGrpcServerName() {
        return grpcServerName;
    }

    public synchronized void getgrpcChannel(String className, GrpcHthtService annotation, ApplicationContext applicationContext) {
        String name = annotation.server();
        if ("".equals(name) || null == name) {
            return;
        }
        grpcServerName.put(className, name);
        if (null == grpcChannel.get(name)) {
            Channel channel = processInjectionPoint(null, Channel.class, annotation, applicationContext);
            if (null != channel) {
                grpcChannel.put(name, channel);
                //blockingStub.put(name,CommonServiceGrpc.newBlockingStub(channel));
            } else {
                log.error("通道初始化失败{}", name);
            }
        }
    }

    public synchronized void getgrpcChannel(String serviceName, String host, int port) {
        Channel channel = null;
        if (null == grpcChannel.get(serviceName)) {
            channel = ManagedChannelBuilder.forAddress(host, port)
                    .defaultLoadBalancingPolicy("round_robin")
                    .nameResolverFactory(new DnsNameResolverProvider())
                    .usePlaintext().build();
            grpcChannel.put(serviceName, channel);
            //blockingStub.put(serviceName,CommonServiceGrpc.newBlockingStub(channel));
        }

    }

    protected <T> T processInjectionPoint(final Member injectionTarget, final Class<T> injectionType,
                                          final GrpcHthtService annotation, ApplicationContext applicationContext) {
        final List<ClientInterceptor> interceptors = new ArrayList<>();
        final String name = annotation.server();
        final Channel channel;
        try {
            channel = getChannelFactory(applicationContext).createChannel(name, interceptors, false);
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


    private GrpcChannelFactory getChannelFactory(ApplicationContext applicationContext) {
        if (this.channelFactory == null) {
            final GrpcChannelFactory factory = applicationContext.getBean(GrpcChannelFactory.class);
            this.channelFactory = factory;
            return factory;
        }
        return this.channelFactory;
    }

    protected <T> T valueForMember(final String name, final Member injectionTarget, final Class<T> injectionType,
                                   final Channel channel) throws BeansException {
        if (Channel.class.equals(injectionType)) {
            return injectionType.cast(channel);
        }
        return null;
    }
}
