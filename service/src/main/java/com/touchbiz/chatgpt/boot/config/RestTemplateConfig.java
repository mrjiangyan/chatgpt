package com.touchbiz.chatgpt.boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

/**
 * 优雅的http请求方式RestTemplate
 *
 * @author: jeecg-boot
 * @Return:
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        //ms毫秒
        factory.setReadTimeout(5000);
        //ms毫秒
        factory.setConnectTimeout(15000);
        return factory;
    }
}
