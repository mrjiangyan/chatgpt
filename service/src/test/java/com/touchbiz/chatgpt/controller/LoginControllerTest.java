package com.touchbiz.chatgpt.controller;

import com.touchbiz.cache.starter.IRedisTemplate;
import com.touchbiz.chatgpt.common.BaseTest;
import com.touchbiz.chatgpt.database.domain.ChatSession;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

@Slf4j
public class LoginControllerTest extends BaseTest {

    @Autowired
    private IRedisTemplate redisTemplate;

    @Test
    public void login() {
        ChatSession session = new ChatSession();
        session.setSessionId("11111");
        var key = "AAA";
        var success = redisTemplate.setObject(key, session, 200);
        log.info("success:{}", success);
        session = redisTemplate.getObject(key , ChatSession.class);
        log.info("session:{}", session);
    }
}