package com.touchbiz.chatgpt.common.aspect;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class RequestLimitFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        //设置当前请求
        return chain.filter(exchange)
                .subscriberContext(context -> context.put(ReactiveHttpContextHolder.Info.CONTEXT_KEY, exchange));
    }
}
