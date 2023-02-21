package com.touchbiz.chatgpt.application;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.touchbiz.chatgpt.database.domain.ChatSession;
import com.touchbiz.chatgpt.dto.ChatInfo;
import com.touchbiz.common.entity.model.SysUserCacheInfo;

/**
 * <p>
 * chat会话
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
public interface ChatApplicationService {

	IPage<ChatInfo> getPageList(Integer pageNo, Integer pageSize, SysUserCacheInfo sysUser);

	ChatSession createSession(SysUserCacheInfo sysUser);

	void add(ChatInfo chatInfo, SysUserCacheInfo sysUser);

	void deleteSession(String id, SysUserCacheInfo user);
}
