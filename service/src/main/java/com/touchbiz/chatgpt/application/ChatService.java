package com.touchbiz.chatgpt.application;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.touchbiz.chatgpt.database.domain.SysUser;
import com.touchbiz.chatgpt.dto.ChatInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * chat会话
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
public interface ChatService {

	IPage<ChatInfo> getPageList(Integer pageNo, Integer pageSize, SysUser sysUser);

	String addSessionId(SysUser sysUser, HttpServletRequest request);

	void add(ChatInfo chatInfo, SysUser sysUser);

	void delete(String id);
}
