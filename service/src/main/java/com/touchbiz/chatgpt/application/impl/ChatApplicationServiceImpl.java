package com.touchbiz.chatgpt.application.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchbiz.cache.starter.IRedisTemplate;
import com.touchbiz.chatgpt.application.ChatApplicationService;
import com.touchbiz.chatgpt.database.domain.ChatSession;
import com.touchbiz.chatgpt.database.domain.ChatSessionDetail;
import com.touchbiz.chatgpt.dto.Chat;
import com.touchbiz.chatgpt.dto.ChatInfo;
import com.touchbiz.chatgpt.dto.ChatResult;
import com.touchbiz.chatgpt.infrastructure.constants.CacheConstant;
import com.touchbiz.chatgpt.infrastructure.enums.ChatSessionInfoTypeEnum;
import com.touchbiz.chatgpt.infrastructure.utils.AesEncryptUtil;
import com.touchbiz.chatgpt.infrastructure.utils.RequestUtils;
import com.touchbiz.chatgpt.service.ChatSessionInfoService;
import com.touchbiz.chatgpt.service.ChatSessionService;
import com.touchbiz.common.entity.exception.BizException;
import com.touchbiz.common.entity.model.SysUserCacheInfo;
import com.touchbiz.webflux.starter.filter.ReactiveRequestContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.touchbiz.chatgpt.infrastructure.constants.CacheConstant.CHAT_SESSION_EXPIRE_SECONDS;

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

    @Autowired
    protected IRedisTemplate redisTemplate;

    @Override
    public IPage<ChatSession> getChatSessionPageList(Integer pageNo, Integer pageSize, SysUserCacheInfo user) {
        Page<ChatSession> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<ChatSession> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatSession::getUserId, user.getId());
        queryWrapper.eq(ChatSession::getDeleted, false);
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
        if (user != null) {
            chatSession.setCreator(user.getUsername());
            chatSession.setUserId(Long.valueOf(user.getId()));
        } else {
            chatSession.setCreator("");
        }
        chatSessionService.save(chatSession);
        return chatSession;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createSessionInfo(Chat chat, ChatResult result, SysUserCacheInfo user) {
        String sessionId = chat.getSessionId();
        String redis = CacheConstant.CHAT_SESSION_SEQUENCE_KEY + sessionId;
        if (redisTemplate.hasKey(redis)) {
            redisTemplate.incr(redis);
        } else {
            redisTemplate.set(redis, 0, CacheConstant.CHAT_SESSION_SEQUENCE_EXPIRE_SECONDS);
        }
        List<ChatResult.Choice> choices = result.getChoices();
        ChatSessionDetail chatSessionQuestionDetail = ChatSessionDetail.builder()
                .sessionId(sessionId)
                .sequence(Long.valueOf(String.valueOf(redisTemplate.get(redis))))
                .content(chat.getPrompt())
                .type(ChatSessionInfoTypeEnum.QUESTION.getCode())
                .build();
        ChatSessionDetail chatSessionAnswerDetail = ChatSessionDetail.builder()
                .sessionId(sessionId)
                .sequence(Long.valueOf(String.valueOf(redisTemplate.get(redis))))
                .content(CollectionUtils.isEmpty(choices) ? "" : choices.get(0).getText())
                .type(ChatSessionInfoTypeEnum.ANSWER.getCode())
                .build();
        if (user != null) {
            chatSessionQuestionDetail.setUserId(Long.valueOf(user.getId()));
            chatSessionQuestionDetail.setCreator(user.getUsername());
            chatSessionAnswerDetail.setUserId(Long.valueOf(user.getId()));
            chatSessionAnswerDetail.setCreator(user.getUsername());
        } else {
            chatSessionQuestionDetail.setCreator("");
            chatSessionAnswerDetail.setCreator("");
        }
        List<ChatSessionDetail> list = Arrays.asList(chatSessionQuestionDetail, chatSessionAnswerDetail);
        chatSessionInfoService.saveBatch(list);
        return chatSessionAnswerDetail.getContent();
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

    @Override
    public void checkSessionId(String sessionId) {
        LambdaQueryWrapper<ChatSession> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatSession::getSessionId, sessionId);
        queryWrapper.eq(ChatSession::getDeleted, false);
        if (chatSessionService.count(queryWrapper) != 1) {
            throw new BizException("会话id不存在，请联系管理员");
        }
        if (!AesEncryptUtil.isDesEncrypt(sessionId)) {
            throw new BizException("会话id验证失败，请联系管理员");
        }
    }
}
