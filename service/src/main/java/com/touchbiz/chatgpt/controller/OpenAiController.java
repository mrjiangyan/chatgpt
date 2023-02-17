package com.touchbiz.chatgpt.controller;

import com.touchbiz.chatgpt.boot.config.OpenAiConfig;
import com.touchbiz.chatgpt.common.proxy.OpenAiEventStreamService;
import com.touchbiz.common.entity.result.MonoResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/openai/")
@RestController
public class OpenAiController {

    @Autowired
    private OpenAiEventStreamService service;

    @GetMapping("models")
    public MonoResult<?> listModels(){
        return MonoResult.ok(service.listModels());
    }

    @GetMapping("files")
    public MonoResult<?> listFiles(){
        var file = service.uploadFile("warn.log", "/opt/logs/chatgpt-service/warn.log");
        log.info("response-file:{}", file);
        return MonoResult.ok(service.listFiles());
    }

    @GetMapping("fineTunes")
    public MonoResult<?> listFineTunes(){
        return MonoResult.ok(service.listFineTunes());
    }

}
