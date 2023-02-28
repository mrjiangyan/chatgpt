package com.touchbiz.chatgpt.simple;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.touchbiz.common.entity.result.Result;
import com.touchbiz.common.utils.tools.JsonUtils;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.junit.Test;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class CscecTokenClearTest {

    private static OkHttpClient client =   (new OkHttpClient.Builder()).connectTimeout(5000L,TimeUnit.MILLISECONDS).readTimeout(100000L,TimeUnit.MILLISECONDS).writeTimeout(10000L,TimeUnit.MILLISECONDS).retryOnConnectionFailure(true).connectionPool(new ConnectionPool(500, 5L,TimeUnit.MINUTES)).build();

    @SneakyThrows
    @Test
    public void testDeleteMaterialUser(){
        var token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NzczOTU5NjksInVzZXJuYW1lIjoiMTc3MTc5MjE1MzEifQ.rsesZj4UQDrkMY_lfTTTTS452P2YqM_ihWxN3TDZFDs";
        var url = "https://aiot.cscec8b.com.cn:41883/ai/count/h5/sys/online/list?pageSize=100";
        //下载现有的所有用户
        var request = new Request.Builder()
                .addHeader("x-access-token", token)
                .url(url)
                .build();
        var response = client.newCall(request).execute();
        var json = response.body().string();
        log.info("response:{}", json);
        Result<PageDTO<SysUserOnlineVO>> result = JsonUtils.toObjectType(json, new TypeReference<Result<PageDTO<SysUserOnlineVO>>>() {
        });
        assert result.isSuccess();
        assert result.getResult().getRecords() != null;

        AtomicBoolean isSuccess = new AtomicBoolean(false);
        result.getResult().getRecords().stream().filter(
                x->!x.getUsername().equals("程帅")
                && !x.getUsername().equals("13917969197")
                && !Objects.equals(x.getToken(), "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NzcxMTU0MDcsInVzZXJuYW1lIjoiSmVmZiJ9.QjKhY5iSUaiAoYdRuoeVLznDWlZ10Kf3yvRBP0UkVEY"))
                .forEach(user->{
                    try {
                        forceLogout(
                               "https://aiot.cscec8b.com.cn:41883/ai/count/h5/sys/online/forceLogout",

                        token, user);
                        isSuccess.set(true);
                    } catch (Exception e) {
                        log.error("", e);
                    }
                });

        assert isSuccess.get();

    }

    @SneakyThrows
    @Test
    public void testAll(){
        while(true){
            int random = new Random().nextInt(1,10);
            log.info("random-material:{}", random);
            Thread.sleep(1000 * random);
            testDeleteMaterialUser();
            random = new Random().nextInt(1,10);
            log.info("random-nameplate:{}", random);
            Thread.sleep(1000 * random);
            testDeleteMaterialUser();
        }
    }


    @SneakyThrows
    @Test
    public void testDeleteNameplateUser(){
        var token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2Nzc0MDA1OTMsInVzZXJuYW1lIjoiMTc3MTc5MjE1MzEifQ.acp6hDPYLMRkLTre-PejhE3OQVUCIyrUfgt21Sz0aDQ";
        var url = "https://aiot.cscec8b.com.cn:41883/ai/ocr/h5/sys/online/list?pageSize=100";
        //下载现有的所有用户
        var request = new Request.Builder()
                .addHeader("x-access-token", token)
                .url(url)
                .build();
        var response = client.newCall(request).execute();
        var json = response.body().string();
        log.info("response:{}", json);
        Result<PageDTO<SysUserOnlineVO>> result = JsonUtils.toObjectType(json, new TypeReference<Result<PageDTO<SysUserOnlineVO>>>() {
        });
        assert result.isSuccess();

        AtomicBoolean isSuccess = new AtomicBoolean(false);
        result.getResult().getRecords().stream().filter(
                        x->!x.getUsername().equals("程帅"))
                .forEach(user->{
                    try {
                        forceLogout(
                                "https://aiot.cscec8b.com.cn:41883/ai/ocr/h5/sys/online/forceLogout",
                                token, user);
                        isSuccess.set(true);
                    } catch (Exception e) {
                        log.error("", e);
                    }
                });

        assert isSuccess.get();

    }

    @SneakyThrows
    public void forceLogout(String url, String token, SysUserOnlineVO user){
        //下载现有的所有用户
        var request = new Request.Builder()
                .addHeader("x-access-token", token)
                .post(RequestBody.create(MediaType.parse("application/json;charset=utf-8"), JsonUtils.toJson(user).getBytes()))
                .url(url)
                .build();
        var response = client.newCall(request).execute();
        String json = response.body().string();
        log.info("user:{}, json:{}", user, json);
    }



        @Data
        public static class SysUserOnlineVO {
            /**
             * 会话id
             */
            private String id;

            /**
             * 会话编号
             */
            private String token;

            /**
             * 用户名
             */
            private String username;

            /**
             * 用户名
             */
            private String realname;


            /**
             * 性别（1：男 2：女）
             */
             /**
             * 手机号
             */
            private String phone;
        }

}
