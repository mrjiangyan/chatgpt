package com.touchbiz.chatgpt.database.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.touchbiz.chatgpt.common.dto.LogDTO;
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
    @InterceptorIgnore(illegalSql = "true", tenantLine = "true")
    void saveLog(@Param("dto") LogDTO dto);

}
