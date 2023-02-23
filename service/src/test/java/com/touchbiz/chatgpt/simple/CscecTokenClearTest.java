package com.touchbiz.chatgpt.simple;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.touchbiz.common.entity.result.Result;
import com.touchbiz.common.utils.security.AesEncryptUtil;
import com.touchbiz.common.utils.tools.JsonUtils;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.junit.Test;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
public class CscecTokenClearTest {

    private static OkHttpClient client =   (new OkHttpClient.Builder()).connectTimeout(5000L,TimeUnit.MILLISECONDS).readTimeout(100000L,TimeUnit.MILLISECONDS).writeTimeout(10000L,TimeUnit.MILLISECONDS).retryOnConnectionFailure(true).connectionPool(new ConnectionPool(500, 5L,TimeUnit.MINUTES)).build();

    @SneakyThrows
    @Test
    public void testDeleteMaterialUser(){
        var token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NzY5ODg2MjksInVzZXJuYW1lIjoiSmVmZiJ9.JoWXXJHLm5sdeKqbLAYWkrgOaET-UFmNpXdaO1roJew";
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

        assert isSuccess.get() != false;

    }


    @SneakyThrows
    @Test
    public void testDeleteNameplateUser(){
        var token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NzY5ODk3MDAsInVzZXJuYW1lIjoiSmVmZiJ9.DbQxh0IkKt-xAyFim59f7hnlnXrDr1Gvm-xjYV2B2fw";
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

        assert isSuccess.get() != false;

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
             * 头像
             */
            private String avatar;

            /**
             * 生日
             */
            @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            private Date birthday;

            /**
             * 性别（1：男 2：女）
             */
             /**
             * 手机号
             */
            private String phone;
        }

}
