package com.touchbiz.chatgpt.boot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * 加载项目配置
 * @author: jeecg-boot
 */
@Component
@Data
@ConfigurationProperties(prefix = "jeecg")
public class JeecgBaseConfig {
    /**
     * 签名密钥串(字典等敏感接口)
     * @TODO 降低使用成本加的默认值,实际以 yml配置 为准
     */
    private String signatureSecret = "dd05f1c54d63749eda95f9fa6d49v442a";
    /**
     * 需要加强校验的接口清单
     */
    private String signUrls;

    /**
     * 是否启用安全模式
     */
    private Boolean safeMode = false;

}
