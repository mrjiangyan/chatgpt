package com.touchbiz.chatgpt.boot.filter;

import cn.hutool.http.HttpStatus;
import com.touchbiz.cache.starter.IRedisTemplate;
import com.touchbiz.chatgpt.common.dto.Result;
import com.touchbiz.common.entity.annotation.Auth;
import com.touchbiz.common.entity.model.SysUserCacheInfo;
import com.touchbiz.common.utils.tools.JsonUtils;
import com.touchbiz.webflux.starter.configuration.HttpHeaderConstants;
import com.touchbiz.webflux.starter.filter.ReactiveRequestContextHolder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;

import static com.touchbiz.common.utils.text.CommonConstant.X_ACCESS_TOKEN;


/**
 * 验证token是否有效
 */


@Slf4j
@Component
@AllArgsConstructor
public class AuthFilter implements WebFilter, Ordered {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    final String SYS_USERS_CACHE = "sys:cache:user";

    private final IRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (request.getMethod() == HttpMethod.OPTIONS || request.getMethod() == HttpMethod.HEAD) {
            return chain.filter(exchange);
        }
        ReactiveRequestContextHolder.put(request);

        HandlerMethod handlerMethod = requestMappingHandlerMapping.getHandlerInternal(exchange).toProcessor().peek();
        if (handlerMethod != null) {
            //检查是否有auth注释，没有则跳过认证
            Method method = handlerMethod.getMethod();
            if (!method.isAnnotationPresent(Auth.class)) {
                return chain.filter(exchange);
            }
        } else {
            return chain.filter(exchange);
        }

        String token = request.getHeaders().getFirst(X_ACCESS_TOKEN);
        if (StringUtils.isEmpty(token)) {
            var cookie = request.getCookies().getFirst(HttpHeaderConstants.HEADER_TOKEN);
            if (cookie != null) {
                token = cookie.getValue();
            }
            if (StringUtils.isEmpty(token)) {
                 return outputMessage(exchange.getResponse(), "token is not exist");
            }
        }

        Auth auth = handlerMethod.getMethodAnnotation(Auth.class);
        assert auth != null;
        try {
            String redisKey = SYS_USERS_CACHE + token;
            String json = (String) redisTemplate.get(redisKey);
            log.info("key:{},json:{}", redisKey, json);
            SysUserCacheInfo user =  JsonUtils.toObject(json, SysUserCacheInfo.class);
            //查找该用户
            exchange.getAttributes().put(HttpHeaderConstants.HEADER_USER, user);
            ReactiveRequestContextHolder.putUser(user);
        } catch (Exception ex) {
            log.error("", ex);
            return outputMessage(exchange.getResponse(), ex.getMessage());
        }
        return chain.filter(exchange);
    }

    private Mono<Void> outputMessage(ServerHttpResponse response, String source) {
        response.getHeaders().put(HttpHeaders.CONTENT_TYPE, Collections.singletonList(MediaType.APPLICATION_JSON_VALUE));
        response.getHeaders().put(HttpHeaders.VARY, Arrays.asList("Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        response.getHeaders().put("source", Collections.singletonList(source));
        return response.writeAndFlushWith(Flux.just(ByteBufFlux.just(response.bufferFactory().wrap(JsonUtils.toJson(Result.error(HttpStatus.HTTP_FORBIDDEN, source)).getBytes()))));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
