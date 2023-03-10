package com.touchbiz.chatgpt.infrastructure.utils;

import com.touchbiz.common.entity.exception.BizException;
import com.touchbiz.common.utils.text.CommonConstant;
import com.touchbiz.common.utils.text.oConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.List;

/**
 * IP地址
 *
 * @Author scott
 * <p>
 * update: 【类名改了大小写】 date: 2022-04-18
 * @email jeecgos@163.com
 * @Date 2019年01月14日
 */
@Slf4j
public class RequestUtils {
    private static Logger logger = LoggerFactory.getLogger(RequestUtils.class);

    /**
     * 获取IP地址
     * <p>
     * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
     */
    public static String getIpAddr(ServerHttpRequest request) {
        List<String> ip = null;
        try {
            var headers = request.getHeaders();
            ip = headers.get("x-forwarded-for");
            if (CollectionUtils.isEmpty(ip) || CommonConstant.UNKNOWN.equalsIgnoreCase(ip.get(0))) {
                ip = headers.get("Proxy-Client-IP");
            }
            if (CollectionUtils.isEmpty(ip) || CommonConstant.UNKNOWN.equalsIgnoreCase(ip.get(0))) {
                ip = headers.get("WL-Proxy-Client-IP");
            }
            if (CollectionUtils.isEmpty(ip) || CommonConstant.UNKNOWN.equalsIgnoreCase(ip.get(0))) {
                ip = headers.get("HTTP_CLIENT_IP");
            }
            if (CollectionUtils.isEmpty(ip) || CommonConstant.UNKNOWN.equalsIgnoreCase(ip.get(0))) {
                ip = headers.get("HTTP_X_FORWARDED_FOR");
            }
            if (CollectionUtils.isEmpty(ip) || CommonConstant.UNKNOWN.equalsIgnoreCase(ip.get(0))) {
                String hostString = request.getRemoteAddress().getHostString();
                if ("127.0.0.1".equals(hostString) || "0:0:0:0:0:0:0:1".equals(hostString)) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (Exception e) {
                        log.error("根据网卡获取本机配置的IP异常=>{}", e.getMessage());
                    }
                    if (inet.getHostAddress() != null) {
                        hostString = inet.getHostAddress();
                    }
                }
                return hostString;
            }
        } catch (Exception e) {
            logger.error("IPUtils ERROR ", e);
        }
        return ip.get(0);
    }

    public static String getToken(HttpServletRequest request) {
        String token = request.getHeader(CommonConstant.X_ACCESS_TOKEN);
        if (oConvertUtils.isEmpty(token)) {
            throw new BizException("用户token失效，请联系管理员");
        }
        return token;
    }


}
