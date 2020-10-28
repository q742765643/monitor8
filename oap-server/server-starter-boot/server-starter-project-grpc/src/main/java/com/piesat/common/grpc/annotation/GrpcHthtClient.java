package com.piesat.common.grpc.annotation;

import io.grpc.ClientInterceptor;

import java.lang.annotation.*;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/13 15:51
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface GrpcHthtClient {
    String value() default "";

    Class<? extends ClientInterceptor>[] interceptors() default {};

    String[] interceptorNames() default {};

    boolean sortInterceptors() default false;
}
