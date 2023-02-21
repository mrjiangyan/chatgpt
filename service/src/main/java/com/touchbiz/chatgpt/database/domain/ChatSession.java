package com.touchbiz.chatgpt.database.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * <p>
 * chat会话
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ChatSession extends BaseDomain {

    /**
     * 会话id
     */
    private String sessionId;

    private Long userId;
    /**
     * ip
     */
    private String ip;
    /**
     * 会话开始时间
     */
    private LocalDateTime startTime;

    private LocalDateTime lastTime;

    private String subject;

    private Boolean deleted;

}
