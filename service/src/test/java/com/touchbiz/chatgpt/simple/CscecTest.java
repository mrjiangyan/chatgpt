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

@Slf4j
public class CscecTest  {

    private static OkHttpClient client =   (new OkHttpClient.Builder()).connectTimeout(5000L,TimeUnit.MILLISECONDS).readTimeout(100000L,TimeUnit.MILLISECONDS).writeTimeout(10000L,TimeUnit.MILLISECONDS).retryOnConnectionFailure(true).connectionPool(new ConnectionPool(500, 5L,TimeUnit.MINUTES)).build();

    private String listJson ="{\"success\":true,\"message\":\"\",\"code\":200,\"result\":{\"records\":[{\"id\":80,\"appid\":\"SuypYZUFIIApRriIi\",\"formType\":null,\"name\":\"大年搜\",\"secretKey\":\"QUUO3WYA1ZaN9gOS46gqIXtbeFux8s2IOePkjw9h2ImNYQYXl0xEbqlWWDOe3YlXLajN7llOnw\",\"status\":1,\"gmtCreate\":\"2023-02-17 10:50:41\",\"gmtModify\":null,\"deleted\":0,\"formTypeName\":null},{\"id\":74,\"appid\":\"gEM5YqnGpj7t8GWg3\",\"formType\":null,\"name\":\"21\",\"secretKey\":\"9MoB8LSZIvfmdO46l5pjQa4GddzdxXl7SduU7qGuAOtmsyUii5RdNoRGMTddbpByWN8T4mimJY\",\"status\":2,\"gmtCreate\":\"2023-01-06 16:35:18\",\"gmtModify\":\"2023-01-06 16:35:30\",\"deleted\":0,\"formTypeName\":null},{\"id\":73,\"appid\":\"hkYd2oH8LRGZoVbHl\",\"formType\":null,\"name\":\"测测\",\"secretKey\":\"Eb6kuvJzQgTdrA8MNHl83v8dd3briCKh4J7V2bEm0lfPqam40oZcZHD3lpwVh92r1IBc7qhfr0\",\"status\":2,\"gmtCreate\":\"2023-01-06 16:31:59\",\"gmtModify\":\"2023-01-06 16:32:04\",\"deleted\":0,\"formTypeName\":null},{\"id\":68,\"appid\":\"GthaY332PbueAq5bT\",\"formType\":null,\"name\":\"1112\",\"secretKey\":\"R3s3zWIv8O41gtBYp6mL4blTkVkeXRv5whaFNWZfmiFUaRwT7aGgsRy31iQy8JgLVMINU8prCV\",\"status\":1,\"gmtCreate\":\"2022-12-30 15:19:23\",\"gmtModify\":\"2022-12-30 15:19:31\",\"deleted\":0,\"formTypeName\":null},{\"id\":60,\"appid\":\"uWmzP9TmGWQDFMxHv\",\"formType\":null,\"name\":\"小帅\",\"secretKey\":\"j57DUn0EOymvzy62GWwWqEhfd3tJX8J4e5P3yVqpS4FQGAniYmba3E8DceudjDKH1X9McMtjWy\",\"status\":1,\"gmtCreate\":\"2022-12-21 22:29:55\",\"gmtModify\":null,\"deleted\":0,\"formTypeName\":null},{\"id\":58,\"appid\":\"4Jo89Txg6CzoFhU7K\",\"formType\":null,\"name\":\"7657\",\"secretKey\":\"QFiZDjcJJ6PaL7yzY3J0h392sU7TYjEw3QjH68PluUQ8SeLENB9KGbiRjLL3KBP76lkaaLEptJ\",\"status\":1,\"gmtCreate\":\"2022-12-21 10:38:41\",\"gmtModify\":\"2022-12-21 11:18:04\",\"deleted\":0,\"formTypeName\":null},{\"id\":57,\"appid\":\"dqASM9YQVg0jkTNhI\",\"formType\":null,\"name\":\"532423\",\"secretKey\":\"BIzWXibUC6UZBvqQiI5irhv2lZDFGtDuAxnLRFFGLL4xHJ5Vv82LVSEOI9ycJ8QiiUlcVkkARG\",\"status\":1,\"gmtCreate\":\"2022-12-21 10:38:37\",\"gmtModify\":null,\"deleted\":0,\"formTypeName\":null},{\"id\":56,\"appid\":\"Nx5IA8lCVb7a23rd3\",\"formType\":null,\"name\":\"423\",\"secretKey\":\"wan6ZeIlcI7e8qGr3GlhOMAHbc1s14ha2NjTsUPietBfR8AOR3PEp5l5zWbGWBOUBOTz9gLagp\",\"status\":1,\"gmtCreate\":\"2022-12-21 10:38:23\",\"gmtModify\":null,\"deleted\":0,\"formTypeName\":null},{\"id\":55,\"appid\":\"HYcltT6ciDJYiK8Jp\",\"formType\":null,\"name\":\"43243211\",\"secretKey\":\"4HUADmoqpFU2UwokB6XeoSTHxH4SLJ3VC0GBkaZ7O7i63wQfIkxKLy8nC5h3dCuxeauGDmzIDg\",\"status\":1,\"gmtCreate\":\"2022-12-21 10:35:53\",\"gmtModify\":null,\"deleted\":0,\"formTypeName\":null},{\"id\":54,\"appid\":\"3zBVeVrWNXJ2IzsDS\",\"formType\":null,\"name\":\"43243\",\"secretKey\":\"BoCCluxXfKBKEX4HEnEm9Vhd1jAAgjnrxQbYXpPpbBtxmbUzGYJrXeoCRjRFM9WB34xn0EROQG\",\"status\":1,\"gmtCreate\":\"2022-12-21 10:33:50\",\"gmtModify\":null,\"deleted\":0,\"formTypeName\":null},{\"id\":53,\"appid\":\"GthLXUxZBCkGJZ7jM\",\"formType\":null,\"name\":\"432432\",\"secretKey\":\"7rTqroypIxkszPYLuX0hw6wPY8DlFDq8zMKQ3M6jyk7e613EFQNxFn8bkMNeZBAQbQLC9EGd5w\",\"status\":1,\"gmtCreate\":\"2022-12-21 10:33:46\",\"gmtModify\":null,\"deleted\":0,\"formTypeName\":null},{\"id\":52,\"appid\":\"RztKhJucFwyoMFqmA\",\"formType\":null,\"name\":\"3213\",\"secretKey\":\"0GQrXFGAkcg5pO5UOifCuHaT5vvgSzrTzRunNLPdKMemcx40UFyip6tlM0hPX43eklwoorCyxc\",\"status\":1,\"gmtCreate\":\"2022-12-21 10:32:35\",\"gmtModify\":null,\"deleted\":0,\"formTypeName\":null},{\"id\":51,\"appid\":\"qsHY3otlsA1yh0FBA\",\"formType\":null,\"name\":\"312\",\"secretKey\":\"04RCx6UKRyU4tDRTNiv5X6Bh0NGVU9UtEoRXO0BmGXEJSzOfGk8eyCeQ2UMn8F5BUJzu158X9J\",\"status\":1,\"gmtCreate\":\"2022-12-21 10:31:35\",\"gmtModify\":null,\"deleted\":0,\"formTypeName\":null},{\"id\":1,\"appid\":\"l9d58d177b05b93f6\",\"formType\":null,\"name\":\"test_un_operation\",\"secretKey\":\"52_YgwabmkB-_KU67QWD8UdQ29j_y72IjgNSXOhx0Bz6DfkQ6WhBrCqSix2bDmregNrsM5bN9IPFapTb6rtD_sgKYSHMOJyjxwtimdUzmS9z0roVYoJVWluHepKpP0avFr8QVWwuH0IbVE0Xtw6GNLdAJDXEC\",\"status\":1,\"gmtCreate\":\"2022-12-03 00:51:30\",\"gmtModify\":\"2022-12-30 15:19:56\",\"deleted\":0,\"formTypeName\":null}],\"total\":14,\"size\":30,\"current\":1,\"orders\":[],\"optimizeCountSql\":true,\"searchCount\":true,\"optimizeJoinOfCountSql\":true,\"countId\":null,\"maxLimit\":null,\"pages\":1},\"timestamp\":1676959700106}";

