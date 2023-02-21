package com.touchbiz.chatgpt.controller;


import com.baomidou.mybatisplus.extension.service.IService;
import com.touchbiz.cache.starter.IRedisTemplate;
import com.touchbiz.chatgpt.dto.response.LoginUser;
import com.touchbiz.chatgpt.infrastructure.constants.CommonConstant;
import com.touchbiz.chatgpt.service.ISysUserService;
import com.touchbiz.webflux.starter.filter.ReactiveRequestContextHolder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import static com.touchbiz.chatgpt.infrastructure.constants.CommonConstant.TOKEN_PREFIX;
import static com.touchbiz.common.utils.text.CommonConstant.X_ACCESS_TOKEN;

/**
 * @Description: Controller基类
 * @Date: 2019-4-21 8:13
 * @Version: 1.0
 */
@Slf4j
public abstract class AbstractBaseController<T, S extends IService<T>> {

    @Getter
    @Autowired
    protected S service;

    @Autowired
    protected ISysUserService sysUserService;

    @Getter
    @Autowired
    protected IRedisTemplate redisTemplate;

//    protected SysUserCacheInfo getCurrentUser(){
//       return (SysUserCacheInfo) ReactiveRequestContextHolder.getUser();
//    }

    protected LoginUser getCurrentUser(){
        var user = ReactiveRequestContextHolder.getUser();
        if(user == null){
            var request = ReactiveRequestContextHolder.get();
            String token = request.getHeaders().getFirst(X_ACCESS_TOKEN);
            if(token != null && token.startsWith(TOKEN_PREFIX)){
                token = token.replaceFirst(TOKEN_PREFIX, "");
            }
            String redisKey = CommonConstant.SYS_USERS_CACHE + token;
            log.info("redisKey:{}", redisKey);
            return redisTemplate.getObject(redisKey, LoginUser.class);
        }
        return (LoginUser) user;
    }



}
