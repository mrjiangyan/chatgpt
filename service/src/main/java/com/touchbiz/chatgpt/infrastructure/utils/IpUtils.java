package com.touchbiz.chatgpt.infrastructure.utils;

import com.touchbiz.common.utils.text.CommonConstant;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * IP地址
 * 
 * @Author scott
 *
 * update: 【类名改了大小写】 date: 2022-04-18
 * @email jeecgos@163.com
 * @Date 2019年01月14日
 */
public class IpUtils {
	private static Logger logger = LoggerFactory.getLogger(IpUtils.class);

	/**
	 * 获取IP地址
	 * 
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
                return  request.getRemoteAddress().toString();
            }
        } catch (Exception e) {
        	logger.error("IPUtils ERROR ", e);
        }
        return ip.get(0);
    }
	
}