    private String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NzcwMjk4MDEsInVzZXJuYW1lIjoiYWRtaW4ifQ.hWFNq-Ueg_c8740trPyh7GIATOKX_V9WnV5h69_dsuE";
    @Test
    public void changeTag(){

    }

    @SneakyThrows
    @Test
    public void queryList(){
        String url = "https://aiot.cscec8b.com.cn:41883/ai/count/backend/auth/account/pages?_t=1676943499&pageNo=1&pageSize=30&name=";

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
                    try {
                        x.setSecretKey(AesEncryptUtil.encrypt(x.getSecretKey()));
                        if(x.getId()%10 ==2){
                            x.setStatus(0);
                            log.info("设置状态为停用,auth:{},", x);
                        }
                        set(x);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

    }

    @SneakyThrows
    @Test
    public void testDeleteUser(){
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
                        forceLogout(token, user);
                        isSuccess.set(true);
                    } catch (Exception e) {
                        log.error("", e);
                    }
                });

        assert isSuccess.get();

    }

    @SneakyThrows
    public void forceLogout(String token, SysUserOnlineVO user){
        var url = "https://aiot.cscec8b.com.cn:41883/ai/count/h5/sys/online/forceLogout";
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

    @SneakyThrows
    @Test
    public void testDownload(){
        for(int i=0;i<200;i++){
            new DownloadThread().start();
        }
        Thread.sleep(24 * 60 * 60 *1000);
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


        public static class DownloadThread extends Thread{
//        {"success":true,"message":"","code":200,"result":{"records":[{"id":1662,"creator":"1595444549567979546","modifier":null,"gmtCreate":"2023-02-17 15:19:00","gmtModify":"2023-02-17 15:19:28","modelId":19,"materialName":"钢筋","quantity":72,"review":73,"model":"","spec":"","imageUrl":"material_1676618340172.jpg","rawImageUrl":"https://aiot.cscec8b.com.cn:41883/ai/count/backend/api/biz/image?path=material_1676618340172.jpg","fileSize":0,"fileName":"material_1676618340172.jpg","appid":null,"targetImage":"material_target_1676618340271.jpg","sourceImage":null},{"id":1627,"creator":"1595444549567979552","modifier":null,"gmtCreate":"2023-02-16 18:06:36","gmtModify":"2023-02-16 18:06:40","modelId":19,"materialName":"钢筋","quantity":4,"review":11,"model":"","spec":"","imageUrl":"material_1676541995988.jpg","rawImageUrl":"https://aiot.cscec8b.com.cn:41883/ai/count/backend/api/biz/image?path=material_1676541995988.jpg","fileSize":0,"fileName":"material_1676541995988.jpg","appid":null,"targetImage":"material_target_1676541996100.jpg","sourceImage":null},{"id":1599,"creator":"1595444549567979552","modifier":null,"gmtCreate":"2023-02-16 01:51:15","gmtModify":"2023-02-16 01:51:30","modelId":19,"materialName":"钢筋","quantity":0,"review":6,"model":"","spec":"","imageUrl":"material_1676483475272.jpg","rawImageUrl":"https://aiot.cscec8b.com.cn:41883/ai/count/backend/api/biz/image?path=material_1676483475272.jpg","fileSize":0,"fileName":"material_1676483475272.jpg","appid":null,"targetImage":"material_target_1676483475384.jpg","sourceImage":null},{"id":1594,"creator":"1595444549567979552","modifier":null,"gmtCreate":"2023-02-16 00:54:26","gmtModify":"2023-02-16 00:54:31","modelId":19,"materialName":"钢筋","quantity":0,"review":3,"model":"","spec":"","imageUrl":"material_1676480066684.jpg","rawImageUrl":"https://aiot.cscec8b.com.cn:41883/ai/count/backend/api/biz/image?path=material_1676480066684.jpg","fileSize":0,"fileName":"material_1676480066684.jpg","appid":null,"targetImage":"material_target_1676480066770.jpg","sourceImage":null},{"id":1592,"creator":"1595444549567979552","modifier":null,"gmtCreate":"2023-02-16 00:31:42","gmtModify":"2023-02-16 00:31:48","modelId":19,"materialName":"钢筋","quantity":0,"review":6,"model":"","spec":"","imageUrl":"material_1676478702588.jpg","rawImageUrl":"https://aiot.cscec8b.com.cn:41883/ai/count/backend/api/biz/image?path=material_1676478702588.jpg","fileSize":0,"fileName":"material_1676478702588.jpg","appid":null,"targetImage":"material_target_1676478702664.jpg","sourceImage":null},{"id":1591,"creator":"1595444549567979552","modifier":null,"gmtCreate":"2023-02-16 00:19:43","gmtModify":"2023-02-16 00:19:54","modelId":19,"materialName":"钢筋","quantity":0,"review":7,"model":"","spec":"","imageUrl":"material_1676477983768.jpg","rawImageUrl":"https://aiot.cscec8b.com.cn:41883/ai/count/backend/api/biz/image?path=material_1676477983768.jpg","fileSize":0,"fileName":"material_1676477983768.jpg","appid":null,"targetImage":"material_target_1676477983849.jpg","sourceImage":null},{"id":1590,"creator":"1595444549567979552","modifier":null,"gmtCreate":"2023-02-15 23:42:41","gmtModify":"2023-02-15 23:43:01","modelId":19,"materialName":"钢筋","quantity":0,"review":6,"model":"1","spec":"1","imageUrl":"material_1676475761708.jpg","rawImageUrl":"https://aiot.cscec8b.com.cn:41883/ai/count/backend/api/biz/image?path=material_1676475761708.jpg","fileSize":0,"fileName":"material_1676475761708.jpg","appid":null,"targetImage":"material_target_1676475761750.jpg","sourceImage":null},{"id":1585,"creator":"1595444549567979539","modifier":null,"gmtCreate":"2023-02-15 18:41:19","gmtModify":"2023-02-15 18:43:22","modelId":19,"materialName":"钢筋","quantity":15,"review":33,"model":"","spec":"","imageUrl":"material_1676457679710.jpg","rawImageUrl":"https://aiot.cscec8b.com.cn:41883/ai/count/backend/api/biz/image?path=material_1676457679710.jpg","fileSize":0,"fileName":"material_1676457679710.jpg","appid":null,"targetImage":"material_target_1676457679792.jpg","sourceImage":null},{"id":1351,"creator":"1595444549567979553","modifier":null,"gmtCreate":"2023-02-14 14:22:19","gmtModify":"2023-02-14 14:22:41","modelId":19,"materialName":"钢筋","quantity":140,"review":141,"model":"","spec":"","imageUrl":"material_1676355739772.jpg","rawImageUrl":"https://aiot.cscec8b.com.cn:41883/ai/count/backend/api/biz/image?path=material_1676355739772.jpg","fileSize":0,"fileName":"material_1676355739772.jpg","appid":null,"targetImage":"material_target_1676355739862.jpg","sourceImage":null},{"id":1349,"creator":"1595444549567979552","modifier":null,"gmtCreate":"2023-02-14 11:24:42","gmtModify":"2023-02-14 11:24:53","modelId":19,"materialName":"钢筋","quantity":0,"review":1,"model":"1","spec":"1","imageUrl":"material_1676345082090.jpg","rawImageUrl":"https://aiot.cscec8b.com.cn:41883/ai/count/backend/api/biz/image?path=material_1676345082090.jpg","fileSize":0,"fileName":"material_1676345082090.jpg","appid":null,"targetImage":"material_target_1676345082152.jpg","sourceImage":null},{"id":1345,"creator":"1595444549567979552","modifier":null,"gmtCreate":"2023-02-14 11:18:59","gmtModify":"2023-02-14 11:19:03","modelId":19,"materialName":"钢筋","quantity":1,"review":6,"model":"","spec":"","imageUrl":"material_1676344739899.jpg","rawImageUrl":"https://aiot.cscec8b.com.cn:41883/ai/count/backend/api/biz/image?path=material_1676344739899.jpg","fileSize":0,"fileName":"material_1676344739899.jpg","appid":null,"targetImage":"material_target_1676344739932.jpg","sourceImage":null},{"id":1344,"creator":"1595444549567979552","modifier":null,"gmtCreate":"2023-02-14 11:16:52","gmtModify":"2023-02-14 11:17:03","modelId":19,"materialName":"钢筋","quantity":1,"review":2,"model":"1","spec":"1","imageUrl":"material_1676344612594.jpg","rawImageUrl":"https://aiot.cscec8b.com.cn:41883/ai/count/backend/api/biz/image?path=material_1676344612594.jpg","fileSize":0,"fileName":"material_1676344612594.jpg","appid":null,"targetImage":"material_target_1676344612683.jpg","sourceImage":null},{"id":1338,"creator":"1595444549567979529","modifier":null,"gmtCreate":"2023-02-14 10:55:49","gmtModify":"2023-02-14 10:55:50","modelId":19,"materialName":"钢筋","quantity":1,"review":2,"model":"","spec":"","imageUrl":"material_1676343348999.jpg","rawImageUrl":"https://aiot.cscec8b.com.cn:41883/ai/count/backend/api/biz/image?path=material_1676343348999.jpg","fileSize":0,"fileName":"material_1676343348999.jpg","appid":null,"targetImage":"material_target_1676343349037.jpg","sourceImage":null},{"id":1314,"creator":"1595444549567979529","modifier":null,"gmtCreate":"2023-02-14 10:42:18","gmtModify":"2023-02-14 10:42:29","modelId":19,"materialName":"钢筋","quantity":74,"review":72,"model":"","spec":"","imageUrl":"material_1676342537967.jpg","rawImageUrl":"https://aiot.cscec8b.com.cn:41883/ai/count/backend/api/biz/image?path=material_1676342537967.jpg","fileSize":0,"fileName":"material_1676342537967.jpg","appid":null,"targetImage":"material_target_1676342538095.jpg","sourceImage":null},{"id":1308,"creator":"1595444549567979529","modifier":null,"gmtCreate":"2023-02-14 10:39:36","gmtModify":"2023-02-14 10:39:50","modelId":19,"materialName":"钢筋","quantity":84,"review":85,"model":"","spec":"","imageUrl":"material_1676342375892.jpg","rawImageUrl":"https://aiot.cscec8b.com.cn:41883/ai/count/backend/api/biz/image?path=material_1676342375892.jpg","fileSize":0,"fileName":"material_1676342375892.jpg","appid":null,"targetImage":"material_target_1676342375988.jpg","sourceImage":null},{"id":1307,"creator":"1595444549567979552","modifier":null,"gmtCreate":"2023-02-14 10:37:38","gmtModify":"2023-02-14 10:38:10","modelId":19,"materialName":"钢筋","quantity":0,"review":13,"model":"1","spec":"1","imageUrl":"material_1676342258600.jpg","rawImageUrl":"https://aiot.cscec8b.com.cn:41883/ai/count/backend/api/biz/image?path=material_1676342258600.jpg","fileSize":0,"fileName":"material_1676342258600.jpg","appid":null,"targetImage":"material_target_1676342258706.jpg","sourceImage":null}],"total":331,"size":16,"current":1,"orders":[],"optimizeCountSql":true,"searchCount":true,"countId":null,"maxLimit":null,"pages":21},"timestamp":1676963786656}
        private String[] fileNames= new String[]{"material_1676355739772.jpg","material_1676618340172.jpg","material_1676342258600.jpg", "material_1676618340172.jpg", "material_1676541995988.jpg"};
        String url = "https://aiot.cscec8b.com.cn:41883/ai/count/backend/api/biz/image?path=";
        String batchDownloadUrl = "https://aiot.cscec8b.com.cn:41883/ai/count/backend/api/biz/image/batchDownload?files=";

        public DownloadThread(){

        }
        @SneakyThrows
        @Override
        public void run(){
            int times =1;
            List<String> list = new ArrayList();
            while (true) {
                try{
                    var id = fileNames[times%fileNames.length];
                    list.add(id);
                    var request = new Request.Builder()
                            .url(url+ id)
                            .build();
                    var response = client.newCall(request).execute();
                    var bytes= response.body().bytes();
                    log.info("request:{}, response:code:{}，length:{}", request.url().url(), response.code(), bytes.length);
                    response.close();
                }
                catch(Exception err){
                    log.error("error:{}", err.getMessage());
                }
                times++;
                if(times%10 ==0){
                    try{
                        var request = new Request.Builder()
                                .url(batchDownloadUrl+ String.join(",", list))
                                .build();
                        var response = client.newCall(request).execute();
                        log.info("request:{}, response:code:{}，header:{}", request.url().url(), response.code(), response.headers());
                        response.close();
                    }
                    catch(Exception err){
                        log.error("error:{}", err.getMessage());
                    }
                }

            }
        }
    }

    @SneakyThrows
    private void set(ApiAuth auth){
        String url = "https://aiot.cscec8b.com.cn:41883/ai/count/backend/auth/account/save";

        var request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json;charset=utf-8"), JsonUtils.toJson(auth).getBytes()))
                .addHeader("x-access-token", token)
                .addHeader("accept", "application/json, text/plain, */*")
                .build();
        var response = client.newCall(request).execute();
        String json = response.body().string();
        log.info("json:{}", json);
    }

    @Data
    public static class ApiAuth{

        private Integer id;

        private String appid;

        private String name;

        private String secretKey;

        private Integer status;

    }
}
