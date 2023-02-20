package com.touchbiz.chatgpt.controller;

import com.touchbiz.chatgpt.boot.config.WechatAccountConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;

@RestController
@RequestMapping("api/openai/wechat/")
@Slf4j
public class WechatController {

    private final WxMpService wxMpService;

    private final WechatAccountConfig config;

    public WechatController(WxMpService wxMpService, WechatAccountConfig config) {
        this.wxMpService = wxMpService;
        this.config = config;
    }

    @SneakyThrows
    @GetMapping("/authorize")
    public Mono<String> authorize(@RequestParam("returnUrl") String returnUrl){
        String url = config.getMpReturnUrl();
        var encode = URLEncoder.encode(returnUrl, "utf-8");
        var redirectUrl =  wxMpService.getOAuth2Service()
                .buildAuthorizationUrl(url, WxConsts.OAuth2Scope.SNSAPI_BASE, encode);
        log.info("[微信网页登录],redirectUrl={}", redirectUrl);
        return Mono.just(redirectUrl);
    }

//    @GetMapping("/userInfo")
//    public Mono<String> userInfo(@RequestParam("code") String code,
//                                 @RequestParam("sstate") String returnUrl){
//        WxMPO
//    }
}
