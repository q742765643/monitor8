package com.piesat.skywalking;

import com.piesat.common.grpc.annotation.GrpcServiceScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.piesat.*"})
@GrpcServiceScan(packages = {"com.piesat"})
public class ScheduleApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(ScheduleApplication.class, args);

    }
}
