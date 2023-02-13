package com.touchbiz.chatgpt.boot.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * 优雅的http请求方式RestTemplate
 *
 * @author: jeecg-boot
 * @Return:
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(30))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();
//        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
//
//        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
//        stringHttpMessageConverter.setWriteAcceptCharset(true);
//
//        List<MediaType> mediaTypeList = new ArrayList<>();
//        mediaTypeList.add(MediaType.ALL);
//
//        for (int i = 0; i < restTemplate.getMessageConverters().size(); i++) {
//            if (restTemplate.getMessageConverters().get(i) instanceof StringHttpMessageConverter) {
//                restTemplate.getMessageConverters().remove(i);
//                restTemplate.getMessageConverters().add(i, stringHttpMessageConverter);
//            }
//            if (restTemplate.getMessageConverters().get(i) instanceof MappingJackson2HttpMessageConverter) {
//                try {
//                    ((MappingJackson2HttpMessageConverter) restTemplate.getMessageConverters().get(i)).setSupportedMediaTypes(mediaTypeList);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        return restTemplate;
    }

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
