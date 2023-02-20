package com.touchbiz.chatgpt.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.touchbiz.chatgpt.common.dto.LogDTO;
import com.touchbiz.chatgpt.database.domain.SysUser;
import com.touchbiz.chatgpt.database.mapper.BaseCommonMapper;
import com.touchbiz.chatgpt.dto.response.LoginUser;
import com.touchbiz.chatgpt.infrastructure.converter.UserConverter;
import com.touchbiz.chatgpt.infrastructure.utils.IpUtils;
import com.touchbiz.chatgpt.infrastructure.utils.SpringContextUtils;
import com.touchbiz.chatgpt.service.BaseCommonService;
import com.touchbiz.webflux.starter.filter.ReactiveRequestContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @Description: common实现类
 * @author: jeecg-boot
 */
@Service
@Slf4j
public class BaseCommonServiceImpl implements BaseCommonService {

    @Resource
    private BaseCommonMapper baseCommonMapper;


    @Override
    public void addLog(String logContent, Integer logType, Integer operateType, LoginUser user) {
        addLog(logContent, logType, operateType, UserConverter.INSTANCE.transformIn(user));
    }

    @Override
    public void addLog(String logContent, Integer logType, Integer operateType, SysUser user) {
        LogDTO sysLog = new LogDTO();
        //注解上的描述,操作日志内容
        sysLog.setLogContent(logContent);
        sysLog.setLogType(logType);
        sysLog.setOperateType(operateType);
        try {
            //获取request
            var request = ReactiveRequestContextHolder.get();
            //设置IP地址
            sysLog.setIp(IpUtils.getIpAddr(request));
            sysLog.setRequestType(request.getMethod().name());
            sysLog.setRequestUrl(request.getPath().toString());
            if(request.getMethod().equals(HttpMethod.POST) || request.getMethod().equals(HttpMethod.PUT)
            || request.getMethod().equals(HttpMethod.DELETE)){
                
            }
        } catch (Exception e) {
            log.error("e:", e);
            sysLog.setIp("127.0.0.1");
        }
        //获取登录用户信息
        if(user!=null){
            sysLog.setCreateBy(String.valueOf(user.getId()));
            sysLog.setUserid(user.getUsername());
            sysLog.setUsername(user.getRealname());
        }
        //保存日志（异常捕获处理，防止数据太大存储失败，导致业务失败）JT-238
        try {
            log.info("sysLog:{}", sysLog);
            baseCommonMapper.saveLog(sysLog);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    @Override
    public void addLog(String logContent, Integer logType, Integer operateType) {
        addLog(logContent, logType, operateType, (SysUser) null);
    }



}
