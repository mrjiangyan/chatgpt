package com.touchbiz.chatgpt.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchbiz.chatgpt.database.domain.ChatSession;
import com.touchbiz.chatgpt.database.mapper.ChatSessionMapper;
import com.touchbiz.chatgpt.service.ChatSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
public class ChatSessionServiceImpl extends ServiceImpl<ChatSessionMapper, ChatSession> implements ChatSessionService {


}
