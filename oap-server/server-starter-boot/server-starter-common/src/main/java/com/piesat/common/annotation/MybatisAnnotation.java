package com.piesat.common.annotation;

import java.lang.annotation.*;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2020-03-10 13:29
 **/
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MybatisAnnotation {
    String value();
}

