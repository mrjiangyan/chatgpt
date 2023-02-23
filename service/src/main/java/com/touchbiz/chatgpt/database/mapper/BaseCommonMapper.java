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
    @Insert("""
             insert into sys_log (log_type, log_content, method, operate_type, request_url, request_type, request_param, ip, userid, username, cost_time,creator,header)
                    values(
                        #{dto.logType,jdbcType=INTEGER},
                        #{dto.logContent,jdbcType=VARCHAR},
                        #{dto.method,jdbcType=VARCHAR},
                        #{dto.operateType,jdbcType=INTEGER},
                        #{dto.requestUrl,jdbcType=VARCHAR},
                        #{dto.requestType,jdbcType=VARCHAR},
                        #{dto.requestParam,jdbcType=VARCHAR},
                        #{dto.ip,jdbcType=VARCHAR},
                        #{dto.userid,jdbcType=VARCHAR},
                        #{dto.username,jdbcType=VARCHAR},
                        #{dto.costTime,jdbcType=BIGINT},
                        #{dto.createBy,jdbcType=VARCHAR},
                        #{dto.header,jdbcType=VARCHAR}
                    )\
            """)
    @InterceptorIgnore(illegalSql = "true", tenantLine = "true")
    void saveLog(@Param("dto") LogDTO dto);

}
