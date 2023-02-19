package com.touchbiz.chatgpt.infrastructure.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @Author scott
 * @Date 2019/9/23 14:12
 * @Description: 编程校验token有效性
 */
@Slf4j
public class TokenUtils {

    public static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;

    public static String sign(String username, String secret) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        // 附带username信息
        return JWT.create().withClaim("username", username).withExpiresAt(date).sign(algorithm);

    }
    /**
     * 获取 request 里传递的 token
     *
     * @param request
     * @return
     */

}
