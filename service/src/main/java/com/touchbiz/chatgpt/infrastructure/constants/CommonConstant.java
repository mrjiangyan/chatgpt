package com.touchbiz.chatgpt.infrastructure.constants;

/**
 * @Description: 通用常量
 * @author: jeecg-boot
 */
public interface CommonConstant {



	/**
	 * 系统日志类型： 登录
	 */
	int LOG_TYPE_1 = 1;

    /**
     * 接口服务 sign
     */
    String TIMESTAMP="timeStamp";

    String APPID = "appid";

    String SIGN = "sign";

    String SYS_USERS_CACHE = "sys:cache:encrypt:user";

    String TOKEN_PREFIX = "Bearer ";

    String SPLICER = "\n\n";
}
