package com.touchbiz.chatgpt.common;

import com.touchbiz.chatgpt.boot.ChatgptApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(value = {"classpath:local.yml"}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = ChatgptApplication.class )
@ActiveProfiles(profiles = "dev")
@AutoConfigureMockMvc
//@Transactional
public abstract class BaseTest {

    @LocalServerPort
    int randomServerPort;

}