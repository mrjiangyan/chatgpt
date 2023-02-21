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
import com.touchbiz.chatgpt.dto.ChatResult;
import com.touchbiz.chatgpt.dto.response.LoginUser;
import com.touchbiz.chatgpt.infrastructure.enums.ChatSessionInfoTypeEnum;
import com.touchbiz.chatgpt.infrastructure.utils.AesEncryptUtil;
import com.touchbiz.chatgpt.infrastructure.utils.RequestUtils;
import com.touchbiz.chatgpt.service.ChatSessionInfoService;
import com.touchbiz.chatgpt.service.ChatSessionService;
import com.touchbiz.common.entity.exception.BizException;
import com.touchbiz.common.utils.tools.JsonUtils;
import com.touchbiz.webflux.starter.filter.ReactiveRequestContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.touchbiz.chatgpt.infrastructure.constants.CacheConstant.*;

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

    /**
     * 主题截取长度
     */
    private final int length = 10;

    @Override
    public IPage<ChatSession> getChatSessionPageList(Integer pageNo, Integer pageSize, LoginUser user) {
        Page<ChatSession> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<ChatSession> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatSession::getUserId, user.getId());
        queryWrapper.eq(ChatSession::getDeleted, false);
        queryWrapper.orderByDesc(ChatSession::getGmtCreate);
        var sessionPage = chatSessionService.page(page, queryWrapper);
        return sessionPage;
    }

    @Override
    public ChatSession createSession(LoginUser user) {
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
    public String createSessionInfo(Chat chat, ChatResult result, LoginUser user) {
        String sessionId = chat.getSessionId();
        String prompt = chat.getPrompt();
        String redis = CHAT_SESSION_SEQUENCE_KEY + sessionId;
        if (redisTemplate.hasKey(redis)) {
            redisTemplate.incr(redis);
        } else {
            //保存主题为第一次的问题
            ChatSession chatSession = JsonUtils.toObject(JsonUtils.toJson(redisTemplate.get(CHAT_SESSION_KEY + sessionId)), ChatSession.class);
            if (!ObjectUtils.isEmpty(chatSession)) {
                chatSession.setSubject(prompt.length() > length ? prompt.substring(0, length) + "..." : prompt);
                chatSessionService.updateById(chatSession);
            }
            redisTemplate.set(redis, 0, CHAT_SESSION_SEQUENCE_EXPIRE_SECONDS);
        }
        List<ChatResult.Choice> choices = result.getChoices();
        ChatSessionDetail chatSessionQuestionDetail = ChatSessionDetail.builder()
                .sessionId(sessionId)
                .sequence(Long.valueOf(String.valueOf(redisTemplate.get(redis))))
                .content(prompt)
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
    public void deleteSession(String id, LoginUser user) {
        LambdaQueryWrapper<ChatSession> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatSession::getSessionId, id);
        queryWrapper.eq(ChatSession::getUserId, user.getId());
        ChatSession session = new ChatSession();
        session.setDeleted(true);
        chatSessionService.update(session, queryWrapper);
    }

    @Override
    public void checkSessionId(String sessionId) {
        if (!redisTemplate.hasKey(CHAT_SESSION_KEY + sessionId)) {
            throw new BizException("会话id不存在，请联系管理员");
        }
        if (!AesEncryptUtil.isDesEncrypt(sessionId)) {
            throw new BizException("会话id验证失败，请联系管理员");
        }
    }
}
