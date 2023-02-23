package com.touchbiz.chatgpt.boot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.time.Duration;

@Data
@RefreshScope
@ConfigurationProperties(prefix = "openai")
public class OpenAiConfig {

    private String key;

    private Duration timeout = Duration.ofSeconds(10);

    private String model;
}
