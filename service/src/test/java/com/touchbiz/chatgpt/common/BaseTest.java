package com.touchbiz.chatgpt.common;

import com.touchbiz.chatgpt.boot.ChatgptApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = ChatgptApplication.class)
@ActiveProfiles(profiles = "dev")
@AutoConfigureMockMvc
//@Transactional
@Import({FeignAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class})
public abstract class BaseTest {

    @LocalServerPort
    int randomServerPort;

}