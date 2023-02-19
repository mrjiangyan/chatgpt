package com.touchbiz.chatgpt.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.touchbiz.chatgpt.common.dto.LogDTO;
import com.touchbiz.chatgpt.database.domain.SysUser;
import com.touchbiz.chatgpt.database.mapper.BaseCommonMapper;
import com.touchbiz.chatgpt.dto.response.LoginUser;
import com.touchbiz.chatgpt.infrastructure.utils.IpUtils;
import com.touchbiz.chatgpt.infrastructure.utils.SpringContextUtils;
import com.touchbiz.chatgpt.service.BaseCommonService;
import lombok.extern.slf4j.Slf4j;
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
    public void addLog(String logContent, Integer logType, Integer operatetype, LoginUser user) {
        LogDTO sysLog = new LogDTO();
        sysLog.setId(String.valueOf(IdWorker.getId()));
        //注解上的描述,操作日志内容
        sysLog.setLogContent(logContent);
        sysLog.setLogType(logType);
        sysLog.setOperateType(operatetype);
        try {
            //获取request
            HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
            //设置IP地址
            sysLog.setIp(IpUtils.getIpAddr(request));
        } catch (Exception e) {
            sysLog.setIp("127.0.0.1");
        }
        //获取登录用户信息
        if(user!=null){
            sysLog.setUserid(user.getUsername());
            sysLog.setUsername(user.getRealname());
        }
        sysLog.setCreateTime(new Date());
        //保存日志（异常捕获处理，防止数据太大存储失败，导致业务失败）JT-238
        try {
            baseCommonMapper.saveLog(sysLog);
        } catch (Exception e) {
            log.warn(" LogContent length : "+sysLog.getLogContent().length());
            log.warn(e.getMessage());
        }
    }

    @Override
    public void addLog(String logContent, Integer logType, Integer operateType) {
        addLog(logContent, logType, operateType, null);
    }



}
