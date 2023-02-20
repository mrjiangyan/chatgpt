package com.touchbiz.chatgpt.simple;

import com.touchbiz.chatgpt.infrastructure.utils.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class SimpleTest {


    @Test
    public void test(){
        log.info("{}", PasswordUtil.encrypt("admin","123456","12345678"));
    }
}
