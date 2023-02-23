package com.touchbiz.chatgpt.common.aspect;

import com.touchbiz.chatgpt.common.exception.RequestLimitException;
import com.touchbiz.chatgpt.infrastructure.constants.CommonConstant;
import com.touchbiz.chatgpt.infrastructure.utils.RequestUtils;
import com.touchbiz.webflux.starter.filter.ReactiveRequestContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.touchbiz.common.utils.text.CommonConstant.X_ACCESS_TOKEN;

/**
 * 控制接口访问次数限制
 */

@Slf4j
@Aspect
@Component
@RefreshScope
public class RequestLimitAspect {

    private static final String REQUEST_LIMIT_KEY = "request-limit";

    @Value("${request-limit.maxCount:10}")
    private Integer requestLimitMaxCount;

    @Value("${request-limit.timeout:-1}")
    private Long timeout;

    @Autowired
    private RedisTemplate redisTemplate;


    @Pointcut("@annotation(com.touchbiz.chatgpt.common.aspect.annotation.RequestLimit)")
    private void requestLimitRule() {
    }

    @Around("requestLimitRule()")
    public Object requestLimit(ProceedingJoinPoint joinPoint) throws RequestLimitException {
        var request = ReactiveRequestContextHolder.get();
        String token = request.getHeaders().getFirst(X_ACCESS_TOKEN);
        if (ObjectUtils.isNotEmpty(token) && redisTemplate.hasKey(CommonConstant.SYS_USERS_CACHE + token)) {
            return buildProceed(joinPoint);
        }

        String hostAddress = RequestUtils.getIpAddr(request);
        log.info("hostAddress={}", hostAddress);
        String url = request.getURI().toString();
        log.info("url={}", url);
        String redisKey = String.format("%s::%s", REQUEST_LIMIT_KEY, hostAddress);
        long count = redisTemplate.opsForValue().increment(redisKey, 1);
        if (count == 1) {
            if (timeout > 0) {
                redisTemplate.expire(redisKey, timeout, TimeUnit.SECONDS);
            } else {
                redisTemplate.persist(redisKey);
            }
        }
        if (count > requestLimitMaxCount) {
            String error = String.format("HTTP请求【%s】超过了限定的访问次数【%s】", url, requestLimitMaxCount);
            log.error(error);
            throw new RequestLimitException(error);
        }
        return buildProceed(joinPoint);
    }

    /**
     * 统一通过处理
     *
     * @param joinPoint
     * @return
     */
    private Object buildProceed(ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            log.error("接口方法出现异常-->{}", e);
            throw new RequestLimitException(e.toString());
        }
    }
}
