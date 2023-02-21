package com.touchbiz.chatgpt.database.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * chat会话信息
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("chat_session_info")
public class ChatSessionDetail extends BaseDomain {

    /**
     * 会话id
     */
    @ApiModelProperty("会话id")
    private String sessionId;

    @ApiModelProperty("类型(1.问题2.回答)")
    private Integer type;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("顺序")
    private Long sequence;

}
