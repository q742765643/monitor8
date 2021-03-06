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
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Component
@Order(10)
public class TimingConfig implements ApplicationRunner {
    /**
     * private ExecutorService  executorService= new ThreadPoolExecutor(5, 5,
     * 0L, TimeUnit.MILLISECONDS,
     * new LinkedBlockingQueue<Runnable>(5000), new ThreadFactoryBuilder().setNameFormat("heart-log-%d").build(), new ThreadPoolExecutor.AbortPolicy());
     */


    private static ScheduledExecutorService timingPool;
    @Autowired
    private RedisUtil redisUtil;
    @Value("${spring.application.name}")
    private String name;
    @Autowired
    private GrpcProperties grpcProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Map<String, String> server = grpcProperties.getServer();
        Map<String, Map<String, Object>> client = grpcProperties.getClient();
        //Map<String, String> hosts = grpcProperties.getHosts();
        ThreadFactory timingPoolFactory = new ThreadFactoryBuilder().setNameFormat("grpc-worker-timing-pool-%d").build();
        ChannelUtil channelUtil = ChannelUtil.getInstance();
        timingPool = Executors.newScheduledThreadPool(3, timingPoolFactory);
        timingPool.scheduleWithFixedDelay(() -> {
            String hosts = server.get("host");
            List<String> grpcHosts=new ArrayList<>();
            if(StringUtil.isNotEmpty(hosts)){
                grpcHosts.addAll(Arrays.asList(hosts.split(",")));
            }
            String grpcPort = server.get("port");
            if (grpcHosts.size()==0) {
                grpcHosts = NetUtils.getLocalIP();
            }
            for (int i = 0; i < grpcHosts.size(); i++) {
                redisUtil.hset("GRPC.SERVER:" + name, grpcHosts.get(i) + ":" + grpcPort, 1);
            }

        }, 0, 60, TimeUnit.SECONDS);
        timingPool.scheduleWithFixedDelay(() -> {
            log.info("==执行服务发现线程==");
            try {
                for (String key : client.keySet()) {
                    Map<Object, Object> addresss = redisUtil.hmget("GRPC.SERVER:" + key);
                    addresss.forEach((a, b) -> {
                        String address = String.valueOf(a);
                        ConcurrentHashMap<String, ManagedChannel> channelMap = channelUtil.getChannel().get(key);
                        if (channelMap == null) {
                            ManagedChannel channel = ManagedChannelBuilder.forTarget("static://" + address).usePlaintext().build();
                            ConcurrentHashMap<String, ManagedChannel> newchanelMap = new ConcurrentHashMap<>();
                            newchanelMap.put(address, channel);
                            String status = "";
                            try {
                                HealthCheckRequest request = HealthCheckRequest.newBuilder().build();
                                HealthGrpc.HealthBlockingStub stub = HealthGrpc.newBlockingStub(channel);
                                HealthCheckResponse response = stub.withDeadlineAfter(5, TimeUnit.SECONDS).check(request);
                                status = response.getStatus().getValueDescriptor().toString();
                            } catch (Exception e) {
                                redisUtil.hdel("GRPC.SERVER:" + key, address);
                                log.info("grpc {} 心跳检测失败", address);
                            } finally {
                                if (status.equals("SERVING")) {
                                    channelUtil.getChannel().put(key, newchanelMap);
                                }
                            }
                        }
                        if (channelMap != null && channelMap.get(address) == null) {
                            ManagedChannel channel = ManagedChannelBuilder.forTarget("static://" + address).usePlaintext().build();
                            String status = "";
                            try {
                                HealthCheckRequest request = HealthCheckRequest.newBuilder().build();
                                HealthGrpc.HealthBlockingStub stub = HealthGrpc.newBlockingStub(channel);
                                HealthCheckResponse response = stub.withDeadlineAfter(5, TimeUnit.SECONDS).check(request);
                                status = response.getStatus().getValueDescriptor().toString();
                            } catch (Exception e) {
                                redisUtil.hdel("GRPC.SERVER:" + key, address);
                                log.info("grpc {} 心跳检测失败", address);
                            } finally {
                                if (status.equals("SERVING")) {
                                    channelUtil.getChannel().get(key).put(address, channel);
                                }
                            }
                        }

                    });

                }
            } catch (Exception e) {
                log.info("grpc服务发现定时线程异常");
            }

        }, 0, 30, TimeUnit.SECONDS);
        timingPool.scheduleWithFixedDelay(() -> {
            log.info("==执行心跳监测线程==");
            try {
                ConcurrentHashMap<String, ConcurrentHashMap<String, ManagedChannel>> channelMap = channelUtil.getChannel();
                channelMap.forEach((k, v) -> {
                    v.forEach((a, b) -> {
                        String status = "";
                        try {
                            HealthCheckRequest request = HealthCheckRequest.newBuilder().build();
                            HealthGrpc.HealthBlockingStub stub = HealthGrpc.newBlockingStub(b);
                            HealthCheckResponse response = stub.withDeadlineAfter(5, TimeUnit.SECONDS).check(request);
                            status = response.getStatus().getValueDescriptor().toString();
                        } catch (Exception e) {
                            log.info("grpc {} 心跳检测失败", a);
                        } finally {
                            if (!status.equals("SERVING")) {
                                if (null != channelUtil.getChannel().get(k)) {
                                    try {
                                        channelUtil.getChannel().get(k).get(a).shutdown();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                channelUtil.getChannel().get(k).remove(a);
                                redisUtil.hdel("GRPC.SERVER:" + k, a);
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
