package com.touchbiz.chatgpt.application.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchbiz.cache.starter.IRedisTemplate;
import com.touchbiz.chatgpt.application.ChatService;
import com.touchbiz.chatgpt.database.domain.ChatSession;
import com.touchbiz.chatgpt.database.domain.ChatSessionInfo;
import com.touchbiz.chatgpt.database.domain.SysUser;
import com.touchbiz.chatgpt.dto.ChatInfo;
import com.touchbiz.chatgpt.infrastructure.constants.CommonConstant;
import com.touchbiz.chatgpt.infrastructure.converter.ChatSessionInfoConverter;
import com.touchbiz.chatgpt.infrastructure.utils.RequestUtils;
import com.touchbiz.chatgpt.service.ChatSessionInfoService;
import com.touchbiz.chatgpt.service.ChatSessionService;
import com.touchbiz.common.entity.exception.BizException;
import com.touchbiz.common.entity.result.MonoResult;
import com.touchbiz.common.utils.tools.JsonUtils;
import com.touchbiz.webflux.starter.filter.ReactiveRequestContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
public class ChatServiceImpl implements ChatService {

	@Autowired
	private ChatSessionService chatSessionService;

	@Autowired
	private ChatSessionInfoService chatSessionInfoService;

	@Autowired
	private IRedisTemplate iRedisTemplate;

	@Override
	public IPage<ChatInfo> getPageList(Integer pageNo, Integer pageSize, SysUser user) {
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
	public String addSessionId(SysUser user, HttpServletRequest request) {
		String uuid = UUID.randomUUID().toString();
		Map<String, Object> heads = RequestUtils.getHeads(request);
		ChatSession chatSession = new ChatSession();
		chatSession.setSessionId(uuid);
		//获取request
		var requests = ReactiveRequestContextHolder.get();
		chatSession.setIp(RequestUtils.getIpAddr(requests));
		chatSession.setHead(JsonUtils.toJson(heads));
		chatSession.setSessionStartTime(LocalDateTime.now());
		chatSession.setCreator(user.getUsername());
		chatSessionService.save(chatSession);
		return uuid;
	}

	@Override
	public void add(ChatInfo chatInfo, SysUser user) {
		String sessionId = chatInfo.getSessionId();
		checkSessionId(sessionId);
		List<ChatSessionInfo> chatSessionInfoList = new ArrayList<>();
		List<ChatInfo.ChatSessionInfo> list = chatInfo.getList();
		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(item -> {
				ChatSessionInfo chatSessionInfo = new ChatSessionInfo();
				chatSessionInfo.setId(sessionId);
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
