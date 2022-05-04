package com.mine.study.annotation;

import java.lang.annotation.*;

/**
 * 服务器相关信息
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiServer {
    String value() default "";
    String name() default "";
    String description() default "";
    String version() default "";
    String contextPath() default "";
    String basePackage() default "";
}