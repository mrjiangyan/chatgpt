package com.touchbiz.chatgpt.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 登录表单
 *
 * @Author scott
 * @since  2019-01-18
 */
@ApiModel(value="登录对象", description="登录对象")
@Data
public class SysLoginModel {
    @NotNull(message = "请输入账号")
	@ApiModelProperty(value = "账号")
    private String username;

    @NotNull(message = "请输入密码")
	@ApiModelProperty(value = "密码")
    private String password;

    @NotNull(message = "请输入验证码")
	@ApiModelProperty(value = "验证码")
    private String captcha;
	@ApiModelProperty(value = "验证码key")
    private String checkKey;
    
}