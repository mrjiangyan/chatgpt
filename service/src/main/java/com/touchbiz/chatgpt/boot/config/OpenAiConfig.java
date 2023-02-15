package com.touchbiz.chatgpt.boot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "openai")
public class OpenAiConfig {

    private String key;

    private Duration timeout;
}
