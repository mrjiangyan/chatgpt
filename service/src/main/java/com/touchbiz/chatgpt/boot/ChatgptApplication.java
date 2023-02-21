package com.touchbiz.chatgpt.boot;

import com.touchbiz.common.utils.text.oConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.reactive.config.EnableWebFlux;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication(
        scanBasePackages = {
                "com.touchbiz.chatgpt",
                "com.touchbiz.webflux.starter",
                "com.touchbiz.cache.starter",
                "com.touchbiz.db.starter",
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
@Slf4j
public class ChatgptApplication {

    public static void main(String[] args) throws UnknownHostException {
        var application = SpringApplication.run(ChatgptApplication.class, args);
        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        String path = oConvertUtils.getString(env.getProperty("server.servlet.context-path"));
        log.info("\n----------------------------------------------------------\n\t" +
                "Application Chat is running! Access URLs:\n\t" +
                "Local: \t\thttp://localhost:" + port + path + "/\n\t" +
                "External: \thttp://" + ip + ":" + port + path + "/\n\t" +
                "Swagger文档: \thttp://" + ip + ":" + port + path + "/doc.html\n" +
                "----------------------------------------------------------");
    }
}
