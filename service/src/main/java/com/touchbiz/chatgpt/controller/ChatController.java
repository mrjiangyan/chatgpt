package com.touchbiz.chatgpt.controller;

import com.theokanning.openai.completion.CompletionRequest;
import com.touchbiz.chatgpt.application.ChatService;
import com.touchbiz.chatgpt.boot.config.OpenAiConfig;
import com.touchbiz.chatgpt.common.dto.Result;
import com.touchbiz.chatgpt.common.proxy.OpenAiEventStreamService;
import com.touchbiz.chatgpt.database.domain.SysUser;
import com.touchbiz.chatgpt.dto.Chat;
import com.touchbiz.chatgpt.dto.request.ValidChatRight;
import com.touchbiz.common.entity.result.MonoResult;
import com.touchbiz.chatgpt.dto.ChatInfo;
import com.touchbiz.chatgpt.infrastructure.utils.RequestUtils;
import com.touchbiz.chatgpt.service.ISysUserService;
import com.touchbiz.common.entity.exception.BizException;
import com.touchbiz.common.utils.security.JwtUtil;
import com.touchbiz.common.utils.tools.JsonUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;


import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.*;

@Slf4j
@RequestMapping("/api/chatGpt/chatting")
@RestController
public class ChatController extends AbstractBaseController {

    @Autowired
    private OpenAiConfig config;

    @Autowired
    private OpenAiEventStreamService service;

    @Autowired
    private ChatService chatService;

    @PostMapping
    public Mono<Result<?>> prompt(@RequestBody Chat chat){
        log.info("chat:{}", chat);
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(chat.getPrompt())
                .model(config.getModel())
                .stop(Arrays.asList(" Human:"," AI:"))
                .maxTokens(512)
                .presencePenalty(0.6d)
                .frequencyPenalty(0d)
                .temperature(0.9D)
                .bestOf(1)
                .topP(1d)
                .build();
        var result = service.createCompletion(completionRequest);
        log.info("result:{}", JsonUtils.toJson(result));
        return Mono.just(Result.ok(result));
    }

    @ApiOperation("获取会话列表")
    @GetMapping
    public MonoResult<?> getPageList(HttpServletRequest request, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        var user = getUser();
        return MonoResult.ok(chatService.getPageList(pageNo, pageSize, user));
    }

    @ApiOperation("新增会话id")
    @PostMapping("/addSessionId")
    public MonoResult<?> addSessionId() {
        var user = getUser();
        return MonoResult.OK(chatService.addSessionId(user));
    }

    @ApiOperation("新增会话")
    @PostMapping("/add")
    public MonoResult<?> add(HttpServletRequest request, @RequestBody @Validated @NotNull ChatInfo chatInfo) {
        var user = getUser();
        chatService.add(chatInfo, user);
        return MonoResult.OK("新增成功！");
    }

    /**
     * 判断是否允许进行聊天，如果没有相应的次数，则不能进行后续的聊天，并返回相应的提示内容
     * @return
     */
    @PostMapping("/validRight")
    public MonoResult<Object> validChatRight(@RequestBody ValidChatRight validChatRight) {
        return MonoResult.ok("");
    }

    @ApiOperation(value = "删除会话")
    @DeleteMapping("/{id}")
    public MonoResult<?> delete(@PathVariable String id) {
        chatService.delete(id);
        return MonoResult.ok("删除成功！");
    }

}
