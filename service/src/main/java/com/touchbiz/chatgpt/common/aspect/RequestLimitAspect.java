package com.touchbiz.chatgpt.common.aspect;

import com.touchbiz.chatgpt.common.aspect.annotation.RequestLimit;
import com.touchbiz.chatgpt.common.exception.RequestLimitException;
import com.touchbiz.common.entity.exception.BizException;
import com.touchbiz.common.utils.text.CommonConstant;
import com.touchbiz.webflux.starter.filter.ReactiveRequestContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.util.List;

/**
 * 控制接口访问次数限制
 */

@Slf4j
@Aspect
@Component
public class RequestLimitAspect {

    private static final String REQUEST_LIMIT_KEY = "request-limit";

    @Autowired
    private RedisTemplate redisTemplate;

    @Pointcut("@annotation(com.touchbiz.chatgpt.common.aspect.annotation.RequestLimit)")
    private void requestLimitRule() {
    }

    @Around("@annotation(requestLimit)")
    public Object requestLimit(ProceedingJoinPoint joinPoint, RequestLimit requestLimit) throws RequestLimitException {
        var request = ReactiveRequestContextHolder.get();
        String hostAddress = getIpAddr(request);
        log.info("hostAddress={}", hostAddress);
        String url = request.getURI().toString();
        log.info("url={}", url);
        String redisKey = String.format("%s::%s:%s", REQUEST_LIMIT_KEY, hostAddress, url);
        long count = redisTemplate.opsForValue().increment(redisKey, 1);
        if (count == 1) {
            redisTemplate.persist(redisKey);
        }
        if (count > requestLimit.maxCount()) {
            String error = String.format("HTTP请求【%s】超过了限定的访问次数【%s】", url, requestLimit.maxCount());
            log.error(error);
            throw new RequestLimitException(error);
        }
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            log.error("接口方法出现异常-->{}", e);
            throw new RequestLimitException(e.toString());
        }
    }

    /**
     * 获取IP地址
     * <p>
     * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
     */
    public static String getIpAddr(ServerHttpRequest request) {
        String ip = null;
        try {
            List<String> heads = request.getHeaders().get("x-forwarded-for");
            if (ObjectUtils.isEmpty(heads) || CommonConstant.UNKNOWN.equalsIgnoreCase(heads.toString())) {
                heads = request.getHeaders().get("Proxy-Client-IP");
            }
            if (ObjectUtils.isEmpty(heads) || heads.size() == 0 || CommonConstant.UNKNOWN.equalsIgnoreCase(heads.toString())) {
                heads = request.getHeaders().get("WL-Proxy-Client-IP");
            }
            if (ObjectUtils.isEmpty(heads) || CommonConstant.UNKNOWN.equalsIgnoreCase(heads.toString())) {
                heads = request.getHeaders().get("HTTP_CLIENT_IP");
            }
            if (ObjectUtils.isEmpty(heads) || CommonConstant.UNKNOWN.equalsIgnoreCase(heads.toString())) {
                heads = request.getHeaders().get("HTTP_X_FORWARDED_FOR");
            }
            if (ObjectUtils.isEmpty(heads) || CommonConstant.UNKNOWN.equalsIgnoreCase(heads.toString())) {
                ip = request.getRemoteAddress().getHostString();
                if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (Exception e) {
                        log.error("根据网卡获取本机配置的IP异常=>", e.getMessage());
                    }
                    if (inet.getHostAddress() != null) {
                        ip = inet.getHostAddress();
                    }
                }
            } else {
                ip = heads.get(0);
            }
        } catch (Exception e) {
            log.error("RequestLimitAspect:getIpAddr ERROR ", e);
            throw new BizException("请求异常");
        }

//        //使用代理，则获取第一个IP地址
//        if(StringUtils.isEmpty(ip) && ip.length() > 15) {
//			if(ip.indexOf(",") > 0) {
//				ip = ip.substring(0, ip.indexOf(","));
//			}
//		}

        return ip;
    }

}
