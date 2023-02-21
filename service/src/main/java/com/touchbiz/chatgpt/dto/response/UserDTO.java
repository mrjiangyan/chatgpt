package com.touchbiz.chatgpt.dto.response;

import com.touchbiz.common.entity.model.SysUserCacheInfo;
import lombok.Data;

@Data
public class UserDTO extends SysUserCacheInfo {

    private String memberLevel;

    private Integer sessionCount;
}
