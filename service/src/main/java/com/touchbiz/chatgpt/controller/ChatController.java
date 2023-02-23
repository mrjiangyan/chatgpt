package com.touchbiz.chatgpt.controller;


import com.theokanning.openai.completion.CompletionRequest;
import com.touchbiz.chatgpt.application.ChatApplicationService;
import com.touchbiz.chatgpt.boot.config.OpenAiConfig;
import com.touchbiz.chatgpt.common.aspect.annotation.RequestLimit;
import com.touchbiz.chatgpt.common.dto.Result;
import com.touchbiz.chatgpt.common.proxy.OpenAiEventStreamService;
import com.touchbiz.chatgpt.database.domain.ChatSessionDetail;
import com.touchbiz.chatgpt.dto.Chat;
import com.touchbiz.chatgpt.dto.ChatResult;
import com.touchbiz.chatgpt.dto.request.ValidChatRight;
import com.touchbiz.chatgpt.dto.response.ChatSessionDTO;
import com.touchbiz.chatgpt.infrastructure.constants.CommonConstant;
import com.touchbiz.chatgpt.infrastructure.converter.ChatSessionConverter;
import com.touchbiz.chatgpt.service.ChatSessionInfoService;
import com.touchbiz.common.entity.annotation.Auth;
import com.touchbiz.common.entity.result.MonoResult;
import com.touchbiz.common.utils.tools.JsonUtils;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static com.touchbiz.chatgpt.infrastructure.constants.CacheConstant.*;

@Slf4j
@RequestMapping("/api/chatGpt/chatting")
@RestController
public class ChatController extends AbstractBaseController<ChatSessionDetail, ChatSessionInfoService> {

    @Autowired
    private OpenAiConfig config;

    @Autowired
    private OpenAiEventStreamService service;

    @Autowired
    private ChatApplicationService chatApplicationService;

    private List<ChatResult> list = new ArrayList<>();

    @PostMapping
    @RequestLimit()
    public Mono<Result<?>> prompt(@RequestBody @Valid Chat chat) {
        String sessionId = chat.getSessionId();
        String prompt = chat.getPrompt();
        //sessionId校验合法性
        chatApplicationService.checkSessionId(sessionId);
        var user = getCurrentUser();
        log.info("chat:{}", chat);
        String redisKey = CHAT_SESSION_CONTEXT_KEY + sessionId;
        String question;
        //拼接提问
        if (getRedisTemplate().hasKey(redisKey)) {
            question = JsonUtils.toJson(getRedisTemplate().get(redisKey)).trim().replace("\\", "").replace("\"", "").replace("n", "\\n") + CommonConstant.SPLICER + prompt;
        } else {
            question = prompt;
        }
        log.info("question:{}", question);
        CompletionRequest completionRequest = generateRequest(prompt);
        try {
            long start = System.currentTimeMillis();
            var result = service.createCompletion(completionRequest);
            log.info("调用openAI接口耗时:{}", System.currentTimeMillis() - start + "ms");
            String rt = JsonUtils.toJson(result);
            ChatResult chatResult = JsonUtils.toObject(rt, ChatResult.class);
            log.info("result:{}", chatResult);
            if (!ObjectUtils.isEmpty(chatResult) && !CollectionUtils.isEmpty(chatResult.getChoices())) {
                String answerContent = chatResult.getChoices().get(0).getText();
                String answer = answerContent.trim().replace("\\", "");
                redisTemplate.set(CHAT_SESSION_CONTEXT_KEY + sessionId, question + answer, CHAT_SESSION_INFO_EXPIRE_SECONDS);
                chatApplicationService.createSessionInfo(chat, answerContent, user);
                return Mono.just(Result.ok(answerContent));
            }
        } catch (Exception ex) {
            log.error("error:", ex);
            return Mono.just(Result.error("系统超时,请联系管理员"));
        }
        return Mono.just(Result.error("请求失败，请重试"));
    }


