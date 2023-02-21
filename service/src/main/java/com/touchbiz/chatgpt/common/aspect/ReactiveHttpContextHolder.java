package com.touchbiz.chatgpt.common.aspect;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class ReactiveHttpContextHolder {

    //获取当前请求对象
    public static Mono<ServerHttpRequest> getRequest() {
        return Mono.subscriberContext()
                .map(context -> context.get(Info.CONTEXT_KEY).getRequest());
    }

    //获取当前response
    public static Mono<ServerHttpResponse> getResponse() {
        return Mono.subscriberContext()
                .map(context -> context.get(Info.CONTEXT_KEY).getResponse());
    }

    public static final class Info {
        public static final Class<ServerWebExchange> CONTEXT_KEY = ServerWebExchange.class;
    }
}
