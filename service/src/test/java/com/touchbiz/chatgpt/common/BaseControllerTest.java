package com.touchbiz.chatgpt.common;

import com.touchbiz.common.entity.model.SysUserCacheInfo;
import com.touchbiz.webflux.starter.configuration.HttpHeaderConstants;
import com.touchbiz.webflux.starter.filter.ReactiveRequestContextHolder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.WebFilter;

/**
 * @Author: Steven Jiang(jiangyan12@meituan.com)
 * @Date: 2018/10/30 下午1:54
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class BaseControllerTest extends BaseTest {

    private MockMvc mockMvc;

//    @Autowired
//    private WebApplicationContext context;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
//        new MockUp<BaseController>() {
//
//        };
//        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();//建议使用这种

        SysUserCacheInfo token = new SysUserCacheInfo();
        token.setId("0");
        ReactiveRequestContextHolder.putUser(token);
    }



    public final WebFilter filter = (serverWebExchange, webFilterChain) -> {
        String key = "TOKEN_USER" + "abc";
        MockServerHttpRequest request = (MockServerHttpRequest) serverWebExchange.getRequest();
        request.getHeaders().add(HttpHeaderConstants.HEADER_TOKEN, "abc");
//            redisTemplate.setObject(key, new AccountToken(), 100 * 100);
        return webFilterChain.filter(serverWebExchange);
    };

}
