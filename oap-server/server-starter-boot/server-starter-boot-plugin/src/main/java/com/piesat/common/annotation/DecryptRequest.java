package com.piesat.common.annotation;

import java.lang.annotation.*;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/14 9:32
 */
@Target({ElementType.METHOD , ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DecryptRequest {
    /**
     * 是否对body进行解密
     */
    boolean value() default true;
}
