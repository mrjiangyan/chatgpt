package com.touchbiz.chatgpt.boot.configuration;

import com.theokanning.openai.OpenAiService;
import com.touchbiz.chatgpt.boot.config.OpenAiConfig;
import com.touchbiz.chatgpt.common.proxy.OpenAiEventStreamService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class OpenAiConfiguration {

    @Bean
    public OpenAiEventStreamService getOpenAiService(OpenAiConfig config){
        return new OpenAiEventStreamService(config);
    }
}
