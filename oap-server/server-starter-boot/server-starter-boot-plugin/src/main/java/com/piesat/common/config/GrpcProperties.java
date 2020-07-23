package com.piesat.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties("grpc")
public class GrpcProperties {
     private Map<String,String> server;
     private Map<String,Map<String,Object>> client;
}
