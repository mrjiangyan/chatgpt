package com.touchbiz.chatgpt.application;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.touchbiz.chatgpt.database.domain.ChatSession;
import com.touchbiz.chatgpt.dto.Chat;
import com.touchbiz.chatgpt.dto.ChatResult;
import com.touchbiz.chatgpt.dto.response.LoginUser;

/**
 * <p>
 * chat会话
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
public interface ChatApplicationService {

	IPage<ChatSession> getChatSessionPageList(Integer pageNo, Integer pageSize, LoginUser sysUser);

	ChatSession createSession(LoginUser sysUser);

	void createSessionInfo(Chat chat, String result, LoginUser sysUser);

	void deleteSession(String id, LoginUser user);

	void checkSessionId(String sessionId);
}
