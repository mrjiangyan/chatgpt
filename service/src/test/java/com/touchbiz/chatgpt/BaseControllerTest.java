package com.touchbiz.chatgpt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.touchbiz.cache.starter.IRedisTemplate;
import com.touchbiz.common.entity.model.SysUserCacheInfo;
import com.touchbiz.common.entity.result.ApiResult;
import com.touchbiz.common.utils.tools.JsonUtils;
import com.touchbiz.webflux.starter.configuration.HttpHeaderConstants;
import com.touchbiz.webflux.starter.filter.ReactiveRequestContextHolder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.WebFilter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

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

    @Autowired
    private IRedisTemplate redisTemplate;

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

    public final <T> ApiResult<T> getTest(String url, MediaType contentType) {
        MockHttpServletRequestBuilder requestBuilder = get(url);
        return process(null, requestBuilder, contentType);
    }

    public <T> ApiResult<T> postTest(String url, Object postParam) {
        MockHttpServletRequestBuilder requestBuilder = post(url);
        return process(postParam, requestBuilder, MediaType.APPLICATION_JSON);
    }

    public <T> ApiResult<T> putTest(String url, Object postParam, MediaType contentType) {
        MockHttpServletRequestBuilder requestBuilder = put(url);
        return process(postParam, requestBuilder, contentType);
    }

    private <T> ApiResult<T> process(Object postParam, MockHttpServletRequestBuilder requestBuilder, MediaType contentType) {
        if (postParam != null) {
            if (postParam instanceof byte[]) {
                requestBuilder.content((byte[]) postParam);
            } else if (postParam instanceof String) {
                requestBuilder.content((String) postParam);
            } else {
                requestBuilder.content(JsonUtils.toJson(postParam));
            }
        } else {
            requestBuilder.content(JsonUtils.toJson(null));
        }
        if (contentType != null) {
            requestBuilder.contentType(contentType);
        }
//        MvcResult result = getMockMvc().perform(requestBuilder).andExpect(status().isOk())
//                .andReturn();
        String json = null;//result.getResponse().getContentAsString();
        TypeReference reference = new TypeReference<ApiResult<T>>() {
        };
        return JsonUtils.toObjectType(json, reference);
    }

    public <T> ApiResult<T> deleteTest(String url, Object postParam, MediaType contentType) {
        MockHttpServletRequestBuilder requestBuilder = delete(url);
        return process(postParam, requestBuilder, contentType);
    }

}
