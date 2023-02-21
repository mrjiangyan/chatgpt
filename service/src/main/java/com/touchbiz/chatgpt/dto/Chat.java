package com.touchbiz.chatgpt.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Chat {
    /**
     *  会话id
     */
    @NotBlank(message = "会话id不存在")
    private String sessionId;

    /**
     *  问题
     */
    @NotBlank(message = "请提问")
    private String prompt;
}
