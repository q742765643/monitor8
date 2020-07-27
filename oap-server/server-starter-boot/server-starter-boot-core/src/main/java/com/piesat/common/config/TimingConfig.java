package com.piesat.common.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.piesat.common.grpc.config.ChannelUtil;
import com.piesat.sso.client.util.RedisUtil;
import com.piesat.util.NetUtils;
import com.piesat.util.StringUtil;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Component
public class TimingConfig implements ApplicationRunner {
    @Autowired
    private RedisUtil redisUtil;
    @Value("${spring.application.name}")
    private String name;
    @Autowired
    private GrpcProperties grpcProperties;

    /**private ExecutorService  executorService= new ThreadPoolExecutor(5, 5,
     0L, TimeUnit.MILLISECONDS,
     new LinkedBlockingQueue<Runnable>(5000), new ThreadFactoryBuilder().setNameFormat("heart-log-%d").build(), new ThreadPoolExecutor.AbortPolicy());*/


    private static ScheduledExecutorService timingPool;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        Map<String,String> server=grpcProperties.getServer();
        Map<String,Map<String,Object>> client=grpcProperties.getClient();
        String host=server.get("host");
        String grpcPort=server.get("port");
        if(StringUtil.isEmpty(host)){
            host= NetUtils.getLocalHost();
        }
        String grpcHost=host;
        ThreadFactory timingPoolFactory = new ThreadFactoryBuilder().setNameFormat("grpc-worker-timing-pool-%d").build();
        ChannelUtil channelUtil= ChannelUtil.getInstance();
        timingPool = Executors.newScheduledThreadPool(2, timingPoolFactory);
        timingPool.scheduleWithFixedDelay (()->{
            try {
                redisUtil.hset("GRPC.SERVER:"+name,grpcHost+":"+grpcPort,1);
                for (String key : client.keySet()) {
                    Map<Object,Object> addresss=redisUtil.hmget("GRPC.SERVER:"+key);
                    addresss.forEach((a,b)->{
                        String address=String.valueOf(a);
                        ConcurrentHashMap<String, ManagedChannel>  channelMap=channelUtil.getChannel().get(key);
                        if(channelMap==null){
                            ManagedChannel channel = ManagedChannelBuilder.forTarget("static://"+address).usePlaintext().build();
                            ConcurrentHashMap<String, ManagedChannel> newchanelMap=new ConcurrentHashMap<>();
                            newchanelMap.put(address,channel);
                            String status="";
                            try {
                                HealthCheckRequest request= HealthCheckRequest.newBuilder().build();
                                HealthGrpc.HealthBlockingStub stub=HealthGrpc.newBlockingStub(channel);
                                HealthCheckResponse response=stub.check(request);
                                status=response.getStatus().getValueDescriptor().toString();
                            } catch (Exception e) {
                                log.info("grpc {} 心跳检测失败",address);
                            }finally {
                                if(status.equals("SERVING")) {
                                    channelUtil.getChannel().put(key, newchanelMap);
                                }
                            }
                        }
                        if(channelMap!=null&&channelMap.get(address)==null){
                            ManagedChannel channel = ManagedChannelBuilder.forTarget("static://"+address).usePlaintext().build();
                            String status="";
                            try {
                                HealthCheckRequest request= HealthCheckRequest.newBuilder().build();
                                HealthGrpc.HealthBlockingStub stub=HealthGrpc.newBlockingStub(channel);
                                HealthCheckResponse response=stub.check(request);
                                status=response.getStatus().getValueDescriptor().toString();
                            } catch (Exception e) {
                                log.info("grpc {} 心跳检测失败",address);
                            }finally {
                                if(status.equals("SERVING")){
                                    channelUtil.getChannel().get(key).put(address,channel);

                                }
                            }
                        }

                    });

                }
            } catch (Exception e) {
                log.info("grpc服务发现定时线程异常");
            }

        }, 0, 30, TimeUnit.SECONDS);
        timingPool.scheduleWithFixedDelay(()->{
            try {
                ConcurrentHashMap<String, ConcurrentHashMap<String,ManagedChannel>>  channelMap=channelUtil.getChannel();
                channelMap.forEach((k,v)->{
                    v.forEach((a,b)->{
                        String status="";
                        try {
                            HealthCheckRequest request= HealthCheckRequest.newBuilder().build();
                            HealthGrpc.HealthBlockingStub stub=HealthGrpc.newBlockingStub(b);
                            HealthCheckResponse response=stub.check(request);
                            status=response.getStatus().getValueDescriptor().toString();
                        } catch (Exception e) {
                            log.info("grpc {} 心跳检测失败",a);
                        }finally {
                            if(!status.equals("SERVING")){
                                if(null!=channelUtil.getChannel().get(k)){
                                    channelUtil.getChannel().get(k).get(a).shutdown();
                                }
                                channelUtil.getChannel().get(k).remove(a);
                                redisUtil.hdel("GRPC.SERVER:"+name,k);
                           /*     executorService.execute(
                                        ()->{
                                            boolean flag=false;
                                            int i=0;
                                            while (i<5){
                                                i++;
                                                try {
                                                    HealthCheckRequest request= HealthCheckRequest.newBuilder().build();
                                                    HealthGrpc.HealthBlockingStub stub=HealthGrpc.newBlockingStub(b);
                                                    HealthCheckResponse response=stub.check(request);
                                                    String check=response.getStatus().getValueDescriptor().toString();
                                                    if(check.equals("SERVING")){
                                                        flag=true;
                                                        break;
                                                    }
                                                    Thread.sleep(500);
                                                } catch (Exception e) {
                                                    log.info("grpc {} 心跳检测失败",a);
                                                }

                                            }
                                            if(!flag){
                                                redisUtil.hdel("GRPC.SERVER:"+name,k);
                                            }
                                        }
                                );*/
                            }
                        }

                    });
                });
            } catch (Exception e) {
                log.info("grpc心跳检测定时线程异常");
            }


        }, 0, 60, TimeUnit.SECONDS);
    }
}
