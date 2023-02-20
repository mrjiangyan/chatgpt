package com.touchbiz.chatgpt.dto.request;

import lombok.Data;

/**
 * @author jiangyan
 */
@Data
public class ValidChatRight {

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 客户端id
     */
    private String clientId;

}
