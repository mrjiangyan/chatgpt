//package com.touchbiz.chatgpt.boot.configuration;
//
//import com.touchbiz.chatgpt.boot.config.WechatAccountConfig;
//import me.chanjar.weixin.mp.api.WxMpService;
//import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
//import me.chanjar.weixin.mp.config.WxMpConfigStorage;
//import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class WechatMpConfiguration {
//
//    @Autowired
//    private WechatAccountConfig config;
//
//    @Bean
//    public WxMpService wxMpService(){
//        WxMpService wxMpService = new WxMpServiceImpl();
//        wxMpService.setWxMpConfigStorage(wxMpConfigStorage());
//        return wxMpService;
//    }
//
//    @Bean
//    public WxMpConfigStorage wxMpConfigStorage(){
//        WxMpDefaultConfigImpl wxMpDefaultConfig = new WxMpDefaultConfigImpl();
//        wxMpDefaultConfig.setAppId(config.getAppId());
//        wxMpDefaultConfig.setSecret(config.getAppSecret());
//        return wxMpDefaultConfig;
//    }
//}
