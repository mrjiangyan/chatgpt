package com.touchbiz.chatgpt.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.touchbiz.common.entity.annotation.SensitiveField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 在线用户信息
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LoginUser {

	/**
	 * 登录人id
	 */
	@SensitiveField
	private String id;

	/**
	 * 登录人账号
	 */
	@SensitiveField
	private String username;

	/**
	 * 登录人名字
	 */
	@SensitiveField
	private String realname;

	/**
	 * 登录人密码
	 */
	@SensitiveField
	private String password;

	/**
	 * 头像
	 */
	@SensitiveField
	private String avatar;

	/**
	 * 生日
	 */
	@SensitiveField
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date birthday;

	/**
	 * 电话
	 */
	@SensitiveField
	private String phone;

	/**
	 * 状态(1：正常 2：冻结 ）
	 */
	private Integer status;

}
