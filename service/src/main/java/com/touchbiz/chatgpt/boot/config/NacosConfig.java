package com.touchbiz.chatgpt.boot.config;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.cloud.nacos.NacosConfigAutoConfiguration;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author scott
 * @date 2020/05/26
 * 路由配置信息
 */
@Configuration
@RefreshScope
@ConditionalOnBean(NacosConfigAutoConfiguration.class)
public class NacosConfig {

    public String serverAddr;
    public String namespace;
    public String dataId;
    public String dataGroup;
    public String username;
    public String password;

    @Value("${spring.cloud.nacos.discovery.server-addr}")
    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    @Value("${spring.cloud.nacos.discovery.namespace:#{null}}")
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Value("${spring.cloud.nacos.config.group:DEFAULT_GROUP}")
    public void setDataGroup(String dataGroup) {
        this.dataGroup = dataGroup;
    }

    public String getServerAddr() {
        return serverAddr;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getDataId() {
        return dataId;
    }

    public String getDataGroup() {
        return dataGroup;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Bean
    public ConfigService createConfigService(NacosConfig config) {
        try {
            Properties properties = new Properties();
            properties.setProperty("serverAddr", config.getServerAddr());
            if(StringUtils.isNotBlank(config.getNamespace())){
                properties.setProperty("namespace", config.getNamespace());
            }
            if(StringUtils.isNotBlank(config.getUsername())){
                properties.setProperty("username", config.getUsername());
            }
            if(StringUtils.isNotBlank(config.getPassword())){
                properties.setProperty("password", config.getPassword());
            }
            return NacosFactory.createConfigService(properties);
        } catch (Exception e) {
            return null;
        }
    }
}
