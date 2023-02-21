package com.touchbiz.chatgpt.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatSessionDTO {


    /**
     * 会话id
     */
    private String sessionId;

    /**
     * 会话开始时间
     */
    private LocalDateTime startTime;

    private LocalDateTime lastTime;
}
