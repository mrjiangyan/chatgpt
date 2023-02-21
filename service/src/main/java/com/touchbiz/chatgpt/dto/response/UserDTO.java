package com.touchbiz.chatgpt.dto.response;

import lombok.Data;

@Data
public class UserDTO extends LoginUser {

    private String memberLevel;

    private Integer sessionCount;
}
