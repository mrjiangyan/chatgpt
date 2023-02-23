package com.touchbiz.chatgpt.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserDTO extends LoginUser {

    private String memberLevel;

    private Integer sessionCount;
}
