package com.piesat.common.grpc.annotation;

import com.piesat.common.grpc.constant.SerializeType;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Inherited
@Retention(RUNTIME)
public @interface GrpcHthtService {

    /**
     * 远程服务名
     */
    String server() default "";

    /**
     * 序列化工具实现类
     */
    SerializeType[] serialization() default {};

}