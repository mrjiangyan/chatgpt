package com.touchbiz.chatgpt.boot.configuration;

import com.theokanning.openai.OpenAiService;
import com.touchbiz.chatgpt.boot.config.OpenAiConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class OpenAiConfiguration {

    @Bean
    public OpenAiService getOpenAiService(OpenAiConfig config){
        return new OpenAiService(config.getKey());
    }
}
