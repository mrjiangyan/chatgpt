package com.touchbiz.chatgpt.application.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchbiz.chatgpt.application.ChatApplicationService;
import com.touchbiz.chatgpt.database.domain.ChatSession;
import com.touchbiz.chatgpt.database.domain.ChatSessionDetail;
import com.touchbiz.chatgpt.dto.ChatInfo;
import com.touchbiz.chatgpt.infrastructure.utils.AesEncryptUtil;
import com.touchbiz.chatgpt.infrastructure.utils.RequestUtils;
import com.touchbiz.chatgpt.service.ChatSessionInfoService;
import com.touchbiz.chatgpt.service.ChatSessionService;
import com.touchbiz.common.entity.exception.BizException;
import com.touchbiz.common.entity.model.SysUserCacheInfo;
import com.touchbiz.webflux.starter.filter.ReactiveRequestContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * chat会话
 * </p>
 *
 * @Author: scott
 * @Date: 2018-12-20
 */
@Service
@Slf4j
public class ChatApplicationServiceImpl implements ChatApplicationService {

	@Autowired
	private ChatSessionService chatSessionService;

	@Autowired
	private ChatSessionInfoService chatSessionInfoService;

	@Override
	public IPage<ChatSession> getChatSessionPageList(Integer pageNo, Integer pageSize, SysUserCacheInfo user) {
		Page<ChatSession> page = new Page<>(pageNo, pageSize);
		LambdaQueryWrapper<ChatSession> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(ChatSession::getUserId, user.getId());
		queryWrapper.orderByDesc(ChatSession::getGmtCreate);
		var sessionPage = chatSessionService.page(page, queryWrapper);
		return sessionPage;
	}

	@Override
	public ChatSession createSession(SysUserCacheInfo user) {
		String uuid = IdWorker.getIdStr();
		//获取request
		var requests = ReactiveRequestContextHolder.get();
		ChatSession chatSession = new ChatSession();
		chatSession.setSessionId(AesEncryptUtil.encrypt(uuid));
		chatSession.setIp(RequestUtils.getIpAddr(requests));
		chatSession.setStartTime(LocalDateTime.now());
		if(user != null){
			chatSession.setCreator(user.getUsername());
			chatSession.setUserId(Long.valueOf(user.getId()));
		}
		else{
			chatSession.setCreator("");
		}
		chatSessionService.save(chatSession);
		return chatSession;
	}

	@Override
	public void add(ChatInfo chatInfo, SysUserCacheInfo user) {
		String sessionId = chatInfo.getSessionId();
		checkSessionId(sessionId);
		List<ChatSessionDetail> chatSessionDetailList = new ArrayList<>();
		List<ChatInfo.ChatSessionInfo> list = chatInfo.getList();
		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(item -> {
				ChatSessionDetail chatSessionDetail = new ChatSessionDetail();
				chatSessionDetail.setSessionId(sessionId);
				chatSessionDetail.setCreator(user.getUsername());
				chatSessionDetail.setType(item.getType());
				chatSessionDetail.setContent(item.getContent());
				chatSessionDetailList.add(chatSessionDetail);
			});
			chatSessionInfoService.saveBatch(chatSessionDetailList);
		}
	}

	@Override
	public void deleteSession(String id, SysUserCacheInfo user) {
		LambdaQueryWrapper<ChatSession> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(ChatSession::getSessionId, id);
		queryWrapper.eq(ChatSession::getUserId, user.getId());
		ChatSession session = new ChatSession();
		session.setDeleted(true);
		chatSessionService.update(session, queryWrapper);
	}

	private void checkSessionId(String sessionId) {
		LambdaQueryWrapper<ChatSession> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(ChatSession::getSessionId, sessionId);
		if (chatSessionService.count(queryWrapper) != 1) {
			throw new BizException("会话id不存在，请联系管理员");
		}
	}
}
