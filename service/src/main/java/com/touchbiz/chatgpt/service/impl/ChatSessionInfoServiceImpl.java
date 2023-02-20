package com.touchbiz.chatgpt.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchbiz.chatgpt.database.domain.ChatSessionInfo;
import com.touchbiz.chatgpt.database.mapper.ChatSessionInfoMapper;
import com.touchbiz.chatgpt.service.ChatSessionInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * chat会话信息
 * </p>
 *
 * @Author: scott
 * @Date: 2018-12-20
 */
@Service
@Slf4j
public class ChatSessionInfoServiceImpl extends ServiceImpl<ChatSessionInfoMapper, ChatSessionInfo> implements ChatSessionInfoService {


}
