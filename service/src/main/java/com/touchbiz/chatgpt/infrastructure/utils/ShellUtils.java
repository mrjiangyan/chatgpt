package com.touchbiz.chatgpt.infrastructure.utils;

import cn.hutool.core.util.ArrayUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiangyan
 */
@Slf4j
public class ShellUtils {

    private ShellUtils(){

    }

    @Data
    public static class ExecuteResult{

        private String output;

        private String errorOutput;

        private Boolean success;

    }

    public static ExecuteResult executeWithOut(String command){
        ExecuteResult result = new ExecuteResult();
        try {
            log.info("command:{}", command);
            Process process = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", command });
            // 0 表示线程正常终止。
            if (process.waitFor() == 0) {
                var bytes = process.getInputStream().readAllBytes();
                if(!ArrayUtil.isEmpty(bytes)){
                    result.setSuccess(true);
                    result.setOutput(new String(bytes));
                    log.info("Output-Message:{}", result.getOutput());
                }
                return result;
            }
            var bytes = process.getInputStream().readAllBytes();
            if(!ArrayUtil.isEmpty(bytes)){
                result.setOutput(new String(bytes));
                log.info("Output-Message:{}", result.getOutput());
            }
            bytes = process.getErrorStream().readAllBytes();
            if(!ArrayUtil.isEmpty(bytes)){
                var errorMessage = new String(bytes);
                log.error("Error-Message:{}", errorMessage);
                result.setErrorOutput(errorMessage);
            }
        } catch (Exception e) {
            log.error("", e);
            result.setErrorOutput(e.getMessage());
        }
        return result;
    }


    public static String execute(String command){
        try {
            log.info("command:{}", command);
            Process process = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", command });
            // 0 表示线程正常终止。
            if (process.waitFor() == 0) {
                return null;
            }
            var bytes = process.getInputStream().readAllBytes();
            if(!ArrayUtil.isEmpty(bytes)){
                log.info("Input-Message:{}", new String(bytes));
            }
            bytes = process.getErrorStream().readAllBytes();
            if(!ArrayUtil.isEmpty(bytes)){
                var errorMessage = new String(bytes);
                log.error("Error-Message:{}", errorMessage);
                return errorMessage;
            }
        } catch (Exception e) {
            log.error("", e);
            return e.getMessage();
        }
        return null;
    }

}

