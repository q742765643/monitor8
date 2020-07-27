package com.piesat.common.grpc.config;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.rpc.CommonServiceGrpc;
import io.grpc.Channel;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.internal.DnsNameResolverProvider;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.channelfactory.GrpcChannelFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Member;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/12/13 14:14
 */
@Slf4j
public class ChannelUtil {
    private ConcurrentHashMap<String,Object> grpcServices=new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> grpcServerName=new ConcurrentHashMap<>();
    private ConcurrentHashMap<String,ConcurrentHashMap<String, ManagedChannel>>  channel=new ConcurrentHashMap<>();
    private volatile static  ChannelUtil instance=null;

    public synchronized ConcurrentHashMap<String, Object> getGrpcServices() {
        return grpcServices;
    }

    public synchronized ConcurrentHashMap<String, String> getGrpcServerName() {
        return grpcServerName;
    }

    public  ConcurrentHashMap<String, ConcurrentHashMap<String,ManagedChannel>> getChannel() {
        return channel;
    }



    //私有的默认构造函数
    public ChannelUtil(){}
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

    private GrpcChannelFactory channelFactory = null;

    public synchronized void getgrpcChannel(String className, GrpcHthtService annotation,ApplicationContext applicationContext){
        String name=annotation.server();
        if("".equals(name)||null==name){
            return;
        }
        grpcServerName.put(className,name);

    }
    public  synchronized  void getgrpcChannel(String serviceName, String host,int port){
        Channel channel=null;
            channel = ManagedChannelBuilder.forAddress(host, port)
                    .defaultLoadBalancingPolicy("round_robin")
                    .nameResolverFactory(new DnsNameResolverProvider())
                    .usePlaintext().build();

            //blockingStub.put(serviceName,CommonServiceGrpc.newBlockingStub(channel));


    }
    protected <T> T processInjectionPoint(final Member injectionTarget, final Class<T> injectionType,
                                          final GrpcHthtService annotation,ApplicationContext applicationContext) {
        final List<ClientInterceptor> interceptors =new ArrayList<>();
        final String name = annotation.server();
        final Channel channel;
        try {
            channel = getChannelFactory(applicationContext).createChannel(name, interceptors,false);
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

    public Channel selectChannel(String name){
        Map<String,ManagedChannel> map=channel.get(name);
        //重新建立一個map,避免出現由於服務器上線和下線導致的並發問題
        Map<String,Integer> serverMap  = new HashMap<String,Integer>();
        map.forEach((k,v)->{
            serverMap.put(k,1);
        });
        //獲取ip列表list
        Set<String> keySet = serverMap.keySet();
        Iterator<String> it = keySet.iterator();

        List<String> serverList = new ArrayList<String>();

        while (it.hasNext()) {
            String server = it.next();
            Integer weight = serverMap.get(server);
            for (int i = 0; i < weight; i++) {
                serverList.add(server);
            }
        }
        Random random = new Random();
        int randomPos = random.nextInt(serverList.size());

        String server = serverList.get(randomPos);
        return map.get(server);
    }
}
