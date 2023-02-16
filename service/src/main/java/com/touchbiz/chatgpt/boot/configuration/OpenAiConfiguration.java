package com.touchbiz.chatgpt.boot.configuration;

import com.touchbiz.chatgpt.boot.config.OpenAiConfig;
import com.touchbiz.chatgpt.common.proxy.OpenAiEventStreamService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OpenAiConfig.class )
public class OpenAiConfiguration {

    @Bean
    public OpenAiEventStreamService getOpenAiService(OpenAiConfig config){
        return new OpenAiEventStreamService(config);
    }
}
