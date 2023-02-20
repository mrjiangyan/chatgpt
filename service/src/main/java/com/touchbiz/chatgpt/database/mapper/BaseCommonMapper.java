package com.touchbiz.chatgpt.database.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.touchbiz.chatgpt.common.dto.LogDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: BaseCommonMapper
 * @author: jeecg-boot
 */
public interface BaseCommonMapper {


    /**
     * 保存日志
     * @param dto
     */
    @Insert(" insert into sys_log (log_type, log_content, method, operate_type, request_url, request_type, request_param, ip, userid, username, cost_time,creator,header)\n" +
            "        values(\n" +
            "            #{dto.logType,jdbcType=INTEGER},\n" +
            "            #{dto.logContent,jdbcType=VARCHAR},\n" +
            "            #{dto.method,jdbcType=VARCHAR},\n" +
            "            #{dto.operateType,jdbcType=INTEGER},\n" +
            "            #{dto.requestUrl,jdbcType=VARCHAR},\n" +
            "            #{dto.requestType,jdbcType=VARCHAR},\n" +
            "            #{dto.requestParam,jdbcType=VARCHAR},\n" +
            "            #{dto.ip,jdbcType=VARCHAR},\n" +
            "            #{dto.userid,jdbcType=VARCHAR},\n" +
            "            #{dto.username,jdbcType=VARCHAR},\n" +
            "            #{dto.costTime,jdbcType=BIGINT},\n" +
            "            #{dto.createBy,jdbcType=VARCHAR},\n" +
            "            #{dto.header,jdbcType=VARCHAR}\n" +
            "        )")
    @InterceptorIgnore(illegalSql = "true", tenantLine = "true")
    void saveLog(@Param("dto") LogDTO dto);

}
