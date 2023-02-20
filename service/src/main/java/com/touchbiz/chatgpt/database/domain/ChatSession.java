package com.touchbiz.chatgpt.database.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 会话id
     */
    private String sessionId;
    /**
     * ip
     */
    private String ip;
    /**
     * 请求头信息
     */
    private String head;

    /**
     * 会话开始时间
     */
    private LocalDateTime sessionStartTime;

}
