package com.carroll.monitor.data.collector.annotation;

import java.lang.annotation.*;

/**
 * <p>
 * 业务监控注解
 * @author: carroll
 * @date 2019/9/9
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Monitor {

    String tag();
    String field() default "";
    String target() default "";
    long timeoutMs() default -1;
}
