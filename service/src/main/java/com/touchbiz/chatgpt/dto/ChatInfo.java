package com.touchbiz.chatgpt.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * chat信息
 */
@Data
public class ChatInfo {

    @ApiModelProperty("会话id")
    private String sessionId;

    @ApiModelProperty("会话信息集合")
    private List<ChatSessionInfo> list;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @Data
    public static class ChatSessionInfo {

        @ApiModelProperty("类型(1.问题2.回答)")
        private Integer type;

        @ApiModelProperty("内容")
        private String content;

        @ApiModelProperty("会话时间")
        private LocalDateTime sessionTime;
    }
}
