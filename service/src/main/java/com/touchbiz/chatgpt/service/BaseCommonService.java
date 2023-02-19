package com.touchbiz.chatgpt.service;

import com.touchbiz.chatgpt.database.domain.SysUser;
import com.touchbiz.chatgpt.dto.response.LoginUser;

/**
 * common接口
 * @author: jeecg-boot
 */
public interface BaseCommonService {

    /**
     * 保存日志
     * @param logContent
     * @param logType
     * @param operateType
     * @param user
     */
    void addLog(String logContent, Integer logType, Integer operateType, LoginUser user);

    /**
     * 保存日志
     * @param logContent
     * @param logType
     * @param operateType
     */
    void addLog(String logContent, Integer logType, Integer operateType);

}
