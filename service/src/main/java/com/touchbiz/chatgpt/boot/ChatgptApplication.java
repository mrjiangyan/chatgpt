package com.touchbiz.chatgpt.boot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.reactive.config.EnableWebFlux;

import java.util.TimeZone;

@SpringBootApplication(
        scanBasePackages = {
                "com.touchbiz.chatgpt",
                "com.touchbiz.webflux.starter",
                "com.touchbiz.cache.starter",
//                "com.touchbiz.db.starter",
                "com.touchbiz.config.starter",

        }
)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync
@EnableWebFlux
@EnableDiscoveryClient
//@EnableFeignClients(basePackages = {"com.prime"})
@MapperScan(basePackages ={"com.touchbiz.chatgpt.database.mapper",
     }

)
public class ChatgptApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatgptApplication.class, args);
    }
}
