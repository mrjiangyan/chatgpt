package com.touchbiz.chatgpt.infrastructure.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.touchbiz.chatgpt.infrastructure.constants.CommonConstant.*;

/**
 * <p>Description : 生成签名工具类 </p>
 *
 * @author : xyf
 * @version : v1.0.0
 * @since : 2022/12/2 23:04
 **/

public class SignatureUtil {


    public static String generateSignature(Map<String, String> data, String appKey) {
        Set<String> keySet = data.keySet();
        String[] keyArray = keySet.toArray(new String[0]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (SIGN.equals(k)) {
                continue;
            }
            if (data.get(k) instanceof String) {
                // 参数值为空，则不参与签名
                if (data.get(k).trim().length() > 0) {
                    sb.append(k).append("=").append(data.get(k).trim()).append("&");
                }
            }


        }
        sb.append("key=").append(appKey);
        return getSHA256(sb.toString());
    }

    public static String getSHA256(String str) {
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }

    /**
     * 将byte转为16进制
     *
     * @param bytes
     * @return
     */
    private static String byte2Hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String temp = null;
        for (byte aByte : bytes) {
            temp = Integer.toHexString(aByte & 0xFF);
            if (temp.length() == 1) {
                // 1得到一位的进行补0操作
                sb.append("0");
            }
            sb.append(temp);
        }
        return sb.toString();
    }


    /**
     * // 签名生成方式
     * @param args
     */
    public static void main(String[] args) {

        String requestSign ="71905b7b254507047bb44753119d823c81b9ed4b11601267cdc2bb3e3d1cf1ee";

        //分配的appid
        String appid= "l9d58d177b05b93f6";
        // 时间戳
        String timestamp="1669992981815";
        String secretKey="52_YgwabmkB-_KU67QWD8UdQ29j_y72IjgNSXOhx0Bz6DfkQ6WhBrCqSix2bDmregNrsM5bN9IPFapTb6rtD_sgKYSHMOJyjxwtimdUzmS9z0roVYoJVWluHepKpP0avFr8QVWwuH0IbVE0Xtw6GNLdAJDXEC";
        String backUrl="www.baidu.com";
        Map<String,String> map=new HashMap<>();
        map.put(APPID,appid);
        map.put(TIMESTAMP,timestamp);
        map.put("allInfo",appid+timestamp);
        String sign= SignatureUtil.generateSignature(map,secretKey);

        if (requestSign.equals(sign)){
            //todo:验证成功
        }
        System.out.println("签名"+sign);
    }

    public static Boolean checkSignature(String appid,String timestamp,String secretKey,String requestSign){
        Map<String,String> map=new HashMap<>();
        map.put(APPID,appid);
        map.put(TIMESTAMP,timestamp);
        map.put("allInfo",appid+timestamp);
        String sign= SignatureUtil.generateSignature(map,secretKey);
        return sign.equals(requestSign);
    }


}