    @SneakyThrows
    @GetMapping(value = "/completion", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Result<String>>> completion(@RequestParam("sessionId") String sessionId,
                                                            @RequestParam("prompt") String prompt) {
        if (sessionId.contains(" ")) {
            sessionId = sessionId.replace(" ", "+");
        }
        //sessionId校验合法性
        chatApplicationService.checkSessionId(sessionId);
        var user = getCurrentUser();
        //sessionId校验合法性
        chatApplicationService.checkSessionId(sessionId);

        String redisKey = CHAT_SESSION_CONTEXT_KEY + sessionId;
        String question;
        //拼接提问
        if (getRedisTemplate().hasKey(redisKey)) {
            question = JsonUtils.toJson(getRedisTemplate().get(redisKey)).trim().replace("\\", "").replace("\"", "").replace("n", "\\n") + CommonConstant.SPLICER + prompt;
        } else {
            question = prompt;
        }
        log.info("question:{}", question);
        var eventStream = service.createCompletionFlux(this.generateRequest(question));
        eventStream.doOnError(x -> log.error("doOnError SSE:", x));
        String finalSessionId = sessionId;
        eventStream.subscribe(consumer
                , error -> log.error("Error receiving SSE:", error),
                () -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    list.forEach(item -> {
                        List<ChatResult.Choice> choices = item.getChoices();
                        if (!CollectionUtils.isEmpty(choices)) {
                            String text = choices.get(0).getText();
                            if (!ObjectUtils.isEmpty(text)) {
                                stringBuilder.append(text);
                            }
                        }
                    });
                    String answerContent = stringBuilder.toString();
                    String answer = answerContent.replace("\\", "");
                    redisTemplate.set(CHAT_SESSION_CONTEXT_KEY + finalSessionId, question + answer, CHAT_SESSION_INFO_EXPIRE_SECONDS);
                    chatApplicationService.createSessionInfo(finalSessionId, prompt, answerContent, user);
                    list.clear();
                }
        );
        return eventStream.map(x -> {
                    Result<String> result = Result.ok();
                    result.setResult(x.data());
                    return ServerSentEvent.builder(result).build();
                }).subscribeOn(Schedulers.elastic());
    }


    private Consumer<ServerSentEvent<String>> consumer = content -> {
        String data = content.data();
        if ("[DONE]".equals(data)) {
            return;
        }
        ChatResult chatResult = JsonUtils.toObject(data, ChatResult.class);
        list.add(chatResult);
        log.info("Time: {} - event: name[{}], id [{}], content[{}] ",
                LocalTime.now(), content.event(), content.id(), data
        );
    };

    @Auth
    @ApiOperation("获取会话列表")
    @GetMapping
    public MonoResult<List<ChatSessionDTO>> getPageList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        var user = getCurrentUser();
        var result = chatApplicationService.getChatSessionPageList(pageNo, pageSize, user);
        var list = result.getRecords().stream().map(ChatSessionConverter.INSTANCE::transformOut)
                .toList();
        return MonoResult.ok(list);
    }

    @ApiOperation("新增会话id")
    @PostMapping("/session")
    public MonoResult<?> createSession() {
        var user = getCurrentUser();
        var session = chatApplicationService.createSession(user);
        //添加缓存
        String key = CHAT_SESSION_KEY + session.getSessionId();
        getRedisTemplate().setObject(key, session, CHAT_SESSION_EXPIRE_SECONDS);
        return MonoResult.OK(ChatSessionConverter.INSTANCE.transformOut(session));
    }

    /**
     * 判断是否允许进行聊天，如果没有相应的次数，则不能进行后续的聊天，并返回相应的提示内容
     *
     * @return
     */
    @PostMapping("/validRight")
    public MonoResult<Object> validChatRight(@RequestBody ValidChatRight validChatRight) {
        return MonoResult.ok("");
    }

    @Auth
    @ApiOperation(value = "删除会话")
    @DeleteMapping("/{id}")
    public MonoResult<?> delete(@PathVariable String id) {
        var user = getCurrentUser();
        chatApplicationService.deleteSession(id, user);
        return MonoResult.ok("删除成功！");
    }

    private CompletionRequest generateRequest(String prompt) {
        return CompletionRequest.builder()
                .prompt(prompt)
                .model(config.getModel())
                .stop(Arrays.asList(" Human:", " AI:"))
                .maxTokens(128)
                .presencePenalty(0.6d)
                .frequencyPenalty(0d)
                .temperature(0.9D)
                .bestOf(1)
                .topP(1d)
                .build();
    }
}
