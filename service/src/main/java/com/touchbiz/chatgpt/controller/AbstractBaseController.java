package com.touchbiz.chatgpt.controller;


import com.baomidou.mybatisplus.extension.service.IService;
import com.touchbiz.cache.starter.IRedisTemplate;
import com.touchbiz.chatgpt.infrastructure.constants.CommonConstant;
import com.touchbiz.chatgpt.service.ISysUserService;
import com.touchbiz.common.entity.model.SysUserCacheInfo;
import com.touchbiz.common.utils.tools.JsonUtils;
import com.touchbiz.webflux.starter.filter.ReactiveRequestContextHolder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

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

    protected SysUserCacheInfo getCurrentUser(){
        var user = ReactiveRequestContextHolder.getUser();
        if(user == null){
            var request = ReactiveRequestContextHolder.get();
            String token = request.getHeaders().getFirst(X_ACCESS_TOKEN);
            String redisKey = CommonConstant.SYS_USERS_CACHE + token;
            String json = (String) redisTemplate.get(redisKey);
            if(json == null){
                return null;
            }
            log.info("key:{},json:{}", redisKey, json);
            return  JsonUtils.toObject(json, SysUserCacheInfo.class);
        }
        return (SysUserCacheInfo) user;
    }



}
