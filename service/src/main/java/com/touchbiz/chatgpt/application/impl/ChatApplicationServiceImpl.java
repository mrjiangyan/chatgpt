package com.touchbiz.chatgpt.application.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchbiz.chatgpt.application.ChatApplicationService;
import com.touchbiz.chatgpt.database.domain.ChatSession;
import com.touchbiz.chatgpt.database.domain.ChatSessionInfo;
import com.touchbiz.chatgpt.dto.Chat;
import com.touchbiz.chatgpt.dto.ChatInfo;
import com.touchbiz.chatgpt.infrastructure.converter.ChatSessionInfoConverter;
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
import java.util.UUID;
import java.util.stream.Collectors;

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
	public IPage<ChatInfo> getPageList(Integer pageNo, Integer pageSize, SysUserCacheInfo user) {
		Page<ChatSession> page = new Page<>(pageNo, pageSize);
		LambdaQueryWrapper<ChatSession> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(ChatSession::getCreator, user.getUsername());
		queryWrapper.orderByDesc(ChatSession::getGmtCreate);
		Page<ChatSession> ChatSessionPage = chatSessionService.page(page, queryWrapper);
		List<ChatSession> records = ChatSessionPage.getRecords();
		if (CollectionUtils.isEmpty(records)) {
			return new Page<>();
		}
		List<String> sessionIds = records.stream().map(ChatSession::getSessionId).collect(Collectors.toList());
		List<ChatSessionInfo> chatSessionInfoList = chatSessionInfoService.listByIds(sessionIds);
		IPage<ChatInfo> iPage = new Page<>();
		List<ChatInfo> list = records.stream().map(item -> {
			ChatInfo chatInfo = new ChatInfo();
			List<ChatInfo.ChatSessionInfo> chatSessionInfos = new ArrayList<>();
			chatInfo.setSessionId(item.getSessionId());
			chatInfo.setCreateTime(item.getGmtCreate().toLocalDateTime());
			chatInfo.setCreator(user.getUsername());
			chatSessionInfoList.stream().filter(chatSessionInfo -> item.getSessionId().equals(chatSessionInfo.getId())).forEach(chatSessionInfo -> {
						ChatInfo.ChatSessionInfo chatSession = ChatSessionInfoConverter.INSTANCE.transformOut(chatSessionInfo);
						chatSession.setSessionTime(chatSessionInfo.getGmtCreate().toLocalDateTime());
						chatSessionInfos.add(chatSession);
					}
			);
			chatInfo.setList(chatSessionInfos);
			return chatInfo;
		}).collect(Collectors.toList());
		iPage.setRecords(list);
		iPage.setCurrent(pageNo);
		iPage.setSize(pageSize);
		iPage.setTotal(ChatSessionPage.getTotal());
		return iPage;
	}

	@Override
	public ChatSession createSession(SysUserCacheInfo user) {
		String uuid = UUID.randomUUID().toString();
		//获取request
		var requests = ReactiveRequestContextHolder.get();
		ChatSession chatSession = new ChatSession();
		chatSession.setSessionId(AesEncryptUtil.encrypt(uuid));
		chatSession.setIp(RequestUtils.getIpAddr(requests));
		chatSession.setStartTime(LocalDateTime.now());
		if(user != null){
			chatSession.setCreator(user.getUsername());
		}
		chatSessionService.save(chatSession);
		return chatSession;
	}

	@Override
	public void add(ChatInfo chatInfo, SysUserCacheInfo user) {
		String sessionId = chatInfo.getSessionId();
		checkSessionId(sessionId);
		List<ChatSessionInfo> chatSessionInfoList = new ArrayList<>();
		List<ChatInfo.ChatSessionInfo> list = chatInfo.getList();
		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(item -> {
				ChatSessionInfo chatSessionInfo = new ChatSessionInfo();
				chatSessionInfo.setSessionId(sessionId);
				chatSessionInfo.setCreator(user.getUsername());
				chatSessionInfo.setType(item.getType());
				chatSessionInfo.setContent(item.getContent());
				chatSessionInfoList.add(chatSessionInfo);
			});
			chatSessionInfoService.saveBatch(chatSessionInfoList);
		}
	}

	@Override
	public void delete(String id) {
		chatSessionInfoService.removeById(id);
		LambdaQueryWrapper<ChatSession> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(ChatSession::getSessionId, id);
		ChatSession one = chatSessionService.getOne(queryWrapper);
		chatSessionService.removeById(one.getId());
	}

	private void checkSessionId(String sessionId) {
		LambdaQueryWrapper<ChatSession> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(ChatSession::getSessionId, sessionId);
		if (chatSessionService.count(queryWrapper) != 1) {
			throw new BizException("会话id不存在，请联系管理员");
		}
	}
}
