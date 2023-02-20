package com.touchbiz.chatgpt.database.domain;


import io.swagger.annotations.ApiModelProperty;
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
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ChatSessionInfo extends BaseDomain {

    /**
     * 会话id
     */
    @ApiModelProperty("会话id")
    private String id;

    @ApiModelProperty("类型(1.问题2.回答)")
    private Integer type;

    @ApiModelProperty("内容")
    private String content;

}
