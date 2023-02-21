package com.touchbiz.chatgpt.common.aspect.annotation;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.annotation.*;


/**
 * 统计规定时间段内接口访问次数限制
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Order(Ordered.HIGHEST_PRECEDENCE)
public @interface RequestLimit {

    /**
     * 访问最大次数
     *
     * @return
     */
    int maxCount() default 10;

    /**
     * 访问时间段（默认1分钟）
     *
     * @return
     */
    long timeout() default 60 * 1000;
}
