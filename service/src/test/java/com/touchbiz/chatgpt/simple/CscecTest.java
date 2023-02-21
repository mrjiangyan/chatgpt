package com.touchbiz.chatgpt.simple;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.touchbiz.chatgpt.common.BaseTest;
import com.touchbiz.common.entity.result.Result;
import com.touchbiz.common.utils.tools.JsonUtils;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CscecTest extends BaseTest {

    private OkHttpClient client =   (new OkHttpClient.Builder()).connectTimeout(5000L,TimeUnit.MILLISECONDS).readTimeout(10000L,TimeUnit.MILLISECONDS).writeTimeout(10000L,TimeUnit.MILLISECONDS).retryOnConnectionFailure(true).connectionPool(new ConnectionPool(10, 5L,TimeUnit.MINUTES)).build();
    ;

    private String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NzcwMjk4MDEsInVzZXJuYW1lIjoiYWRtaW4ifQ.hWFNq-Ueg_c8740trPyh7GIATOKX_V9WnV5h69_dsuE";
    @Test
    public void changeTag(){

    }

    @SneakyThrows
    @Test
    public void queryList(){
        String url = "https://aiot.cscec8b.com.cn:41883/ai/count/backend/auth/account/pages?_t=1676943499&pageNo=1&pageSize=30&name=";
        var headers =new HashMap<String, Object>();
        headers.put("x-access-token", token);
        headers.put("accept", "application/json, text/plain, */*");

        var request = new Request.Builder()
                .url(url)
                .addHeader("x-access-token", token)
                        .addHeader("accept", "application/json, text/plain, */*")
                                .build();
        var response = client.newCall(request).execute();
        String json = response.body().string();


        log.info("response:{}", json);
        Result<PageDTO<ApiAuth>> result = JsonUtils.toObjectType(json, new TypeReference<Result<PageDTO<ApiAuth>>>() {
        });
        assert result.isSuccess();
        assert result.getResult().getRecords() != null;

        result.getResult().getRecords().stream().filter(x->x.getStatus() == 1)
                .forEach(x->{
                    log.info("auth:{}", x);
                });

    }

    @Data
    public static class ApiAuth{

        private String id;

        private String appId;

        private String name;

        private String secretKey;

        private Integer status;

    }
}
